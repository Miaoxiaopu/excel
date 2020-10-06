package com.fileinfo.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AOPConfig {
    @Pointcut(value = "execution(* com.fileinfo.controller.*.*(..))")
    public void costTimePoint(){
    }
    @Around(value = "costTimePoint()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();
        long start = System.currentTimeMillis();
        log.info("------>" + signature.getName() + "开始执行<------");
        Object result = pjp.proceed();
        long end = System.currentTimeMillis();
        log.info("------>" + signature.getName() + "执行结束,共耗时"+ (end-start)/1000+"s<------");
        return result;
    }
}
