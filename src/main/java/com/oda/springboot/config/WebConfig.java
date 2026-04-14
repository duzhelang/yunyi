package com.oda.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${files.upload.path}")
    private String fileUploadPath;
    @Value("${files.avatar.path}")
    private String avatarUploadPath;
    @Value("${files.common.path}")
    private String commonUploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 通用文件映射(兼容接口)
//        registry.addResourceHandler("/file/**")
//                .addResourceLocations("file:" + fileUploadPath);
//        // 2. 头像专用文件夹映?
//        registry.addResourceHandler("/file/avatar/**")
//                .addResourceLocations("file:" + avatarUploadPath);
//        // 3. 普通文件文件夹映射
//        registry.addResourceHandler("/file/common/**")
//                .addResourceLocations("file:" + commonUploadPath);
        // 通用文件 /file/** ?项目根目?files/
        registry.addResourceHandler("/file/**")
                .addResourceLocations("file:" + new File(fileUploadPath).getAbsolutePath() + File.separator);
        // 头像专用 /file/avatar/** ?项目根目?files/avatar/
        registry.addResourceHandler("/file/avatar/**")
                .addResourceLocations("file:" + new File(avatarUploadPath).getAbsolutePath() + File.separator);
        // 普通文?/file/common/** ?项目根目?files/common/
        registry.addResourceHandler("/file/common/**")
                .addResourceLocations("file:" + new File(commonUploadPath).getAbsolutePath() + File.separator);
    }
}