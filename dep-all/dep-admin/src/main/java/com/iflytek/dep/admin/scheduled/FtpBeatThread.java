package com.iflytek.dep.admin.scheduled;


import com.iflytek.dep.admin.dao.FtpMonitorBeanMapper;
import com.iflytek.dep.admin.model.FTPConfig;
import com.iflytek.dep.admin.model.FtpMonitorBean;
import com.iflytek.dep.admin.utils.FTPUtils;
import com.iflytek.dep.common.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * ftp心跳检测定时任务线程
 *
 * @author dzr
 */
public class FtpBeatThread implements Runnable {
    private Logger logger = LoggerFactory.getLogger(FtpBeatThread.class);
    FTPConfig ftpConfig;

    public FtpBeatThread(FTPConfig ftpConfig) {
        this.ftpConfig = ftpConfig;
    }

    @Override
    public void run() {
        try {
            String ftpId = ftpConfig.getFtpId();
            String nodeId = ftpConfig.getNodeId();
            String ftpIp = ftpConfig.getFtpIp();
            String userName = ftpConfig.getUsername();
            String password = ftpConfig.getPassword();
            int ftpPort = ftpConfig.getFtpPort();
            boolean result = FTPUtils.validateConnect(ftpIp, ftpPort, userName, password);
            String status = result ? "Y" : "N";
            FtpMonitorBeanMapper ftpMonitorBeanMapper = SpringUtil.getBean(FtpMonitorBeanMapper.class);
            FtpMonitorBean record = ftpMonitorBeanMapper.selectByPrimaryKey(ftpId);
            if (null == record) {
                record = new FtpMonitorBean();
                record.setFtpId(ftpId);
                record.setMachineIp(ftpIp);
                record.setNodeId(nodeId);
                record.setProbeResult(status);
                record.setProbeTime(new Date());
                record.setServerType("02");
                ftpMonitorBeanMapper.insert(record);
            } else {
                record.setMachineIp(ftpIp);
                record.setNodeId(nodeId);
                record.setProbeResult(status);
                record.setProbeTime(new Date());
                record.setServerType("02");
                ftpMonitorBeanMapper.updateByPrimaryKey(record);
            }
            logger.info("\nFtpBeatThread 执行成功, ftpIp="+ftpConfig.getFtpIp() );
        } catch (Exception e) {
            logger.error("\nFtpBeatThread 执行失败, ftpIp="+ftpConfig.getFtpIp() + ":"+ e.getLocalizedMessage());
        }
    }

}