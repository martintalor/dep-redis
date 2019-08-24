package com.iflytek.dep.admin.service.impl;


import com.iflytek.dep.admin.dao.DEPServerMapper;
import com.iflytek.dep.admin.model.DEPServer;
import com.iflytek.dep.admin.service.DEPServerScheduledService;
import com.iflytek.dep.common.scheduled.QuartzScheduler;
import com.iflytek.dep.common.utils.PropertiesUtils;
import com.iflytek.dep.common.utils.ScheduledFutureFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;

/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.service.impl
 * @Description: DEPServerScheduledImpl
 * @date 2019/2/27--19:20
 */
@Primary
@Service("depServerScheduledService")
public class DEPServerScheduledImpl implements DEPServerScheduledService {
    private Logger logger = LoggerFactory.getLogger(DEPServerScheduledImpl.class);

    @Autowired
    PropertiesUtils propertiesUtil;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private DEPServerMapper depServerMapper;

    @Override
    public boolean start(String depServerId) {
        // 1.参数校验
        if (depServerId == null) {
            logger.info("\ndepServerId : {} 不能为空", depServerId);
            return false;
        }
        DEPServer depServerBean = depServerMapper.selectByPrimaryKey(depServerId);
        if (depServerBean == null) {
            logger.info("\ndepServerId : {} 无效", depServerId);
            return false;
        }
        depServerId = depServerBean.getDepServerId();
        String depServerIp = depServerBean.getDepServerIp();
        String schedulerClass = "com.iflytek.dep.admin.scheduled.DepServerBeatThread";
//        String cron = "*/5 * * * * ?";
        try {
            Class c = Class.forName(schedulerClass);
            Constructor constructor = c.getConstructor(DEPServer.class);
            Runnable runnable = (Runnable) constructor.newInstance(depServerBean);
            QuartzScheduler quartzScheduler = ScheduledFutureFactory.getQuartzScheduler(depServerId);
            if (quartzScheduler == null) {
                quartzScheduler = ScheduledFutureFactory.createQuartzScheduler(depServerId, runnable, propertiesUtil.getPropertiesValue("cron.dep.server.beat"),
                        threadPoolTaskScheduler);
            }
            quartzScheduler.start();
            logger.info("\n开启定时任务 schedulerClass: {} ,depServerId: {} 成功", schedulerClass, depServerId);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean close(String depServerId) {
        // 1.参数校验
        if (null == depServerId) {
            logger.info("\ndepServerId : {} 不能为空", depServerId);
            return false;
        }
        DEPServer depServerBean = depServerMapper.selectByPrimaryKey(depServerId);
        if (depServerBean == null) {
            logger.info("\ndepServerId : {} 无效", depServerId);
            return false;
        }
        depServerId = depServerBean.getDepServerId();
        String depServerIp = depServerBean.getDepServerIp();
        String schedulerClass = "com.iflytek.dep.admin.scheduled.DepServerBeatThread";
//        String cron = "*/5 * * * * ?";
        // 2.关闭任务
        try {
            Class c = Class.forName(schedulerClass);
            Constructor constructor = c.getConstructor(DEPServer.class);
            Runnable runnable = (Runnable) constructor.newInstance(depServerBean);
            QuartzScheduler quartzScheduler = ScheduledFutureFactory.getQuartzScheduler(depServerId);
            if (quartzScheduler == null) {
                quartzScheduler = ScheduledFutureFactory.createQuartzScheduler(schedulerClass, runnable, propertiesUtil.getPropertiesValue("cron.dep.server.beat"),
                        threadPoolTaskScheduler);
            }
            quartzScheduler.stop();
            logger.info("\n关闭定时任务 schedulerClass: {} ,depServerId: {} 成功", schedulerClass, depServerId);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean startDelLocalFile() {
        String schedulerClass = "com.iflytek.dep.admin.scheduled.LocalFileDeleteThread";

        try {
            Class c = Class.forName(schedulerClass);
            Constructor constructor = c.getConstructor();
            Runnable runnable = (Runnable) constructor.newInstance();
            QuartzScheduler quartzScheduler = ScheduledFutureFactory.getQuartzScheduler("LocalFileDeleteThread");
            if (quartzScheduler == null) {
                quartzScheduler = ScheduledFutureFactory.createQuartzScheduler("LocalFileDeleteThread", runnable, propertiesUtil.getPropertiesValue("cron.local.file.del"),
                        threadPoolTaskScheduler);
            }
            quartzScheduler.start();
            logger.info("\n开启定时任务 schedulerClass: {} 成功", schedulerClass, "LocalFileDeleteThread");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean closeDelLocalFile() {
        String schedulerClass = "com.iflytek.dep.admin.scheduled.LocalFileDeleteThread";

        try {
            Class c = Class.forName(schedulerClass);
            Constructor constructor = c.getConstructor();
            Runnable runnable = (Runnable) constructor.newInstance();
            QuartzScheduler quartzScheduler = ScheduledFutureFactory.getQuartzScheduler("LocalFileDeleteThread");
            if (quartzScheduler == null) {
                quartzScheduler = ScheduledFutureFactory.createQuartzScheduler("LocalFileDeleteThread", runnable, propertiesUtil.getPropertiesValue("cron.local.file.del"),
                        threadPoolTaskScheduler);
            }
            quartzScheduler.stop();
            logger.info("\n关闭定时任务 schedulerClass: {} 成功", schedulerClass, "LocalFileDeleteThread");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }
}
