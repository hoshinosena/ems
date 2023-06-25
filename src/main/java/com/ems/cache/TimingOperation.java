package com.ems.cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class TimingOperation {
    Timer tokenTimer, cacheTimer;

    public TimingOperation(@Autowired TokenPool tokenPool,
                           @Autowired UserCache userCache,
                           @Value("${cleanCacheRelay}") long cleanCacheRelay,
                           @Value("${cleanTokenRelay}") long cleanTokenRelay,
                           @Value("${tokenLease}") long tokenLease,
                           @Value("${maxTokenLease}") long maxTokenLease) {
        tokenTimer = new Timer();
        tokenTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Map<String, Info> pool = tokenPool.pool;
                Iterator<Map.Entry<Integer, String>> iterator = tokenPool.bind.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, String> bind = iterator.next();
                    Info info = pool.get(bind.getValue());
                    if (info.startTime + maxTokenLease < System.currentTimeMillis()) {
                        pool.remove(bind.getValue());
                        iterator.remove();
                    } else if (info.lastTime + tokenLease < System.currentTimeMillis()) {
                        pool.remove(bind.getValue());
                        iterator.remove();
                    }
                }
            }
        }, 0, cleanTokenRelay);

        cacheTimer = new Timer();
        cacheTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                userCache.time = System.currentTimeMillis();
                userCache.cache = new ConcurrentHashMap<>();
            }
        }, 0, cleanCacheRelay);
    }
}
