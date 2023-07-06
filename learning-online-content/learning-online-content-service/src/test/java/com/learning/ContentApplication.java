package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author HH
 * @version 1.0
 * @description 内容管理服务启动类
 * @date 2023/6/29 14:48
 **/
@EnableFeignClients(basePackages = {"com.learning.content.feginclient"})
@SpringBootApplication
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
