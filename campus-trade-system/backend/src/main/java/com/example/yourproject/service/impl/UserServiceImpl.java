package com.example.yourproject.service.impl;

import com.example.yourproject.mapper.UserMapper;
import com.example.yourproject.model.User;
import com.example.yourproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
            throw new RuntimeException("用户不存在，ID: " + user.getUserId());
        }
        
        // 更新用户信息
        int result = userMapper.update(user);
        if (result == 0) {
            throw new RuntimeException("更新用户信息失败");
        }
        
        return userMapper.findById(user.getUserId());
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

    @Override
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    @Override
    public User updateUserRole(Long userId, String role) throws RuntimeException {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在，ID: " + userId);
        }
        
        user.setRole(role);
        int result = userMapper.update(user);
        if (result == 0) {
            throw new RuntimeException("更新用户角色失败");
        }
        
        return userMapper.findById(userId);
    }

    @Override
    public List<User> getRegularUsers() {
        return userMapper.findAll().stream()
                .filter(user -> "ROLE_USER".equals(user.getRole()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) throws RuntimeException {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在，ID: " + userId);
        }
        
        // 检查是否是管理员，不允许删除管理员
        if ("ROLE_ADMIN".equals(user.getRole())) {
            throw new RuntimeException("不允许删除管理员账号");
        }
        
        int result = userMapper.deleteById(userId);
        if (result == 0) {
            throw new RuntimeException("删除用户失败");
        }
        
        return true;
    }

    @Override
    public Long getTotalUserCount() {
        return userMapper.countAllUsers();
    }
}