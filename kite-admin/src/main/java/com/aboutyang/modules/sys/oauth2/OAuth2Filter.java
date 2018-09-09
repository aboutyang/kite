package com.aboutyang.modules.sys.oauth2;

import com.aboutyang.common.utils.HttpContextUtils;
import com.aboutyang.common.utils.R;
import com.aboutyang.common.utils.SpringContextUtils;
import com.aboutyang.config.CacheConfig;
import com.aboutyang.modules.sys.entity.SysUserTokenEntity;
import com.aboutyang.modules.sys.service.ShiroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * oauth2过滤器
 */
public class OAuth2Filter extends AuthenticatingFilter {

    private static final Logger log = LoggerFactory.getLogger(OAuth2Filter.class);
    ObjectMapper mapper = new ObjectMapper();

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        //获取请求token
        String token = getRequestToken((HttpServletRequest) request);

        if (StringUtils.isBlank(token)) {
            return null;
        }

        return new OAuth2Token(token);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginRequest(request, response)) {
            return true;
        }

        if (((HttpServletRequest) request).getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;
        }
        return super.isAccessAllowed(request, response, mappedValue);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        //获取请求token，如果token不存在，直接返回401
        String token = getRequestToken((HttpServletRequest) request);
        if (StringUtils.isBlank(token)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, HttpContextUtils.getOrigin());
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

            R r = R.error(HttpStatus.UNAUTHORIZED.value(), "invalid token");
            String json = mapper.writeValueAsString(r);
            httpResponse.getWriter().write(json);
            httpResponse.getWriter().flush();
            return false;
        }

        return executeLogin(request, response);
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, HttpContextUtils.getOrigin());
        try {
            //处理登录失败的异常
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            R r = R.error(HttpStatus.UNAUTHORIZED.value(), throwable.getMessage());
            String json = mapper.writeValueAsString(r);

            httpResponse.getWriter().write(json);
            httpResponse.getWriter().flush();
        } catch (IOException e1) {
        }

        return false;
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        String accessToken = (String) token.getPrincipal();
        Date now = new Date();
        SysUserTokenEntity tokenEntity = SpringContextUtils.getBean(ShiroService.class).queryByToken(accessToken);
        long loginTime = now.getTime() - tokenEntity.getCreateTime().getTime();
        if (loginTime > 1000 * 60 * 60) {
            String refreshToken = TokenGenerator.generateValue();
            Cache.ValueWrapper valueWrapper = SpringContextUtils.getBean(CacheManager.class).getCache(CacheConfig.TOKEN_CACHE).putIfAbsent("refresh_" + token, refreshToken);
            if (valueWrapper != null && valueWrapper.get() != null) {
                refreshToken = (String) valueWrapper.get();
            } else {// touch token
                tokenEntity.setExpireTime(DateUtils.addMinutes(now, 3));
                tokenEntity.setRefreshToken(refreshToken);
                tokenEntity.setAvailable(false);
                SysUserTokenEntity newTokenEntity = new SysUserTokenEntity();
                newTokenEntity.setToken(refreshToken);
                newTokenEntity.setUserId(tokenEntity.getUserId());
                newTokenEntity.setUsername(tokenEntity.getUsername());
                newTokenEntity.setCreateTime(now);
                newTokenEntity.setLoginTime(tokenEntity.getLoginTime());
                newTokenEntity.setUpdateTime(now);
                Date expireTime = new Date(now.getTime() + tokenEntity.getExpireTime().getTime() - tokenEntity.getCreateTime().getTime());
                newTokenEntity.setExpireTime(expireTime);
                newTokenEntity.setAvailable(true);

                SpringContextUtils.getBean(ShiroService.class).refreshToken(tokenEntity);
                SpringContextUtils.getBean(ShiroService.class).saveUpdateToken(newTokenEntity);
            }
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("refreshToken", refreshToken);
            log.info("刷新token:{}", refreshToken);
        }

        return super.onLoginSuccess(token, subject, request, response);
    }

    /**
     * 获取请求的token
     */
    private String getRequestToken(HttpServletRequest httpRequest) {
        //从header中获取token
        String token = httpRequest.getHeader("token");
        //如果header中不存在token，则从参数中获取token
        if (StringUtils.isBlank(token)) {
            token = httpRequest.getParameter("token");
        }
        return token;
    }


}
