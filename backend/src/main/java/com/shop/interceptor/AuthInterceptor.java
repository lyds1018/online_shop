package com.shop.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.shop.exception.BusinessException;
import com.shop.service.UserService;
import com.shop.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 用户认证拦截器
 *
 * 功能：
 * 1. 验证请求中的 JWT 是否有效。
 * 2. 将用户名和角色信息写入 request 属性，供后续业务逻辑使用。
 * 3. 放行无需认证的接口（如登录注册、商品查询）。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthInterceptor(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 请求处理前执行
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器对象
     * @return true 允许请求继续，false 拦截请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();

        // -------------------------
        // 放行无需登录的接口
        // -------------------------
        // 登录、注册接口无需验证 token
        if (path.startsWith("/api/users/login") || path.startsWith("/api/users/register")) {
            return true;
        }
        // GET 请求的商品查询接口可公开访问
        if ("GET".equalsIgnoreCase(request.getMethod()) && path.startsWith("/api/products")) {
            return true;
        }

        // -------------------------
        // 获取并验证 JWT
        // -------------------------
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException("未登录");
        }

        try {
            // 去掉 "Bearer " 前缀
            String jwtToken = token.substring(7);
            
            // 解析 JWT
            String username = jwtUtil.extractUsername(jwtToken);
            String role = jwtUtil.extractRole(jwtToken);

            // 检查用户是否存在
            if (!userService.existsByUsername(username)) {
                throw new BusinessException("用户不存在");
            }

            // 管理员接口权限校验
            if (path.startsWith("/api/admin") && !"ADMIN".equals(role)) {
                throw new BusinessException("无权访问");
            }

            // 将用户名和角色写入 request，供控制器使用
            request.setAttribute("username", username);
            request.setAttribute("role", role);
            return true;

        } catch (Exception e) {
            // JWT 解析或验证失败
            throw new BusinessException("无效的token");
        }
    }
}
