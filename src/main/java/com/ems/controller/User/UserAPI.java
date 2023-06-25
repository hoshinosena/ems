package com.ems.controller.User;

import com.ems.cache.Info;
import com.ems.cache.TokenPool;
import com.ems.po.User;
import com.ems.po.Record;
import com.ems.service.RecordService;
import com.ems.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Para {
    public String token;
    public String uid;
    public String name;
    public String money;
    public String info;
    public User user;
    public String number;
}

@Controller
@RequestMapping("${baseURL}" + "api/user")
public class UserAPI {
    @Value("${selectBlock}")
    int pageSize;
    @Autowired
    TokenPool tokenPool;
    @Autowired
    UserService userService;
    @Autowired
    RecordService recordService;
    RateLimiter rateLimiter;

    public UserAPI(@Value("${APIRateLimit}") double limit) {
        rateLimiter = RateLimiter.create(limit);
    }

    @PostMapping("/get")
    @ResponseBody
    public Object getUser(@RequestBody Para para) {
        Map<String, Object> objs = new LinkedHashMap<>();
        String token = para.token == null ? "" : para.token;
        String uid = para.uid == null ? "" : para.uid;
        String name = para.name == null ? "" : para.name;
        String money = para.money == null ? "" : para.money;
        String number = para.number == null ? "" : para.number;

        if (!rateLimiter.tryAcquire()) {
            objs.put("code", 60001);
            objs.put("message", "请求过载");
            return  objs;
        }

        Info info = tokenPool.pool.get(token);
        if (info == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作");
            return  objs;
        }
        info.lastTime = System.currentTimeMillis(); // Token续期
        User checkUser = info.user;

        // uid或非管理员查询返回key:user
        // 其余均为users
        // 不带number参数或者为空时将全表查询
        // 带number参数则返回页码数据
        if (checkUser.getRole().equals("admin")) {
            if(money.equals("") && name.equals("")) {
                if (uid.equals("")) {
                    if (number.equals("")) {
                        objs.put("code", 0);
                        objs.put("message", "success");
                        objs.put("users", userService.findUsers());
                    } else {
                        PageInfo<User> pageInfo;
                        try {
                            PageHelper.startPage(Integer.parseInt(number), pageSize);
                            List<User> users = userService.findUsers();
                            pageInfo = new PageInfo<>(users);
                        } catch (Exception e) {
                            objs.put("code", 20002);
                            objs.put("message", "不正确的number");
                            return objs;
                        }
                        objs.put("code", 0);
                        objs.put("message", "success");
                        objs.put("page", pageInfo.getPageNum());
                        objs.put("pages", pageInfo.getPages());
                        objs.put("users", pageInfo.getList());
                    }
                } else {
                    int checkUid;
                    try {
                        checkUid = Integer.parseInt(uid);
                    } catch (Exception e) {
                        objs.put("code", 10002);
                        objs.put("message", "不正确的uid");
                        return objs;
                    }
                    objs.put("code", 0);
                    objs.put("message", "success");
                    objs.put("user", userService.findUser(checkUid));
                }
            } else if (name.equals("")){
                double checkMoney;
                try {
                    checkMoney = Double.parseDouble(money);
                } catch (Exception e) {
                    objs.put("code", 10002);
                    objs.put("message", "不正确的money");
                    return objs;
                }
                if (number.equals("")) {
                    objs.put("code", 0);
                    objs.put("message", "success");
                    objs.put("users", userService.findUsers(checkMoney));
                } else {
                    PageInfo<User> pageInfo;
                    try {
                        PageHelper.startPage(Integer.parseInt(number), pageSize);
                        List<User> users = userService.findUsers(checkMoney);
                        pageInfo = new PageInfo<>(users);
                    } catch (Exception e) {
                        objs.put("code", 20002);
                        objs.put("message", "不正确的number");
                        return objs;
                    }
                    objs.put("code", 0);
                    objs.put("message", "success");
                    objs.put("page", pageInfo.getPageNum());
                    objs.put("pages", pageInfo.getPages());
                    objs.put("users", pageInfo.getList());
                }
            } else {
                if (number.equals("")) {
                    objs.put("code", 0);
                    objs.put("message", "success");
                    objs.put("users", userService.findUsers(name));
                } else {
                    PageInfo<User> pageInfo;
                    try {
                        PageHelper.startPage(Integer.parseInt(number), pageSize);
                        List<User> users = userService.findUsers(name);
                        pageInfo = new PageInfo<>(users);
                    } catch (Exception e) {
                        objs.put("code", 20002);
                        objs.put("message", "不正确的number");
                        return objs;
                    }
                    objs.put("code", 0);
                    objs.put("message", "success");
                    objs.put("page", pageInfo.getPageNum());
                    objs.put("pages", pageInfo.getPages());
                    objs.put("users", pageInfo.getList());
                }
            }
        } else {
            objs.put("code", 0);
            objs.put("message", "success");
            objs.put("user", checkUser);
        }
        return objs;
    }

