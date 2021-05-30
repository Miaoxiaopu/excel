package com.fileinfo.interceptor;

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
public class AopForController {
    @Pointcut(value = "execution(* com.fileinfo.controller.*.*(..))")
    public void costTimePoint(){
    }
    @Around(value = "costTimePoint()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();
        long start = System.currentTimeMillis();
        log.info("------> [ {} ] 开始执行<------", signature.getName());
        Object result = pjp.proceed();
        long end = System.currentTimeMillis();
        log.info("------> [ {} ] 执行结束,共耗时{}ms<------", signature.getName(), end - start);
        return result;
    }
}
