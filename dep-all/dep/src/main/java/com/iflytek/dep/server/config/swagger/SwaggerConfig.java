/* 
 *
 * Copyright (C) 1999-2012 IFLYTEK Inc.All Rights Reserved. 
 * 
 * FileName：SwaggerConfig.java
 * 
 * Description：
 * 
 * History：
 * Version   Author      Date            Operation 
 * 1.0	  wangt   2016年10月10日下午5:38:14	       Create	
 */
package com.iflytek.dep.server.config.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger配置，将Controller的方法进行可视化的展现，像方法注释，方法参数，方法返回值等都提供了相应的用户界面
 * 
 * @author ddcai
 *
 * @version 1.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig  extends WebMvcConfigurerAdapter {

    @Value("${server.node.id}")
    private String appName;

    @Value("${node.id}")
    private String nodeId;

    /**
     * @description
     * @author ddcai
     * @date 2019年2月19日上午10:40:30
     */
    @Bean
    public Docket controlApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("control")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false).forCodeGeneration(true)
                .pathMapping("/").select()

                .apis(RequestHandlerSelectors
                        .withClassAnnotation(RestController.class))
                .paths(PathSelectors.regex("/*/.*")).build()
                .apiInfo(controlApiInfo());
    }

    /**
     * @description
     * @author ddcai
     * @date 2019年2月19日上午10:41:52
     */
    private ApiInfo controlApiInfo() {
        ApiInfo apiInfo = new ApiInfo(appName, nodeId+",数据交换平台", "1.0", "", "developer", "",
                "");
        return apiInfo;
    }

}
