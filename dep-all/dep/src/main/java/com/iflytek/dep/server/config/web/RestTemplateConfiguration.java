package com.iflytek.dep.server.config.web;

import org.apache.commons.httpclient.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnClass(value = {RestTemplate.class, HttpClient.class})
public class RestTemplateConfiguration {
  @Autowired
  private RestTemplateBuilder builder;

  @Bean
  public RestTemplate restTemplate() {
    return builder.build();
  }
 
}