package com.ems.controller.Config;

import com.ems.cache.TokenPool;
import com.ems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${baseURL}")
    String baseURL;
    @Autowired
    UserService userService;
    @Autowired
    TokenPool tokenCache;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new Interceptor(baseURL)).addPathPatterns("/**");
    }
}
