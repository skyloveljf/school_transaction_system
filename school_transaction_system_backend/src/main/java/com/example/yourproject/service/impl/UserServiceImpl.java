package com.example.yourproject.service.impl;

import com.example.yourproject.mapper.UserMapper;
import com.example.yourproject.model.User;
import com.example.yourproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service // 标记为Spring的服务组件
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder; // 改为 PasswordEncoder 接口

    @Autowired
    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) { // 注入 PasswordEncoder
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder; // 使用注入的实例
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.findById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    @Transactional // 声明事务性，确保操作的原子性
    public User registerUser(User user) throws RuntimeException {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在: " + user.getUsername());
        }
        if (userMapper.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册: " + user.getEmail());
        }

        // 加密密码
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash())); // 假设传入的是明文密码暂存在passwordHash字段
        user.setRegistrationDate(LocalDateTime.now());
        // lastLoginDate 可以在首次登录时设置，或注册时也设置

        userMapper.insert(user);
        // user对象现在应该有userId了 (通过useGeneratedKeys=true和keyProperty配置)
        return user;
    }

    @Override
    @Transactional
    public User updateUserProfile(User user) throws RuntimeException {
        User existingUser = userMapper.findById(user.getUserId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在: " + user.getUserId());
        }

        // 只允许更新部分字段，例如邮箱，或未来添加的如昵称、头像等
        // 用户名通常不允许修改，或需要特殊流程
        // 密码修改应有单独接口
        boolean updated = false;
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
             // 检查新邮箱是否已被其他用户使用
            User userWithNewEmail = userMapper.findByEmail(user.getEmail());
            if (userWithNewEmail != null && !userWithNewEmail.getUserId().equals(existingUser.getUserId())) {
                throw new RuntimeException("邮箱已被其他用户注册: " + user.getEmail());
            }
            existingUser.setEmail(user.getEmail());
            updated = true;
        }
        
        // 处理头像URL更新
        if (user.getAvatarUrl() != null) {
            existingUser.setAvatarUrl(user.getAvatarUrl());
            updated = true;
        }
        
        // 处理简介更新
        if (user.getBio() != null) {
            existingUser.setBio(user.getBio());
            updated = true;
        }

        if (updated) {
            userMapper.update(existingUser);
        }
        return existingUser;
    }

    @Override
    @Transactional
    public void updateUserLastLogin(Long userId) {
        User user = userMapper.findById(userId);
        if (user != null) {
            user.setLastLoginDate(LocalDateTime.now());
            userMapper.update(user);
        }
    }

    @Override
    public User authenticate(String username, String rawPassword) throws RuntimeException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("认证失败：用户名或密码错误。"); // 用户不存在
        }
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new RuntimeException("认证失败：用户名或密码错误。"); // 密码不匹配
        }
        return user;
    }

    /**
     * 根据用户ID获取用户名
     */
    @Override
    public String getUsernameById(Long userId) {
        User user = getUserById(userId);
        return user != null ? user.getUsername() : "用户" + userId;
    }

    /**
     * 根据用户ID获取用户头像URL
     */
    @Override
    public String getUserAvatarById(Long userId) {
        User user = getUserById(userId);
        // 如果用户存在且有头像，返回头像URL
        if (user != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            return user.getAvatarUrl();
        }
        // 否则返回默认头像
        return "/default-avatar.png";
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword, String confirmNewPassword) throws RuntimeException {
        if (newPassword == null || !newPassword.equals(confirmNewPassword)) {
            throw new RuntimeException("新密码和确认新密码不匹配");
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("旧密码不正确");
        }

        // 密码长度校验 (也可以在DTO中完成，这里是双重保险)
        if (newPassword.length() < 6) {
            throw new RuntimeException("新密码长度至少为6位");
        }

        // 加密新密码并更新
        String newPasswordHash = passwordEncoder.encode(newPassword);
        int updatedRows = userMapper.updatePassword(userId, newPasswordHash);

        return updatedRows > 0;
    }
}