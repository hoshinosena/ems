package com.ems.controller.Record;

import com.ems.cache.Info;
import com.ems.cache.TokenPool;
import com.ems.po.User;
import com.ems.po.Record;
import com.ems.service.ElectricityPriceService;
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
    public Record record;
    public String number;
}

@Controller
@RequestMapping("${baseURL}" + "api/record")
public class RecordAPI {
    @Value("${selectBlock}")
    int pageSize;
    @Autowired
    TokenPool tokenPool;
    @Autowired
    UserService userService;
    @Autowired
    RecordService recordService;
    @Autowired
    ElectricityPriceService ePriceService;
    RateLimiter rateLimiter;

    public RecordAPI(@Value("${APIRateLimit}") double limit) {
        rateLimiter = RateLimiter.create(limit);
    }

    @PostMapping("/get")
    @ResponseBody
    public Object getRecord(@RequestBody Para para) {
        Map<String, Object> objs = new LinkedHashMap<>();
        String token = para.token == null ? "" : para.token;
        String uid = para.uid == null ? "" : para.uid;
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

        // 不带number参数或者为空时将全表查询
        // 带number参数则返回页码数据
        if (checkUser.getRole().equals("admin")){
            if (uid.equals("")) {
                if (number.equals("")) {
                    objs.put("code", 0);
                    objs.put("message", "success");
                    objs.put("records", recordService.findRecords());
                } else {
                    PageInfo<Record> pageInfo;
                    try {
                        PageHelper.startPage(Integer.parseInt(number), pageSize);
                        List<Record> records = recordService.findRecords();
                        pageInfo = new PageInfo<>(records);
                    } catch (Exception e) {
                        objs.put("code", 20002);
                        objs.put("message", "不正确的number");
                        return objs;
                    }
                    objs.put("code", 0);
                    objs.put("message", "success");
                    objs.put("page", pageInfo.getPageNum());
                    objs.put("pages", pageInfo.getPages());
                    objs.put("records", pageInfo.getList());
                }
            } else {
                int checkUid;
                try {
                    checkUid = Integer.parseInt(uid);
                } catch(Exception e) {
                    objs.put("code", 10002);
                    objs.put("message", "不正确的uid");
                    return objs;
                }
                if (userService.findUser(checkUid) == null) {
                    objs.put("code", 10002);
                    objs.put("message", "不正确的uid");
                } else {
                    if (number.equals("")) {
                        objs.put("code", 0);
                        objs.put("message", "success");
                        objs.put("records", recordService.findRecords(checkUid));
                    } else {
                        PageInfo<Record> pageInfo;
                        try {
                            PageHelper.startPage(Integer.parseInt(number), pageSize);
                            List<Record> records = recordService.findRecords(checkUid);
                            pageInfo = new PageInfo<>(records);
                        } catch (Exception e) {
                            objs.put("code", 20002);
                            objs.put("message", "不正确的number");
                            return objs;
                        }
                        objs.put("code", 0);
                        objs.put("message", "success");
                        objs.put("page", pageInfo.getPageNum());
                        objs.put("pages", pageInfo.getPages());
                        objs.put("records", pageInfo.getList());
                    }
                }
            }
        } else {
            // 多表查询
            // 于1.2.1废弃
//            if (checkUser.getRecords().size() <= pageSize) {
//                objs.put("code", 0);
//                objs.put("message", "success");
//                objs.put("page", 1);
//                objs.put("pages", 1);
//                objs.put("records", checkUser.getRecords());
//            }
//            else {
//            }
            if (number.equals("")) {
                objs.put("code", 0);
                objs.put("message", "success");
                objs.put("records", recordService.findRecords(checkUser.getUid()));
            } else {
                PageInfo<Record> pageInfo;
                try {
                    PageHelper.startPage(Integer.parseInt(number), pageSize);
                    List<Record> records = recordService.findRecords(checkUser.getUid());
                    pageInfo = new PageInfo<>(records);
                } catch (Exception e) {
                    objs.put("code", 20002);
                    objs.put("message", "不正确的number");
                    return objs;
                }
                objs.put("code", 0);
                objs.put("message", "success");
                objs.put("page", pageInfo.getPageNum());
                objs.put("pages", pageInfo.getPages());
                objs.put("records", pageInfo.getList());
            }
        }
        return objs;
    }

    @PostMapping("/add")
    @ResponseBody
    public Object addRecord(@RequestBody Para para) {
        Map<String, Object> objs = new LinkedHashMap<>();
        String token = para.token == null ? "" : para.token;
        Record record = para.record;

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

        if (record.getChanges() == 0D) {
            objs.put("code", 20003);
            objs.put("message", "record异常");
        } else if (checkUser.getRole().equals("admin")) {
            User updUser = userService.findUser(record.getUid());
            if (updUser == null) {
                objs.put("code", 10002);
                objs.put("message", "不正确的uid");
                return objs;
            }
            double fee = ePriceService.findPrice().getPrice() * record.getChanges();
            double money = updUser.getMoney() - fee;
            updUser.setRole(null);
            updUser.setPassword(null);
            updUser.setName(null);
            updUser.setMoney(money);
            updUser.setTel(null);
            updUser.setAddress(null);
            updUser.setInfo(null);
            userService.updUser(updUser);
            // 电费录入
            record.setOp(2);
            record.setUid(updUser.getUid());
            record.setChanges(fee);
            // 上面的updUser已经清除cache
            recordService.addRecord(record);
            objs.put("code", 0);
            objs.put("message", "success");
        } else {
            User updUser = userService.findUser(checkUser.getUid());
            double money = updUser.getMoney() + record.getChanges();
            updUser.setRole(null);
            updUser.setPassword(null);
            updUser.setName(null);
            updUser.setMoney(money);
            updUser.setTel(null);
            updUser.setAddress(null);
            updUser.setInfo(null);
            userService.updUser(updUser);
            // 用户充值
            record.setOp(1);
            record.setUid(updUser.getUid());
            // 上面的updUser已经清除cache
            recordService.addRecord(record);
            objs.put("code", 0);
            objs.put("message", "success");
        }
        return objs;
    }
}
