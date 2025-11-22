package com.shop.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.shop.exception.BusinessException;
import com.shop.model.User;
import com.shop.repository.UserRepository;
import com.shop.util.PasswordUtil;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;

    public UserService(UserRepository userRepository, PasswordUtil passwordUtil) {
        this.userRepository = userRepository;
        this.passwordUtil = passwordUtil;
    }

    /**
     * 用户注册
     *
     * @param username    用户名
     * @param rawPassword 原始密码
     * @return 注册后的用户对象
     */
    @Transactional
    public User register(String username, String rawPassword) {
        // 参数校验
        if (!StringUtils.hasText(username) || !StringUtils.hasText(rawPassword)) {
            throw new BusinessException("用户名与密码不能为空");
        }
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("用户名已存在");
        }
        
        // 创建用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordUtil.hashPassword(rawPassword));
        
        return userRepository.save(user);
    }

    /**
     * 用户登录验证
     *
     * @param username    用户名
     * @param rawPassword 原始密码
     * @return 用户对象（如果验证成功）
     */
    public Optional<User> login(String username, String rawPassword) {
        // 参数校验
        if (!StringUtils.hasText(username) || !StringUtils.hasText(rawPassword)) {
            return Optional.empty();
        }
        
        return userRepository.findByUsername(username)
                .filter(user -> passwordUtil.verifyPassword(rawPassword, user.getPassword()));
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return true 如果用户名存在
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 根据 ID 查找用户
     *
     * @param userId 用户 ID
     * @return 用户对象
     */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }
}


