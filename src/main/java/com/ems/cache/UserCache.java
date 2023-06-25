package com.ems.cache;

import com.ems.po.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// User专用缓存
// 在更新和删除用户时移除缓存
// 充值和消费提交成功后只需刷新缓存中money即可
// 此缓存针对操作记录业务中(selectUserByUid)
// 大量查询用户详细信息做优化
@Component
public class UserCache {
    public long time;
    public Map<Integer, User> cache;

    public UserCache() {
        time = System.currentTimeMillis();
        cache = new ConcurrentHashMap<>();
    }
}
