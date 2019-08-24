package com.iflytek.dep.admin.scheduled;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class FtpTaskConfiguration {
    public FtpTaskConfiguration() {
        System.out.println("FtpTaskConfiguration容器启动初始化。。。");
    }

    @Bean
    public ThreadPoolTaskScheduler messageBrokerSockJsTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setThreadNamePrefix("Ftp task-");
        threadPoolTaskScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        threadPoolTaskScheduler.setRemoveOnCancelPolicy(true);
        return threadPoolTaskScheduler;
    }
}