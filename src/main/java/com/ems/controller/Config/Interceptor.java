package com.ems.controller.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

public class Interceptor implements HandlerInterceptor {
    String baseURL;
    String URL_FIX;

    public Interceptor(String baseURL) {
        this.baseURL = baseURL;
        this.URL_FIX = baseURL.substring(0, baseURL.length() - 1);
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse rsp, Object handler) throws IOException {
        if (URL_FIX.equals(req.getRequestURI())) {
            rsp.setStatus(302);
            rsp.sendRedirect(baseURL);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse rsp, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse rsp, Object handler, @Nullable Exception ex) throws Exception {
    }
}
