package com.iflytek.dep.admin.scheduled;


import com.iflytek.dep.admin.constants.MonitorType;
import com.iflytek.dep.admin.model.DEPServer;
import com.iflytek.dep.admin.model.FTPConfig;
import com.iflytek.dep.admin.model.MachineNode;
import com.iflytek.dep.admin.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.monitor
 * @Description:
 * @date 2019/2/28--10:40
 */
@Component
public class SpringListener implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(SpringListener.class);

    @Value("${logicServerNode.serverNodeId}")
    private String serverNodeId;

    @Resource(name="ftpConfigService")
    FTPConfigService ftpConfigService;

    @Resource(name="depServerService")
    DEPServerService depServerService;

    @Resource(name="depServerScheduledService")
    DEPServerScheduledService serverMonitorTaskService;

    @Resource(name="ftpScheduledService")
    FTPScheduledService ftpScheduledService;

    @Resource(name="docServerService")
    DocServerScheduledService docServerScheduledService;

    @Override
    public void afterPropertiesSet() throws Exception {
        //ftp服务器探测
        List<FTPConfig> ftpConfigList = ftpConfigService.listFTPConfig(serverNodeId);
        for (FTPConfig ftpConfig : ftpConfigList) {
            if (Objects.nonNull(ftpConfig)) {
                String ftpId = ftpConfig.getFtpId();
                MonitorType type = MonitorType.FTP_BEAT;
                ftpScheduledService.start(ftpId, type);
                type = MonitorType.FTP_FILE_DEL;
                ftpScheduledService.start(ftpId, type);
                type = MonitorType.FTP_FILE_MOV;
                ftpScheduledService.start(ftpId, type);
            } else {
                logger.info("\nftp未配置，心跳检测|删除文件|移动备份文件定时任务未启动");
            }
        }

        //文档服务器探测
        List<MachineNode> nodeBeanList = ftpConfigService.listDocServer(serverNodeId);
        for (MachineNode nodeBean : nodeBeanList) {
            docServerScheduledService.start(nodeBean.getNodeId());
        }

        //DEP-SERVER探测
        List<DEPServer> depServerList = depServerService.listDEPServer(serverNodeId);
        for (DEPServer depServerBean : depServerList) {
            if (Objects.nonNull(depServerBean)) {
                String depServerId = depServerBean.getDepServerId();
                serverMonitorTaskService.start(depServerId);
            } else {
                logger.info("\ndepServer未配置，心跳检测定时任务未启动");
            }
        }

        //定时任务-本地文件删除
        serverMonitorTaskService.startDelLocalFile();
    }
}