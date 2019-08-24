package com.iflytek.dep.server.config.schedule;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 设置Scheduler线程池，解决【Unexpected error occurred in scheduled task】 问题
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Package com.iflytek.dep.server.config
 * @Description:
 * @date 2019/7/10--14:55
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {

//
//    @Override
//    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
//        scheduledTaskRegistrar.setScheduler(taskExecutor());
//    }
//
//    @Bean(destroyMethod = "shutdown")
//    public Executor taskExecutor() {
//        return Executors.newScheduledThreadPool(25 ,new ThreadFactory() {
//            private final AtomicLong counter = new AtomicLong();
//
//            @Override
//            public Thread newThread(Runnable r) {
//                Thread thread = new Thread(r);
//                thread.setName("dep-scheduler-" + counter.incrementAndGet());
//                return thread;
//            }
//
//        });
//    }
}
