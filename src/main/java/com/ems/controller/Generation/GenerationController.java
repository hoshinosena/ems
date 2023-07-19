package com.ems.controller.Generation;

import com.ems.cache.Info;
import com.ems.cache.TokenPool;
import com.ems.po.User;
import com.ems.service.UserService;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("${baseURL}")
public class GenerationController {
    @Value("${baseURL}")
    String baseURL;
    @Autowired
    UserService userService;
    @Autowired
    TokenPool tokenPool;
    RateLimiter rateLimiter;

    public GenerationController(@Value("${AccessControlLimit}") double limit) {
        rateLimiter = RateLimiter.create(limit);
    }

    @RequestMapping("/")
    public String toIndex() {
        return "redirect:" + baseURL + "index.html";
    }

    @RequestMapping("/index.html")
    public String index(HttpServletRequest req,
                        HttpServletResponse rsp) {
        if (!rateLimiter.tryAcquire()) {
            rsp.setStatus(403);
            return "error/403";
        }

        Object isLogin = req.getSession().getAttribute("isLogin");
        if (isLogin != null && isLogin.equals(true)) {
            if ((boolean)req.getSession().getAttribute("op")) {
                return "redirect:" + baseURL + "admin.html";
            }
            return "redirect:" + baseURL + "user.html";
        }
        rsp.addCookie(new Cookie("baseURL", baseURL));
        return "index";
    }

    @PostMapping("/to_login")
    @ResponseBody
    public Object toLogin(@RequestBody Para para,
                          HttpServletRequest req,
                          HttpServletResponse rsp) {
        String username = para.username == null ? "" : para.username;
        String password = para.password == null ? "" : para.password;
        Map<String, Object> objs = new LinkedHashMap<>();

        if (!rateLimiter.tryAcquire()) {
            objs.put("code", 60001);
            objs.put("message", "请求过载");
            return  objs;
        }

        User checkUser = userService.checkUsername(username);
        if (checkUser == null || !checkUser.getPassword().equals(password)) {
            objs.put("code", 10001);
            objs.put("message", "非法操作");
        } else {
            String token = UUID.randomUUID().toString();
            synchronized (this) {
                if (tokenPool.bind.containsKey(checkUser.getUid())) {  // 刷新token
                    tokenPool.pool.remove(tokenPool.bind.get(checkUser.getUid()));
                }
                tokenPool.bind.put(checkUser.getUid(), token);
                tokenPool.pool.put(token, new Info(checkUser));
            }
            req.getSession().setAttribute("isLogin", true);
            rsp.addCookie(new Cookie("username", username));
            rsp.addCookie(new Cookie("token", token));
            rsp.addCookie(new Cookie("uid", checkUser.getUid() + ""));
            rsp.addCookie(new Cookie("name", checkUser.getName()));
            rsp.addCookie(new Cookie("money", checkUser.getMoney().toString()));
            rsp.addCookie(new Cookie("tel", checkUser.getTel()));
            rsp.addCookie(new Cookie("address", checkUser.getAddress()));
            rsp.addCookie(new Cookie("info", checkUser.getInfo()));
            objs.put("code", 0);
            objs.put("message", "success");
            if (checkUser.getRole().equals("admin")) {
                req.getSession().setAttribute("op", true);
                objs.put("url", "admin.html");
            } else {
                req.getSession().setAttribute("op", false);
                objs.put("url", "user.html");
            }
        }
        return objs;
    }

    @PostMapping("/to_sign")
    @ResponseBody
    public Object toSign(@RequestBody Para para,
                         HttpServletRequest req,
                         HttpServletResponse rsp) {
        String username = para.username == null ? "" : para.username;
        String password = para.password == null ? "" : para.password;
        Map<String, Object> objs = new LinkedHashMap<>();

        if (!rateLimiter.tryAcquire()) {
            objs.put("code", 60001);
            objs.put("message", "请求过载");
            return  objs;
        }

        User addUser = new User();
        addUser.setRole("user");
        addUser.setUsername(username);
        addUser.setPassword(password);
        addUser.setMoney(0D);
        addUser.setName("新用户");
        if (userService.addUser(addUser) == 1) {
            int uid = userService.checkUsername(username).getUid();
            String token = UUID.randomUUID().toString();
            tokenPool.bind.put(uid, token);
            tokenPool.pool.put(token, new Info(addUser));
            req.getSession().setAttribute("isLogin", true);
            req.getSession().setAttribute("op", false);
            rsp.addCookie(new Cookie("username", username));
            rsp.addCookie(new Cookie("token", token));
            rsp.addCookie(new Cookie("uid", uid + ""));
            rsp.addCookie(new Cookie("name", addUser.getName()));
            rsp.addCookie(new Cookie("money", "0"));
            rsp.addCookie(new Cookie("tel", ""));
            rsp.addCookie(new Cookie("address", ""));
            rsp.addCookie(new Cookie("info", ""));
            objs.put("code", 0);
            objs.put("message", "success");
            objs.put("url", "user.html");
        } else {
            objs.put("code", 10003);
            objs.put("message", "user异常");
        }
        return objs;
    }

    @RequestMapping({"/to_logout"})
    @ResponseBody
    public void toLogout(HttpServletRequest req) {
        if (!rateLimiter.tryAcquire()) {
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute("isLogin");
        session.removeAttribute("op");
    }

    // 获取和刷新token
    @RequestMapping("/api/get_token")
    @ResponseBody
    public Object getToken(@RequestBody Para para) {
        String username = para.username == null ? "" : para.username;
        String password = para.password == null ? "" : para.password;
        Map<String, Object> objs = new LinkedHashMap<>();

        if (!rateLimiter.tryAcquire()) {
            objs.put("code", 60001);
            objs.put("message", "请求过载");
            return  objs;
        }

        User checkUser = userService.checkUsername(username);
        if (checkUser == null || !checkUser.getPassword().equals(password)) {
            objs.put("code", 10001);
            objs.put("message", "非法操作");
        } else {
            String token = UUID.randomUUID().toString();
            synchronized (this) {
                if (tokenPool.bind.containsKey(checkUser.getUid())) { // 刷新token
                    tokenPool.pool.remove(tokenPool.bind.get(checkUser.getUid()));
                }
                tokenPool.bind.put(checkUser.getUid(), token);
                tokenPool.pool.put(token, new Info(checkUser));
            }
            objs.put("code", 0);
            objs.put("message", "success");
            objs.put("token", token);
        }
        return objs;
    }
}