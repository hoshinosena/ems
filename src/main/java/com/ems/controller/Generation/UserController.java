package com.ems.controller.Generation;

import com.ems.cache.Info;
import com.ems.cache.TokenPool;
import com.ems.service.UserService;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("${baseURL}")
public class UserController {
    @Value("${baseURL}")
    String baseURL;
    @Autowired
    TokenPool tokenPool;
    @Autowired
    UserService userService;

    // OS Info
    final int OS_X_X64 = 1;
    final int OS_X_AArch64 = 2;
    final int OS_X_FIX = 3;
    final int Other = 4;
    int OperatingSystem;

    // OS X
    int numCPUs;
    long totalMEM;
    long[] CPUInfo;
    long[] MEMInfo;

    // Other
    CentralProcessor processor;
    GlobalMemory memory;

    static long startTime;
    RateLimiter rateLimiter;

    public UserController(@Value("${AccessControlLimit}") double limit) {
        startTime = System.currentTimeMillis();
        rateLimiter = RateLimiter.create(limit);

        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        if (os.getName().equals("Mac OS X")) {
            try {
                if (os.getArch().equals("aarch64")) {
                    numCPUs = OS_X_AArch64_CLib.INSTANCE.hw_logicalcpu();
                    totalMEM = OS_X_AArch64_CLib.INSTANCE.hw_memsize();
                    OperatingSystem = OS_X_AArch64;

                } else if (os.getArch().equals("x86_64")) {
                    numCPUs = OS_X_X64_CLib.INSTANCE.hw_logicalcpu();
                    totalMEM = OS_X_X64_CLib.INSTANCE.hw_memsize();
                    OperatingSystem = OS_X_X64;
                }
            } catch (UnsatisfiedLinkError e) {
                OperatingSystem = OS_X_FIX;
            }
        } else {
            OperatingSystem = Other;
            processor = new SystemInfo().getHardware().getProcessor();
            memory = new SystemInfo().getHardware().getMemory();
        }
    }

    @RequestMapping("/user.html")
    public String userPage(HttpServletRequest req,
                           HttpServletResponse rsp) {
        if (!rateLimiter.tryAcquire()) {
            rsp.setStatus(403);
            return "error/403";
        }

        Object isLogin = req.getSession().getAttribute("isLogin");
        Object op = req.getSession().getAttribute("op");
        if (((Boolean)true).equals(isLogin)) {
            if (((Boolean)true).equals(op)) {
                return "redirect:" + baseURL + "admin.html";
            } else {
                return "user";
            }
        }
        rsp.setStatus(403);
        return "error/403";
    }

    @RequestMapping("/admin.html")
    public String adminPage(HttpServletRequest req,
                            HttpServletResponse rsp) {
        if (!rateLimiter.tryAcquire()) {
            rsp.setStatus(403);
            return "error/403";
        }

        Object isLogin = req.getSession().getAttribute("isLogin");
        Object op = req.getSession().getAttribute("op");
        if (isLogin != null && isLogin.equals(true) && op.equals(true)) {
            return "admin";
        }
        rsp.setStatus(403);
        return "error/403";
    }

    @PostMapping("/system/get")
    @ResponseBody
    public Object sys(@RequestBody Para para) throws InterruptedException {
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

        objs.put("code", 0);
        objs.put("message", "success");

        objs.put("start", startTime);
        objs.put("tokens", tokenPool.pool.size());
        if (OperatingSystem == Other) {
            double[] temp = Other_SYS();
            objs.put("cpu", 100 - temp[0]);
            objs.put("mem", 100 - temp[1]);
        } else {
            double[] temp = OS_X_SYS();
            objs.put("cpu", 100 - temp[0]);
            objs.put("mem", 100 - temp[1]);
        }
        objs.put("time", System.currentTimeMillis());

        return  objs;
    }

    public double[] OS_X_SYS() throws InterruptedException {
        double[] sysInfo = new double[2];

        if (OperatingSystem == OS_X_FIX) {
            TimeUnit.SECONDS.sleep(1);
            sysInfo[0] = 75;
            sysInfo[1] = 75;

            return sysInfo;
        }
        if (OperatingSystem == OS_X_AArch64) {
            CPUInfo = OS_X_AArch64_CLib.INSTANCE.cpu_tick_info(null, numCPUs, 1000 * 1000).getLongArray(0, 4);
            MEMInfo = OS_X_AArch64_CLib.INSTANCE.mem_size_info(null).getLongArray(0, 4);
        } else {
            CPUInfo = OS_X_X64_CLib.INSTANCE.cpu_tick_info(null, numCPUs, 1000 * 1000).getLongArray(0, 4);
            MEMInfo = OS_X_X64_CLib.INSTANCE.mem_size_info(null).getLongArray(0, 4);
        }
        sysInfo[0] = CPUInfo[2] * 100.0 / CPUInfo[3];
        sysInfo[1] = MEMInfo[0] * MEMInfo[3] * 100.0 / totalMEM;

        return sysInfo;
    }

    public double[] Other_SYS() throws InterruptedException {
        double[] sysInfo = new double[2];
        long[] prevTicks = processor.getSystemCpuLoadTicks();

        // 睡眠1s
        TimeUnit.SECONDS.sleep(1);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
                - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
                - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
                - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;

        sysInfo[0] = idle * 100.0 / totalCpu;
        sysInfo[1] = memory.getAvailable() * 100.0 / memory.getTotal();

        return sysInfo;
    }
}