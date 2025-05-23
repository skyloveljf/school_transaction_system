package com.example.yourproject.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    private String location = "uploads"; // 默认存储在项目根目录下的 uploads 文件夹

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}