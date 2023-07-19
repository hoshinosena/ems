package com.ems.controller.Error;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandle {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object exceptionHandle(Exception e){
        System.out.println(e.getMessage());
        Map<String ,Object> objs = new LinkedHashMap<>();
        objs.put("code", 50005);
        objs.put("message", "未知错误");
        return objs;
    }
}
