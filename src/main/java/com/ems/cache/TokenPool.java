package com.ems.cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenPool {
    public long startTime;
    // uid ==> token
    public Map<Integer, String> bind;
    // token ==> info ==> user
    public Map<String, Info> pool;

    public TokenPool() {
        startTime = System.currentTimeMillis();
        bind = new ConcurrentHashMap<>();
        pool = new ConcurrentHashMap<>();
    }
}

