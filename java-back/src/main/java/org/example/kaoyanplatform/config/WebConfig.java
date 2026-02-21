package org.example.kaoyanplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将网络路径 /uploads/** 映射到本地磁盘路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");

        // 将网络路径 /img/** 映射到 static/img 目录
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");
    }
}