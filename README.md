# ems
Electricity Management System - 电费管理系统

# 1.2.2版本更新
* 除 X64 架构的 OS X 外, 现已添加对 AArch64 架构的 OS X(iOS, iPadOS) 的支援
* 使用 C(mach/mach.h) 语言为 OS X 平台重写系统信息访问, 透过 JNA 调用 libsysinfo-architecture.dylib 实现
* 现在开始, 登入系统后访问 index.html 也会被重定向至个人页面
* 修复了直接访问 index.html 导致 baseURL 缺失无法登陆的问题

# 项目简介绍
使用Java开发的简单电费管理系统

Sprint Boot 学习的练手项目

## 项目特性
* 完全的前后端分离设计
    * Web API 使用
    > 透过接统 API 可以实现扣费与充值功能
    > 
    > 规范的响应代码与响应提示
    * Vue 使用
    > 交互体验透过 AJAX 与 JS 实现, 不涉及表单提交与跳转
    > 
    > 现代化 Web Application 设计
* 访问控制
    * 权限控制
    > 严格区分普通用户与管理员并执行严格的权限控制
    * 请求限制
    > 限制 API 请求次数, 抵御 API 滥用与轻度 DDoS 攻击
* Token 使用, 访问 API 首先需要利用账号密码请求 Token, Token可随意刷新并具备过期与续期机制, 这将提高系统性能并增强系统安全性
* Cache 使用, 透过缓存部分数据库数据可以显著提高系统性能, 并且具备完善的同步机制确保数据正确, Cache会自动过期以减少内存占用
* 锁机制使用, 确保不会产生并发错误

### 支援平台
> Windows
> 
> Linux
> 
> OS X(macOS, iOS, iPadOS)

### 环境要求
> Java JDK 17 以降
>
> MySQL

### 下载
[Releases](https://github.com/hoshinosena/ems/releases)

### 部署
* 通用部署
    > 在命令行中输入 `java -jar /parth/to/ems/file`
* 后台部署
    > Linux 与 OS X 可以使用 `nohup java -jar /path/to/ems/file >ems.log 2>&1 &`
    >
    > 这将在后台运行并将日志输出到当前目录下的 ems.log 文件中
    >
    > 关闭后台进程需先使用 `lsof -i:8080` 获取相应进程的 PID, 使用 `kill -9 PID` 终止相应进程

## 反馈
提交[Issue](https://github.com/hoshinosena/ems/issues/new)

## 致谢
| 提供 | 简介 | 来源 |
|-----|------|-----|
| Spring Framework | 一个轻量级 Java Web 框架 | [https://spring.io/](https://spring.io/) |
| Spring Boot | 一个基于 Spring Framework 的快速开发套件 | [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot) |
| Vue | 一个渐进式 JavaScript 框架 | [https://vuejs.org/](https://vuejs.org/) |
| jQuery | 一个快速、简洁的 JavaScript 框架 | [https://github.com/jquery/jquery](https://github.com/jquery/jquery) |
| jQuery Cookie | 一个 jQuery 的 Cookie 增强组件 | [https://github.com/carhartl/jquery-cookie](https://github.com/carhartl/jquery-cookie) |
| jQuery Confirm | 一个集 Alert,Confirm,Dialog 于一身的 jQuery 增强组件 | [https://github.com/craftpip/jquery-confirm](https://github.com/craftpip/jquery-confirm) |
| Chrome | 一个 Google 开发的浏览器与 Web 性能分析工具 | [https://www.google.com/chrome/](https://www.google.com/chrome/) |

## 许可证
[Apache License 2.0](https://github.com/hoshinosena/ems/blob/master/LICENSE)