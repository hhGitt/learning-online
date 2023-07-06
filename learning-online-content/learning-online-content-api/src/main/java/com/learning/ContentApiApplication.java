package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author HH
 * @version 1.0
 * @description 内容管理服务启动类
 * @date 2023/6/29 11:50
 **/
@EnableFeignClients(basePackages = {"com.learning.content.feginclient"})
@SpringBootApplication
@EnableSwagger2
public class ContentApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContentApiApplication.class, args);
	}

}
