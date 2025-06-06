package com.example.yourproject.mapper;

import com.example.yourproject.model.ProhibitedRule;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProhibitedRuleMapper {
    
    /**
     * 获取所有违禁规则
     */
    @Select("SELECT * FROM STS_DB.prohibited_rules ORDER BY created_time DESC")
    List<ProhibitedRule> findAll();
    
    /**
     * 获取所有激活的违禁规则
     */
    @Select("SELECT * FROM STS_DB.prohibited_rules WHERE is_active = 1 ORDER BY created_time DESC")
    List<ProhibitedRule> findAllActive();
    
    /**
     * 根据ID查找违禁规则
     */
    @Select("SELECT * FROM STS_DB.prohibited_rules WHERE rule_id = #{ruleId}")
    ProhibitedRule findById(@Param("ruleId") Long ruleId);
    
    /**
     * 插入新的违禁规则
     */
    @Insert("INSERT INTO STS_DB.prohibited_rules (rule_name, rule_description, is_active, created_time, created_by) " +
            "VALUES (#{ruleName}, #{ruleDescription}, #{isActive}, #{createdTime}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "ruleId")
    int insert(ProhibitedRule rule);
    
    /**
     * 更新违禁规则
     */
    @Update("UPDATE STS_DB.prohibited_rules SET " +
            "rule_name = #{ruleName}, " +
            "rule_description = #{ruleDescription}, " +
            "is_active = #{isActive}, " +
            "updated_time = #{updatedTime} " +
            "WHERE rule_id = #{ruleId}")
    int update(ProhibitedRule rule);
    
    /**
     * 删除违禁规则
     */
    @Delete("DELETE FROM STS_DB.prohibited_rules WHERE rule_id = #{ruleId}")
    int deleteById(@Param("ruleId") Long ruleId);
    
    /**
     * 更新规则激活状态
     */
    @Update("UPDATE STS_DB.prohibited_rules SET is_active = #{isActive}, updated_time = NOW() WHERE rule_id = #{ruleId}")
    int updateActiveStatus(@Param("ruleId") Long ruleId, @Param("isActive") Boolean isActive);
    
    /**
     * 获取规则总数
     */
    @Select("SELECT COUNT(*) FROM STS_DB.prohibited_rules")
    long count();
    
    /**
     * 获取激活规则总数
     */
    @Select("SELECT COUNT(*) FROM STS_DB.prohibited_rules WHERE is_active = 1")
    long countActive();
}