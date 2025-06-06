package com.example.yourproject.service.impl;

import com.example.yourproject.dto.ProhibitedRuleDto;
import com.example.yourproject.mapper.ProhibitedRuleMapper;
import com.example.yourproject.mapper.UserMapper;
import com.example.yourproject.model.ProhibitedRule;
import com.example.yourproject.model.User;
import com.example.yourproject.service.ProhibitedRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProhibitedRuleServiceImpl implements ProhibitedRuleService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProhibitedRuleServiceImpl.class);
    
    private final ProhibitedRuleMapper prohibitedRuleMapper;
    private final UserMapper userMapper;
    
    @Autowired
    public ProhibitedRuleServiceImpl(ProhibitedRuleMapper prohibitedRuleMapper, UserMapper userMapper) {
        this.prohibitedRuleMapper = prohibitedRuleMapper;
        this.userMapper = userMapper;
    }
    
    @Override
    public List<ProhibitedRuleDto> getAllRules() {
        try {
            List<ProhibitedRule> rules = prohibitedRuleMapper.findAll();
            return rules.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("获取所有违禁规则失败", e);
            throw new RuntimeException("获取违禁规则失败：" + e.getMessage());
        }
    }
    
    @Override
    public List<ProhibitedRuleDto> getActiveRules() {
        try {
            List<ProhibitedRule> rules = prohibitedRuleMapper.findAllActive();
            return rules.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("获取激活违禁规则失败", e);
            throw new RuntimeException("获取激活违禁规则失败：" + e.getMessage());
        }
    }
    
    @Override
    public ProhibitedRuleDto getRuleById(Long ruleId) {
        try {
            ProhibitedRule rule = prohibitedRuleMapper.findById(ruleId);
            if (rule == null) {
                throw new RuntimeException("违禁规则不存在，ID：" + ruleId);
            }
            return convertToDto(rule);
        } catch (Exception e) {
            logger.error("根据ID获取违禁规则失败，ID：{}", ruleId, e);
            throw new RuntimeException("获取违禁规则失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ProhibitedRuleDto createRule(String ruleName, String ruleDescription, Long createdBy) {
        try {
            ProhibitedRule rule = new ProhibitedRule();
            rule.setRuleName(ruleName);
            rule.setRuleDescription(ruleDescription);
            rule.setCreatedBy(createdBy);
            rule.setCreatedTime(LocalDateTime.now());
            rule.setIsActive(true);
            
            int result = prohibitedRuleMapper.insert(rule);
            if (result > 0) {
                logger.info("创建违禁规则成功：{}", rule);
                return convertToDto(rule);
            } else {
                throw new RuntimeException("创建违禁规则失败");
            }
        } catch (Exception e) {
            logger.error("创建违禁规则失败", e);
            throw new RuntimeException("创建违禁规则失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ProhibitedRuleDto updateRule(Long ruleId, String ruleName, String ruleDescription) {
        try {
            ProhibitedRule rule = prohibitedRuleMapper.findById(ruleId);
            if (rule == null) {
                throw new RuntimeException("违禁规则不存在，ID：" + ruleId);
            }
            
            rule.setRuleName(ruleName);
            rule.setRuleDescription(ruleDescription);
            rule.setUpdatedTime(LocalDateTime.now());
            
            int result = prohibitedRuleMapper.update(rule);
            if (result > 0) {
                logger.info("更新违禁规则成功：{}", rule);
                return convertToDto(rule);
            } else {
                throw new RuntimeException("更新违禁规则失败");
            }
        } catch (Exception e) {
            logger.error("更新违禁规则失败，ID：{}", ruleId, e);
            throw new RuntimeException("更新违禁规则失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteRule(Long ruleId) {
        try {
            int result = prohibitedRuleMapper.deleteById(ruleId);
            if (result > 0) {
                logger.info("删除违禁规则成功，ID：{}", ruleId);
                return true;
            } else {
                logger.warn("删除违禁规则失败，规则不存在，ID：{}", ruleId);
                return false;
            }
        } catch (Exception e) {
            logger.error("删除违禁规则失败，ID：{}", ruleId, e);
            throw new RuntimeException("删除违禁规则失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean updateRuleStatus(Long ruleId, Boolean isActive) {
        try {
            int result = prohibitedRuleMapper.updateActiveStatus(ruleId, isActive);
            if (result > 0) {
                logger.info("更新违禁规则状态成功，ID：{}，状态：{}", ruleId, isActive);
                return true;
            } else {
                logger.warn("更新违禁规则状态失败，规则不存在，ID：{}", ruleId);
                return false;
            }
        } catch (Exception e) {
            logger.error("更新违禁规则状态失败，ID：{}", ruleId, e);
            throw new RuntimeException("更新违禁规则状态失败：" + e.getMessage());
        }
    }
    
    @Override
    public List<String> getActiveRuleDescriptions() {
        try {
            List<ProhibitedRule> activeRules = prohibitedRuleMapper.findAllActive();
            return activeRules.stream()
                    .map(ProhibitedRule::getRuleDescription)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("获取激活违禁规则描述失败", e);
            throw new RuntimeException("获取违禁规则描述失败：" + e.getMessage());
        }
    }
    
    /**
     * 将实体转换为DTO
     */
    private ProhibitedRuleDto convertToDto(ProhibitedRule rule) {
        if (rule == null) {
            return null;
        }
        
        ProhibitedRuleDto dto = new ProhibitedRuleDto();
        dto.setRuleId(rule.getRuleId());
        dto.setRuleName(rule.getRuleName());
        dto.setRuleDescription(rule.getRuleDescription());
        dto.setIsActive(rule.getIsActive());
        dto.setCreatedTime(rule.getCreatedTime());
        dto.setUpdatedTime(rule.getUpdatedTime());
        dto.setCreatedBy(rule.getCreatedBy());
        
        // 获取创建者用户名
        if (rule.getCreatedBy() != null) {
            try {
                User creator = userMapper.findById(rule.getCreatedBy());
                if (creator != null) {
                    dto.setCreatedByUsername(creator.getUsername());
                }
            } catch (Exception e) {
                logger.warn("获取创建者用户名失败，用户ID：{}", rule.getCreatedBy(), e);
                dto.setCreatedByUsername("未知用户");
            }
        }
        
        return dto;
    }
}