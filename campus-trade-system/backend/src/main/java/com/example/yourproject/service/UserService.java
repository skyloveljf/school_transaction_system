package com.example.yourproject.service;

import com.example.yourproject.model.User;
import java.util.List;

public interface UserService {

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    User getUserById(Long userId);

    /**
     * 根据用户ID获取用户名
     * @param userId 用户ID
     * @return 用户名，如果不存在则返回默认名称
     */
    String getUsernameById(Long userId);

    /**
     * 根据用户ID获取用户头像URL
     * @param userId 用户ID
     * @return 头像URL，如果不存在则返回默认头像
     */
    String getUserAvatarById(Long userId);

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户对象，如果不存在则返回null
     */
    User getUserByUsername(String username);

    /**
     * 根据邮箱获取用户信息
     * @param email 邮箱
     * @return 用户对象，如果不存在则返回null
     */
    User getUserByEmail(String email);

    /**
     * 注册一个新用户
     * 需要处理密码加密、检查用户名和邮箱是否已存在等逻辑
     * @param user 用户对象 (通常包含明文密码，service层进行加密处理)
     * @return 注册成功的用户对象 (包含生成的userId和加密后的密码)
     * @throws RuntimeException 如果用户名或邮箱已存在，或其他注册错误
     */
    User registerUser(User user) throws RuntimeException;

    /**
     * 更新用户信息
     * (注意：密码更新应通过专门的方法处理，例如 changePassword)
     * @param user 用户对象，包含要更新的字段和userId
     * @return 更新后的用户对象
     * @throws RuntimeException 如果用户不存在或更新失败
     */
    User updateUserProfile(User user) throws RuntimeException;

    /**
     * 获取所有用户列表
     * @return 所有用户的列表
     */
    List<User> getAllUsers();

    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param role 新的角色值
     * @return 更新后的用户对象
     * @throws RuntimeException 如果用户不存在或更新失败
     */
    User updateUserRole(Long userId, String role) throws RuntimeException;

    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     */
    void updateUserLastLogin(Long userId);

    /**
     * (可选) 用户登录验证 - 实际项目中会结合Spring Security
     * @param username 用户名
     * @param rawPassword 明文密码
     * @return 验证通过则返回User对象，否则抛出认证异常
     * @throws RuntimeException 如果认证失败
     */
    User authenticate(String username, String rawPassword) throws RuntimeException;

    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码（明文）
     * @param newPassword 新密码（明文）
     * @param confirmNewPassword 确认新密码（明文）
     * @return 如果密码修改成功则返回true，否则返回false或抛出异常
     * @throws RuntimeException 如果验证失败或更新失败
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword, String confirmNewPassword) throws RuntimeException;

    /**
     * 获取所有普通用户列表（不包括管理员）
     * @return 所有普通用户的列表
     */
    List<User> getRegularUsers();

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 如果删除成功返回true，否则返回false或抛出异常
     * @throws RuntimeException 如果用户不存在或删除失败
     */
    boolean deleteUser(Long userId) throws RuntimeException;

    /**
     * 获取用户总数（管理员统计功能）
     * @return 用户总数
     */
    Long getTotalUserCount();
    // 可以根据需要添加更多方法，例如：
    // List<User> getAllUsers(); // (用于管理后台)
    // boolean deleteUser(Long userId); // (用于管理后台)
}