package com.shop.controller;

import com.shop.model.User;
import com.shop.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 基础控制器
 * 提供通用的辅助方法
 */
public abstract class BaseController {

    protected final UserService userService;

    protected BaseController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 从请求中解析当前登录用户 ID
     *
     * @param request HTTP 请求
     * @return 用户 ID
     */
    protected Long getCurrentUserId(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return user.getId();
    }

    /**
     * 从请求中解析当前登录用户
     *
     * @param request HTTP 请求
     * @return 用户对象
     */
    protected User getCurrentUser(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }
}
