package com.shop.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.LoginRequest;
import com.shop.dto.ResponseDTO;
import com.shop.model.User;
import com.shop.service.UserService;
import com.shop.util.JwtUtil;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户注册接口
     *
     * @param request 注册请求
     * @return 注册后的用户信息
     */
    @PostMapping("/register")
    public ResponseDTO<User> register(@RequestBody LoginRequest request) {
        User user = userService.register(request.getUsername(), request.getPassword());
        return ResponseDTO.ok(user);
    }

    /**
     * 用户登录接口
     *
     * @param request 登录请求
     * @return JWT token 和用户信息
     */
    @PostMapping("/login")
    public ResponseDTO<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return userService.login(request.getUsername(), request.getPassword())
                .map(user -> {
                    // 生成 JWT token
                    String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

                    // 构建返回给前端的数据
                    Map<String, Object> body = new HashMap<>();
                    body.put("token", token);
                    body.put("role", user.getRole());
                    body.put("user", user);

                    return ResponseDTO.ok(body);
                })
                .orElseGet(() -> ResponseDTO.fail("用户名或密码错误"));
    }
}

