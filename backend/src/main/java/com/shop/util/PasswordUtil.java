package com.shop.util;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * 密码工具类
 * 提供密码加密和验证功能
 */
@Component
public class PasswordUtil {

    private static final int BCRYPT_COST = 12;

    /**
     * 加密密码
     *
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public String hashPassword(String rawPassword) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, rawPassword.toCharArray());
    }

    /**
     * 验证密码
     *
     * @param rawPassword    原始密码
     * @param hashedPassword 加密后的密码
     * @return true 如果密码匹配
     */
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return BCrypt.verifyer()
                .verify(rawPassword.getBytes(StandardCharsets.UTF_8),
                        hashedPassword.getBytes(StandardCharsets.UTF_8))
                .verified;
    }
}
