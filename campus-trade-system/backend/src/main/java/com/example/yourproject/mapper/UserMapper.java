package com.example.yourproject.mapper;

import com.example.yourproject.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
// import org.apache.ibatis.annotations.Select; // 如果使用注解方式定义SQL
// import org.apache.ibatis.annotations.Insert;
// import org.apache.ibatis.annotations.Update;
// import org.apache.ibatis.annotations.Delete;
// import org.apache.ibatis.annotations.Options;

import java.util.List;

@Mapper // 标记这是一个MyBatis Mapper接口，会被Spring Boot扫描到
public interface UserMapper {

    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    User findById(@Param("userId") Long userId);

    /**
     * 根据用户ID获取用户名
     * @param userId 用户ID
     * @return 用户名，如果不存在则返回null
     */
    String getUsernameById(@Param("userId") Long userId);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象，如果不存在则返回null
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户对象，如果不存在则返回null
     */
    User findByEmail(@Param("email") String email);

    /**
     * 插入一个新用户
     * @param user 用户对象
     * @return 影响的行数，通常是1如果成功
     */
    int insert(User user); // 返回自增主键的需求通常在XML中配置 <selectKey> 或使用 @Options

    /**
     * 更新用户信息
     * @param user 用户对象，必须包含userId
     * @return 影响的行数
     */
    int update(User user);

    /**
     * 根据用户ID删除用户
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteById(@Param("userId") Long userId);

    /**
     * 查询所有用户 (通常用于管理功能，实际应用中可能需要分页)
     * @return 用户列表
     */
    List<User> findAll();

    /**
     * 根据用户ID更新密码
     * @param userId 用户ID
     * @param newPasswordHash 加密后的新密码
     * @return 影响的行数
     */
    int updatePassword(@Param("userId") Long userId, @Param("newPasswordHash") String newPasswordHash);
} 