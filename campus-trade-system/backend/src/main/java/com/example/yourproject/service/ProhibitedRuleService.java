package com.example.yourproject.service;

import com.example.yourproject.dto.ProhibitedRuleDto;
import com.example.yourproject.model.ProhibitedRule;
import java.util.List;

/**
 * 违禁规则服务接口
 */
public interface ProhibitedRuleService {
    
    /**
     * 获取所有违禁规则
     * @return 违禁规则DTO列表
     */
    List<ProhibitedRuleDto> getAllRules();
    
    /**
     * 获取所有激活的违禁规则
     * @return 激活的违禁规则DTO列表
     */
    List<ProhibitedRuleDto> getActiveRules();
    
    /**
     * 根据ID获取违禁规则
     * @param ruleId 规则ID
     * @return 违禁规则DTO
     */
    ProhibitedRuleDto getRuleById(Long ruleId);
    
    /**
     * 创建新的违禁规则
     * @param ruleName 规则名称
     * @param ruleDescription 规则描述
     * @param createdBy 创建者用户ID
     * @return 创建的违禁规则DTO
     */
    ProhibitedRuleDto createRule(String ruleName, String ruleDescription, Long createdBy);
    
    /**
     * 更新违禁规则
     * @param ruleId 规则ID
     * @param ruleName 规则名称
     * @param ruleDescription 规则描述
     * @return 更新的违禁规则DTO
     */
    ProhibitedRuleDto updateRule(Long ruleId, String ruleName, String ruleDescription);
    
    /**
     * 删除违禁规则
     * @param ruleId 规则ID
     * @return 是否删除成功
     */
    boolean deleteRule(Long ruleId);
    
    /**
     * 更新规则激活状态
     * @param ruleId 规则ID
     * @param isActive 是否激活
     * @return 是否更新成功
     */
    boolean updateRuleStatus(Long ruleId, Boolean isActive);
    
    /**
     * 获取激活的违禁规则描述列表（用于AI审核）
     * @return 规则描述列表
     */
    List<String> getActiveRuleDescriptions();
}