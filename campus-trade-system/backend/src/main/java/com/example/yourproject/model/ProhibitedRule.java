package com.example.yourproject.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 违禁规则实体类 - 用于商品自动审核
 */
@Entity
@Table(name = "prohibited_rules")
public class ProhibitedRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id")
    private Long ruleId;
    
    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;
    
    @Column(name = "rule_description", nullable = false, length = 500)
    private String ruleDescription;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;
    
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    // 无参构造函数
    public ProhibitedRule() {
        this.createdTime = LocalDateTime.now();
        this.isActive = true;
    }
    
    // 有参构造函数
    public ProhibitedRule(String ruleName, String ruleDescription, Long createdBy) {
        this();
        this.ruleName = ruleName;
        this.ruleDescription = ruleDescription;
        this.createdBy = createdBy;
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
    
    @PreUpdate
    public void preUpdate() {
        this.updatedTime = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "ProhibitedRule{" +
                "ruleId=" + ruleId +
                ", ruleName='" + ruleName + '\'' +
                ", ruleDescription='" + ruleDescription + '\'' +
                ", isActive=" + isActive +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                ", createdBy=" + createdBy +
                '}';
    }
}