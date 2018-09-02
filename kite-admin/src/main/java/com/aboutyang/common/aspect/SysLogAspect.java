package com.aboutyang.common.aspect;

import com.aboutyang.common.annotation.SysLog;
import com.aboutyang.common.utils.HttpContextUtils;
import com.aboutyang.common.utils.IPUtils;
import com.aboutyang.common.utils.JsonUtils;
import com.aboutyang.modules.sys.entity.SysLogEntity;
import com.aboutyang.modules.sys.entity.SysUserEntity;
import com.aboutyang.modules.sys.service.SysLogService;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 系统日志，切面处理类
 */
@Aspect
@Component
public class SysLogAspect {
    private static final Logger log = LoggerFactory.getLogger(SysLogAspect.class);
    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private JsonUtils jsonUtils;

    @Pointcut("@annotation(com.aboutyang.common.annotation.SysLog)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        StopWatch stopWatch = new StopWatch("method monitor");
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        stopWatch.start(className + "." + methodName + "()");
        //执行方法
        Object result = point.proceed();

        stopWatch.stop();
        //执行时长(毫秒)
        long time = stopWatch.getTotalTimeMillis();

        stopWatch.start("method logging");
        //保存日志
        saveSysLog(point, time);

        stopWatch.stop();
        log.debug("method execute: {}", stopWatch.prettyPrint());

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SysLogEntity sysLog = new SysLogEntity();
        SysLog syslog = method.getAnnotation(SysLog.class);
        if (syslog != null) {
            //注解上的描述
            sysLog.setOperation(syslog.value());
        }

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        //请求的参数
        Object[] args = joinPoint.getArgs();
        try {
            String params = jsonUtils.toJson(args[0]);
            sysLog.setParams(params);
        } catch (Exception e) {

        }

        //获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //设置IP地址
        sysLog.setIp(IPUtils.getIpAddr(request));

        //用户名
        String username = ((SysUserEntity) SecurityUtils.getSubject().getPrincipal()).getUsername();
        sysLog.setUsername(username);

        sysLog.setTime(time);
        sysLog.setCreateDate(new Date());
        //保存系统日志
        sysLogService.asyncInsert(sysLog);
    }
}
