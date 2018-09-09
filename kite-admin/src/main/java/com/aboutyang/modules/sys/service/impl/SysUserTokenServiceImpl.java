package com.aboutyang.modules.sys.service.impl;

import com.aboutyang.common.utils.Constant;
import com.aboutyang.common.utils.PageUtils;
import com.aboutyang.common.utils.Query;
import com.aboutyang.common.utils.R;
import com.aboutyang.modules.sys.dao.SysUserTokenDao;
import com.aboutyang.modules.sys.entity.SysUserEntity;
import com.aboutyang.modules.sys.entity.SysUserTokenEntity;
import com.aboutyang.modules.sys.oauth2.TokenGenerator;
import com.aboutyang.modules.sys.service.SysUserTokenService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.aboutyang.config.CacheConfig.TOKEN_CACHE;


@Service("sysUserTokenService")
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenDao, SysUserTokenEntity> implements SysUserTokenService {
    //12小时后过期
    private final static int EXPIRE = 3600 * 12;

    @Override
    public R createToken(SysUserEntity user) {
        //生成一个token
        String token = TokenGenerator.generateValue();

        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + EXPIRE * 1000);

        //判断是否生成过token
        SysUserTokenEntity tokenEntity = this.selectById(user.getUserId());
        if (tokenEntity == null) {
            tokenEntity = new SysUserTokenEntity();
            tokenEntity.setUserId(user.getUserId());
            tokenEntity.setUsername(user.getUsername());
            tokenEntity.setToken(token);
            tokenEntity.setUpdateTime(now);
            tokenEntity.setExpireTime(expireTime);
            tokenEntity.setCreateTime(now);
            tokenEntity.setLoginTime(now);
            tokenEntity.setAvailable(true);

            //保存token
            this.insert(tokenEntity);
        } else {
            tokenEntity.setToken(token);
            tokenEntity.setUpdateTime(now);
            tokenEntity.setExpireTime(expireTime);

            //更新token
            this.updateById(tokenEntity);
        }

        R r = R.ok().put("token", token).put("expire", EXPIRE);

        return r;
    }

    @Override
    public void logout(long userId) {
        //生成一个token
        String token = TokenGenerator.generateValue();

        //修改token
        SysUserTokenEntity tokenEntity = new SysUserTokenEntity();
        tokenEntity.setUserId(userId);
        tokenEntity.setToken(token);
        this.updateById(tokenEntity);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String username = (String) params.get("username");
        Page<SysUserTokenEntity> page = this.selectPage(
                new Query<SysUserTokenEntity>(params).getPage(),
                new EntityWrapper<SysUserTokenEntity>()
                        .eq("available", true)
                        .like(StringUtils.isNotBlank(username), "username", username)
        );
        return new PageUtils(page);
    }

    @CacheEvict(value = TOKEN_CACHE, key = "#token")
    @Override
    public void forceLogout(String token) {
        this.baseMapper.forceLogout(token);
    }

}
