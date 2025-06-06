package com.example.yourproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProhibitedRuleDto {
    private Long ruleId;
    private String ruleName;
    private String ruleDescription;
    private Boolean isActive;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Long createdBy;
    private String createdByUsername; // 创建者用户名（用于显示）
    
    // 无参构造函数
    public ProhibitedRuleDto() {}
    
    // 有参构造函数
    public ProhibitedRuleDto(Long ruleId, String ruleName, String ruleDescription, 
                           Boolean isActive, LocalDateTime createdTime, 
                           LocalDateTime updatedTime, Long createdBy, String createdByUsername) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.ruleDescription = ruleDescription;
        this.isActive = isActive;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.createdBy = createdBy;
        this.createdByUsername = createdByUsername;
    }
    
    // Getters and Setters
    public Long getRuleId() {
        return ruleId;
    }
    
    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }
    
    public String getRuleName() {
        return ruleName;
    }
    
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    
    public String getRuleDescription() {
        return ruleDescription;
    }
    
    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getCreatedByUsername() {
        return createdByUsername;
    }
    
    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }
    
    @Override
    public String toString() {
        return "ProhibitedRuleDto{" +
                "ruleId=" + ruleId +
                ", ruleName='" + ruleName + '\'' +
                ", ruleDescription='" + ruleDescription + '\'' +
                ", isActive=" + isActive +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                ", createdBy=" + createdBy +
                ", createdByUsername='" + createdByUsername + '\'' +
                '}';
    }
}