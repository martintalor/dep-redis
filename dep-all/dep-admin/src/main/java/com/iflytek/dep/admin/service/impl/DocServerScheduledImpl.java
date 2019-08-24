package com.iflytek.dep.admin.service.impl;


import com.iflytek.dep.admin.dao.MachineNodeMapper;
import com.iflytek.dep.admin.model.MachineNode;
import com.iflytek.dep.admin.service.DocServerScheduledService;
import com.iflytek.dep.common.scheduled.QuartzScheduler;
import com.iflytek.dep.common.utils.PropertiesUtils;
import com.iflytek.dep.common.utils.ScheduledFutureFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;

/**
 * 文档服务器定时任务服务
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.server.service.impl
 * @Description: DocServerScheduledImpl
 * @date 2019/3/13
 */
@Service("docServerService")
public class DocServerScheduledImpl implements DocServerScheduledService {
    private Logger logger = LoggerFactory.getLogger(DocServerScheduledImpl.class);

    @Autowired
    PropertiesUtils propertiesUtil;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Autowired
    private MachineNodeMapper machineNodeMapper;


    @Override
    public boolean start(String nodeId) {
        // 1.参数校验
        if (nodeId == null) {
            logger.info("\nnodeId : {} 不能为空", nodeId);
            return false;
        }
        MachineNode nodeBean = machineNodeMapper.selectByPrimaryKey(nodeId);
        if (nodeBean == null) {
            logger.info("\nnodeId : {} 无效", nodeId);
            return false;
        }
        String schedulerClass = "com.iflytek.dep.admin.scheduled.DocServerBeatThread";
        try {
            Class c = Class.forName(schedulerClass);
            Constructor constructor = c.getConstructor(MachineNode.class);
            Runnable runnable = (Runnable) constructor.newInstance(nodeBean);
            QuartzScheduler quartzScheduler = ScheduledFutureFactory.getQuartzScheduler(nodeId);
            if (quartzScheduler == null) {
                quartzScheduler = ScheduledFutureFactory.createQuartzScheduler(nodeId, runnable, propertiesUtil.getPropertiesValue("cron.doc.server.beat"),
                        threadPoolTaskScheduler);
            }
            quartzScheduler.start();
            logger.info("\n开启定时任务 schedulerClass: {} ,nodeId: {} 成功", schedulerClass, nodeId);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean close(String nodeId) {
        // 1.参数校验
        if (null == nodeId) {
            logger.info("\nnodeId : {} 不能为空", nodeId);
            return false;
        }
        MachineNode nodeBean = machineNodeMapper.selectByPrimaryKey(nodeId);
        if (nodeBean == null) {
            logger.info("\nnodeId : {} 无效", nodeId);
            return false;
        }
        String schedulerClass = "com.iflytek.dep.admin.scheduled.DocServerBeatThread";
        // 2.关闭任务
        try {
            Class c = Class.forName(schedulerClass);
            Constructor constructor = c.getConstructor(MachineNode.class);
            Runnable runnable = (Runnable) constructor.newInstance(nodeBean);
            QuartzScheduler quartzScheduler = ScheduledFutureFactory.getQuartzScheduler(nodeId);
            if (quartzScheduler == null) {
                quartzScheduler = ScheduledFutureFactory.createQuartzScheduler(schedulerClass, runnable, propertiesUtil.getPropertiesValue("cron.doc.server.beat"),
                        threadPoolTaskScheduler);
            }
            quartzScheduler.stop();
            logger.info("\n关闭定时任务 schedulerClass: {} ,nodeId: {} 成功", schedulerClass, nodeId);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }
}
