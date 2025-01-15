package com.zerock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${itemImgLocation}") // itemImgLocation 프로퍼티 주입
    private String itemImgLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /images/item/** 요청을 실제 파일 시스템의 itemImgLocation 디렉토리로 매핑
        registry.addResourceHandler("/images/item/**")
                .addResourceLocations("file:/" + itemImgLocation + "/");

        // 기본 이미지(default.png) 경로 매핑 추가
        registry.addResourceHandler("/images/default.png")
                .addResourceLocations("classpath:/static/images/default.png");
    }
}
