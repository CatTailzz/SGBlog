package com.sangeng.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sangeng.controller"))
                .build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("cattail", "http://www.cattail.com", "zsj@qq.com");
        return new ApiInfoBuilder()
                .title("我是文档标题")
                .description("这名用户很懒，什么都没有留下")
                .contact(contact)   // 联系方式
                .version("1.1.0")  // 版本
                .build();
    }
}