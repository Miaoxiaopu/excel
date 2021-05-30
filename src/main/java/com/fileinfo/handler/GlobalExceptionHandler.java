package com.fileinfo.handler;

import com.fileinfo.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handleException(Exception exception) {
        log.error("\n---------------------------------{}----------------------------->\n{}"
                , Thread.currentThread().getName() + "出现全局异常", exception.toString() + "\n");
//        exception.printStackTrace();
        if (exception instanceof UnauthorizedException) {
            return Result.fail("没有权限!");
        } else if (exception instanceof UnknownAccountException) {
            return Result.fail("用户名错误!");
        } else if (exception instanceof AuthenticationException) {
            return Result.fail("密码错误!");
        }
        return Result.fail(exception.getMessage());
    }
}
