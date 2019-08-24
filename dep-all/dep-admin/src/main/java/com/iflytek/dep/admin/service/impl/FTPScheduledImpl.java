package com.iflytek.dep.admin.service.impl;


import com.iflytek.dep.admin.constants.MonitorType;
import com.iflytek.dep.admin.dao.FTPConfigMapper;
import com.iflytek.dep.admin.model.FTPConfig;
import com.iflytek.dep.admin.service.FTPScheduledService;
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
 * @Description: FTPScheduledImpl
 * @date 2019/2/27--19:20
 */
@Primary
@Service("ftpScheduledService")
public class FTPScheduledImpl implements FTPScheduledService {
    private Logger logger = LoggerFactory.getLogger(FTPScheduledImpl.class);

    @Autowired
    PropertiesUtils propertiesUtil;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private FTPConfigMapper ftpConfigMapper;

    @Override
    public boolean start(String ftpId, MonitorType type) {
        // 1.参数校验
        if (ftpId == null || type == null) {
            logger.info("ftpId : {},type : {} 不能为空", ftpId, type);
            return false;
        }
        FTPConfig ftpConfig = ftpConfigMapper.selectByPrimaryKey(ftpId);
        if (ftpConfig == null) {
            logger.info("\nftpId : {} 无效", ftpId);
            return false;
        }
        ftpId = ftpConfig.getFtpId();
        String ftpIp = ftpConfig.getFtpIp();
        String nodeId = ftpConfig.getNodeId();
        //MonitorType ftpMonitorType = MonitorType.getType(type);

        String[] scheduledClass = getScheduledClass(type);

        if (scheduledClass[0] == null) {
            logger.info("\n未配置ftpId: {} 任务", ftpId);
            return false;
        }
        try {
            Class c = Class.forName(scheduledClass[0]);
            Constructor constructor = c.getConstructor(FTPConfig.class);
            Runnable runnable = (Runnable) constructor.newInstance(ftpConfig);
            QuartzScheduler quartzScheduler = ScheduledFutureFactory.getQuartzScheduler(ftpId+"_"+type.getValue());
            if (quartzScheduler == null) {
                quartzScheduler = ScheduledFutureFactory.createQuartzScheduler(ftpId+"_"+type.getValue(), runnable, scheduledClass[1],
                        threadPoolTaskScheduler);
            }
            quartzScheduler.start();
            logger.info("\n开启定时任务 schedulerClass: {} ,ftpId: {} 成功", scheduledClass[0], ftpId+"_"+type.getValue());
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    private String[] getScheduledClass(MonitorType type) {
        String[] strArr = new String[2];

        String schedulerClass = null;
        String cron = null;

        switch (type) {
            case FTP_BEAT:
                schedulerClass = "com.iflytek.dep.admin.scheduled.FtpBeatThread";
                cron = propertiesUtil.getPropertiesValue("cron.ftp.beat");
                break;
            case FTP_FILE_DEL:
                schedulerClass = "com.iflytek.dep.admin.scheduled.FtpFileDeleteThread";
                cron = propertiesUtil.getPropertiesValue("cron.ftp.file.del");
                break;
            case FTP_FILE_MOV:
                schedulerClass = "com.iflytek.dep.admin.scheduled.FtpFileMoveThread";
                cron = propertiesUtil.getPropertiesValue("cron.ftp.file.mov");
                break;
            default:
                break;
        }

        strArr[0] = schedulerClass;
        strArr[1] = cron;

        return strArr;
    }

    @Override
    public boolean close(String ftpId, MonitorType type) {
        // 1.参数校验
        if (ftpId == null || type == null) {
            logger.info("\nftpId : {},taskType : {} 不能为空", ftpId, type);
            return false;
        }
        FTPConfig ftpConfig = ftpConfigMapper.selectByPrimaryKey(ftpId);
        if (ftpConfig == null) {
            logger.info("\nftpId : {} 无效", ftpId);
            return false;
        }
        ftpId = ftpConfig.getFtpId();
        String ftpIp = ftpConfig.getFtpIp();
        String nodeId = ftpConfig.getNodeId();
        //MonitorType ftpMonitorType = MonitorType.getType(type);

        String[] scheduledClass = getScheduledClass(type);

        if (scheduledClass[0] == null) {
            logger.info("\n未配置ftpId: {} 任务", ftpId);
            return false;
        }
        // 2.关闭任务
        try {
            Class c = Class.forName(scheduledClass[0]);
            Constructor constructor = c.getConstructor(FTPConfig.class);
            Runnable runnable = (Runnable) constructor.newInstance(ftpConfig);
            QuartzScheduler quartzScheduler = ScheduledFutureFactory.getQuartzScheduler(ftpId+"_"+type.getValue());
            if (quartzScheduler == null) {
                quartzScheduler = ScheduledFutureFactory.createQuartzScheduler(ftpId+"_"+type.getValue(), runnable, scheduledClass[1],
                        threadPoolTaskScheduler);
            }
            quartzScheduler.stop();
            logger.info("\n关闭定时任务 schedulerClass: {} ,ftpId: {} 成功", scheduledClass[0], ftpId+"_"+type.getValue());
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }
}
