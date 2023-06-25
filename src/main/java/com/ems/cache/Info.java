package com.ems.cache;

import com.ems.po.User;

// 有效期30min
// 每提交一次请求自动更新lastTime续期
// 不得超过最长租期startTime
public class Info {
    public long startTime;
    public long lastTime;
    public User user;

    public Info(User user) {
        startTime = lastTime = System.currentTimeMillis();
        this.user = user;
    }
}
