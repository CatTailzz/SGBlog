package com.sangeng;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/8/16 16:04
 **/
@SpringBootApplication
@MapperScan("com.sangeng.mapper")
@EnableScheduling
@EnableSwagger2
public class SanGengBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(SanGengBlogApplication.class, args);
    }
}
