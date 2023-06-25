package com.ems.controller.ElectricityPrice;

import com.ems.cache.Info;
import com.ems.cache.TokenPool;
import com.ems.po.ElectricityPrice;
import com.ems.po.User;
import com.ems.service.ElectricityPriceService;
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
    public ElectricityPrice eprice;
    public String select;
    public String number;
}

@Controller
@RequestMapping("${baseURL}" + "api/eprice")
public class ElectricityPriceAPI {
    @Value("${selectBlock}")
    int pageSize;
    @Autowired
    TokenPool tokenPool;
    @Autowired
    UserService userService;
    @Autowired
    ElectricityPriceService ePriceService;
    RateLimiter rateLimiter;

    public ElectricityPriceAPI(@Value("${APIRateLimit}") double limit) {
        rateLimiter = RateLimiter.create(limit);
    }

    @PostMapping("/get")
    @ResponseBody
    public Object getPrice(@RequestBody Para para) {
        Map<String, Object> objs = new LinkedHashMap<>();
        String token = para.token == null ? "" : para.token;
        String select = para.select == null ? "" : para.select;
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

        if (select.equals("latest") || !checkUser.getRole().equals("admin")) {
            objs.put("code", 0);
            objs.put("message", "success");
            objs.put("price", ePriceService.findPrice());
        } else {
            if (number.equals("")) {
                objs.put("code", 0);
                objs.put("message", "success");
                objs.put("prices", ePriceService.findPrices());
            } else {
                PageInfo<ElectricityPrice> pageInfo;
                try {
                    PageHelper.startPage(Integer.parseInt(number), pageSize);
                    List<ElectricityPrice> prices = ePriceService.findPrices();
                    pageInfo = new PageInfo<>(prices);
                } catch (Exception e) {
                    objs.put("code", 20002);
                    objs.put("message", "不正确的number");
                    return objs;
                }
                objs.put("code", 0);
                objs.put("message", "success");
                objs.put("page", pageInfo.getPageNum());
                objs.put("pages", pageInfo.getPages());
                objs.put("prices", pageInfo.getList());
            }
        }
        return objs;
    }

    @PostMapping("/add")
    @ResponseBody
    public Object addPrice(@RequestBody Para para) {
        Map<String, Object> objs = new LinkedHashMap<>();
        String token = para.token == null ? "" : para.token;
        ElectricityPrice electricityPrice = para.eprice;

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
            if (ePriceService.addPrice(electricityPrice) == 1) {
                objs.put("code", 0);
                objs.put("message", "success");
            } else {
                objs.put("code", 30003);
                objs.put("message", "eprice异常");
            }
        }
        return objs;
    }
}