    @PostMapping("/add")
    @ResponseBody
    public Object addUser(@RequestBody Para para) {
        Map<String, Object> objs = new LinkedHashMap<>();
        String token = para.token == null ? "" : para.token;

        if (!rateLimiter.tryAcquire()) {
            objs.put("code", 60001);
            objs.put("message", "请求过载");
            return  objs;
        }

        Info info = tokenPool.pool.get(token);
        if (info == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作");
            return  objs;
        }
        info.lastTime = System.currentTimeMillis(); // Token续期
        User checkUser = info.user;

        User user = para.user;
        if (!checkUser.getRole().equals("admin")) {
            objs.put("code", 10001);
            objs.put("message", "非法操作");
        } else {
            // 初始化
            // 确保只能透过update修改金额
            // 这样record表中的记录能够对上账目
            user.setMoney(0D);
            if (user.getRole() == null || !user.getRole().equals("admin")) {
                user.setRole("user");
            }
            if (userService.addUser(user) == 1) {
                objs.put("code", 0);
                objs.put("message", "success");
            } else {
                objs.put("code", 10003);
                objs.put("message", "user异常");
            }
        }
        return objs;
    }

    @PostMapping("/update")
    @ResponseBody
    public Object updateUser(@RequestBody Para para) {
        Map<String, Object> objs = new LinkedHashMap<>();
        String token = para.token == null ? "" : para.token;
        User updUser = para.user;
        String info = para.info == null ? "" : para.info;

        if (!rateLimiter.tryAcquire()) {
            objs.put("code", 60001);
            objs.put("message", "请求过载");
            return  objs;
        }

        Info _info = tokenPool.pool.get(token);
        if (_info == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作");
            return  objs;
        }
        _info.lastTime = System.currentTimeMillis(); // Token续期
        User checkUser = _info.user;

        if (checkUser.getRole().equals("admin")) {
            Double money;
            User tempUser = userService.findUser(updUser.getUid());
            if (tempUser == null) {
                objs.put("code", 10002);
                objs.put("message", "不正确的uid");
                return objs;
            } else if (checkUser.getUid() == tempUser.getUid() && !"admin".equals(updUser.getRole())) {
                // 管理员不能自降身份
                objs.put("code", 10004);
                objs.put("message", "操作异常");
                return objs;
            }
            money = tempUser.getMoney();
            if (updUser.getRole() == null || !updUser.getRole().equals("admin")) {
                updUser.setRole("user");
            }
            if (userService.updUser(updUser) == 1) {
                if (updUser.getMoney() != null && !money.equals(updUser.getMoney())){
                    Record addRecord = new Record();
                    // 管理员修改电费余额
                    addRecord.setOp(0);
                    addRecord.setChanges(updUser.getMoney());
                    addRecord.setInfo(checkUser.getName()  + "(uid:" + checkUser.getUid() + " ) " + info);
                    addRecord.setUid(updUser.getUid());
                    // 上面的updUser已经清除cache
                    recordService.addRecord(addRecord);
                }
            }  else {
                objs.put("code", 10003);
                objs.put("message", "user异常");
                return objs;
            }
        } else {
            updUser.setRole("user");
            updUser.setMoney(null);
            if (userService.updUser(updUser) != 1) {
                objs.put("code", 10003);
                objs.put("message", "user异常");
                return objs;
            }
        }
        objs.put("code", 0);
        objs.put("message", "success");
        return objs;
    }

    @PostMapping("/delete")
    @ResponseBody
    public Object deleteUser(@RequestBody Para para) {
        Map<String, Object> objs = new LinkedHashMap<>();
        String token = para.token == null ? "" : para.token;
        String uid = para.uid == null ? "" : para.uid;

        if (!rateLimiter.tryAcquire()) {
            objs.put("code", 60001);
            objs.put("message", "请求过载");
            return  objs;
        }

        Info info = tokenPool.pool.get(token);
        if (info == null) {
            objs.put("code", 10001);
            objs.put("message", "非法操作");
            return  objs;
        }
        info.lastTime = System.currentTimeMillis(); // Token续期
        User checkUser = info.user;

        if (!checkUser.getRole().equals("admin")) {
            objs.put("code", 10001);
            objs.put("message", "非法操作");
        } else {
            int checkUid;
            try {
                checkUid = Integer.parseInt(uid);
            } catch (Exception e) {
                objs.put("code", 10002);
                objs.put("message", "不正确的uid");
                return objs;
            }
            if (checkUser.getUid() == checkUid) {
                // 管理员不能删除自己
                objs.put("code", 10004);
                objs.put("message", "操作异常");
            } else {
                if (userService.findUser(checkUid) == null) {
                    objs.put("code", 10002);
                    objs.put("message", "不正确的uid");
                } else {
                    recordService.delRecord(checkUid);
                    userService.delUser(checkUid);
                    objs.put("code", 0);
                    objs.put("message", "success");
                }
            }
        }
        return objs;
    }
}
