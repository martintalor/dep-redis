package com.iflytek.dep.server.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.config.web
 * @Description:
 * @date 2019/6/5--14:33
 */
@Configuration
public class AckTaskConfiguration {

    public AckTaskConfiguration() {
        System.out.println("AckTaskConfiguration容器启动初始化。。。");
    }

    @Bean
    public ThreadPoolTaskScheduler messageBrokerSockJsTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setThreadNamePrefix("Ack task-");
        threadPoolTaskScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        threadPoolTaskScheduler.setRemoveOnCancelPolicy(true);
        return threadPoolTaskScheduler;
    }
}
