package com.fileinfo.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String, Object> handleException(Exception exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode", 500);
        map.put("errorMsg", exception.toString());
        log.error("\n"
                +"---------------------------------"+Thread.currentThread().getName()+"出现全局异常----------------------------->"+"\n"
                + exception.toString()+"\n"
                +"<--------------------------------------------------------------------------");
        return map;
    }
}
