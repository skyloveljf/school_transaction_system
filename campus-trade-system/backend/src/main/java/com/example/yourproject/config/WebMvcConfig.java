package com.example.yourproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 从 application.properties 读取上传文件实际存储的基础路径
    // 例如 storage.disk-path=E:/dev/uploads/campus-trade/
    // 注意：这里的 @Value 注入的是文件系统上的绝对或相对路径，而 storage.location 用于服务内部逻辑构建路径
    // 为了简单，我们将让 ResourceHandler 从 storage.location 配置的相对路径提供服务
    // 假设 storage.location 设置为 "./uploads" 意味着在项目根目录下
    // 那么 file:./uploads/images/ 将指向 项目根目录/uploads/images/
    
    // 如果 storage.location = "uploads/images" (相对于项目根目录)
    // 那么 "file:uploads/images/" 会映射到 项目根目录/uploads/images/
    // URL路径为 /uploads/images/**

    @Value("${storage.location:uploads/images}") // 使用 application.properties 中的 storage.location，默认为 "uploads/images"
    private String uploadPath;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 构建文件系统路径
        // Path actualUploadPath = Paths.get(uploadPath).toAbsolutePath();
        // String resourceLocation = "file:" + actualUploadPath.toString() + "/";
        
        // 简化：直接使用配置的相对路径，Spring Boot会尝试解析它
        // 如果 uploadPath 是 "uploads/images", 那么 file:uploads/images/ 将是项目根目录下的 uploads/images
        String resourceLocation = "file:" + uploadPath + (uploadPath.endsWith("/") ? "" : "/");


        // /uploads/images/** 将映射到文件系统上的 storage.location 目录
        // 例如，如果 storage.location=./uploads/images
        // 请求 /uploads/images/foo.jpg 会查找 ./uploads/images/foo.jpg
        registry.addResourceHandler("/uploads/images/**") // URL 模式
                .addResourceLocations(resourceLocation);  // 文件系统路径,确保末尾有斜杠
    }
}