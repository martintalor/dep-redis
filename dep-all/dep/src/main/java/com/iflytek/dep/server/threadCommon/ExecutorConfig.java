package com.iflytek.dep.server.threadCommon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ExecutorConfig {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorConfig.class);

    @Value("${async.executor.thread.core_pool_size}")
    private int corePoolSize;
    @Value("${async.executor.thread.max_pool_size}")
    private int maxPoolSize;
    @Value("${async.executor.thread.queue_capacity}")
    private int queueCapacity;
    @Value("${async.executor.thread.name.prefix}")
    private String namePrefix;

    /**
     *@描述 创建ack
     *@参数  []
     *@返回值  java.util.concurrent.Executor
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/4/22
     *@修改人和其它信息
     */
    @Bean(name = "ackAsyncServiceExecutor")
    public Executor ackAsyncServiceExecutor() {
        logger.info("start ackAsyncServiceExecutor");
        ThreadPoolTaskExecutor executor = createExcutor();
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

            }
        });
        //执行初始化
        executor.initialize();
        return executor;
    }

    /**
     *@描述 解析ack
     *@参数  []
     *@返回值  java.util.concurrent.Executor
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/4/22
     *@修改人和其它信息
     */
    @Bean(name = "parseAckAsyncServiceExecutor")
    public Executor parseAckAsyncServiceExecutor() {
        logger.info("start parseAckAsyncServiceExecutor");
        ThreadPoolTaskExecutor executor = createExcutor();
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

            }
        });
        //执行初始化
        executor.initialize();
        return executor;
    }


    @Bean(name = "taskAsyncPool")
    public Executor taskAsyncServiceExecutor() {
        logger.info("start taskAsyncServiceExecutor");
        ThreadPoolTaskExecutor executor = createExcutor();
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

            }
        });
        //执行初始化
        executor.initialize();
        return executor;
    }


    @Bean(name = "ftpMonitorPool")
    public Executor ftpMonitorTask() {
        logger.info("start ftpMonitorTask");
        ThreadPoolTaskExecutor executor = createExcutor();
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

            }
        });
        //执行初始化
        executor.initialize();
        return executor;
    }

    // 构建线程池
    public ThreadPoolTaskExecutor createExcutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(corePoolSize);
        //配置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //配置队列大小
        executor.setQueueCapacity(queueCapacity);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(namePrefix);

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        return executor;
    }
}
