package com.iflytek.dep.server.service.impl;

import com.iflytek.dep.common.scheduled.QuartzScheduler;
import com.iflytek.dep.common.utils.PropertiesUtils;
import com.iflytek.dep.common.utils.ScheduledFutureFactory;
import com.iflytek.dep.server.controller.CreatePackController;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.redis.RedissonService;
import com.iflytek.dep.server.service.AckRetryServerScheduled;
import com.iflytek.dep.server.service.dataPack.CreatePackService;
import com.iflytek.dep.server.service.dataPack.GetPackService;
import com.iflytek.dep.server.utils.FileConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.service.impl
 * @Description:
 * @date 2019/6/4--22:24
 */
@Primary
@Service("AckReteyScheduledService")
public class AckRetryServerScheduledImpl implements AckRetryServerScheduled {
    private Logger logger = LoggerFactory.getLogger(AckRetryServerScheduledImpl.class);
    @Autowired
    PropertiesUtils propertiesUtil;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private GetPackService getPackService;

    @Autowired
    private NodeAppBeanMapper nodeAppBeanMapper;

    @Autowired
    private CreatePackController createPackController;

    @Autowired
    private CreatePackService createPackService;

    @Autowired
    private RedissonService redissonService;

    @Override
    public boolean start() {
        String schedulerClass = "com.iflytek.dep.server.scheduled.AckRetryThread";
        String cron = FileConfigUtil.ACKRETRYTIME; //每晚八点半的定时任务
        //String cron = "0/10 * * * * ?"; //每10秒执行一次
        //propertiesUtil.getPropertiesValue("cron.dep.server.beat")
        try {
            Class c = Class.forName(schedulerClass);
            Constructor constructor = c.getConstructor(GetPackService.class,NodeAppBeanMapper.class,CreatePackController.class,CreatePackService.class,RedissonService.class);
            Runnable runnable = (Runnable) constructor.newInstance(getPackService,nodeAppBeanMapper,createPackController,createPackService,redissonService);
            QuartzScheduler quartzScheduler = ScheduledFutureFactory.getQuartzScheduler(schedulerClass);
            if (quartzScheduler == null) {
                quartzScheduler = ScheduledFutureFactory.createQuartzScheduler(schedulerClass, runnable,cron ,
                        threadPoolTaskScheduler);
            }
            quartzScheduler.start();
            logger.info("\n开启ack重探定时任务 schedulerClass: {}  成功", schedulerClass);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

    }

    @Override
    public boolean close() {

        //关闭定时任务
        String schedulerClass = "com.iflytek.dep.server.scheduled.AckRetryThread";
        String cron = "0 30 20 * * ?"; //每晚八点半的定时任务
        //propertiesUtil.getPropertiesValue("cron.dep.server.beat")
        try {
            Class c = Class.forName(schedulerClass);
            Constructor constructor = c.getConstructor(GetPackService.class,NodeAppBeanMapper.class,CreatePackController.class,CreatePackService.class,RedissonService.class);
            Runnable runnable = (Runnable) constructor.newInstance(getPackService,nodeAppBeanMapper,createPackController,createPackService,redissonService);
            QuartzScheduler quartzScheduler = ScheduledFutureFactory.getQuartzScheduler(schedulerClass);
            if (quartzScheduler == null) {
                quartzScheduler = ScheduledFutureFactory.createQuartzScheduler(schedulerClass, runnable,cron ,
                        threadPoolTaskScheduler);
            }
            quartzScheduler.stop();
            logger.info("\n关闭ack重探定时任务 schedulerClass: {}  成功", schedulerClass);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }
}
