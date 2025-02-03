package com.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 */

@SpringBootApplication
@MapperScan("com.project.mapper")
public class CMSSOApplication {
    public static void main(String[] args) {
        SpringApplication.run(CMSSOApplication.class, args);
    }
}
