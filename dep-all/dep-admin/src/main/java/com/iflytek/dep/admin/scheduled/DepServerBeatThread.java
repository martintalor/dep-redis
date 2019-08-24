package com.iflytek.dep.admin.scheduled;

import com.iflytek.dep.admin.dao.ServerMonitorBeanMapper;
import com.iflytek.dep.admin.model.DEPServer;
import com.iflytek.dep.admin.model.DEPServerMonitorBean;
import com.iflytek.dep.common.utils.HttpUtil;
import com.iflytek.dep.common.utils.PropertiesUtils;
import com.iflytek.dep.common.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.monitor
 * @Description:
 * @date 2019/2/28--10:40
 */
public class DepServerBeatThread implements Runnable {

    private Logger logger = LoggerFactory.getLogger(DepServerBeatThread.class);
    DEPServer depServerBean;

    public DepServerBeatThread(DEPServer depServerBean) {
        this.depServerBean = depServerBean;
    }

    @Override
    public void run() {
        try {
            String depServerId = depServerBean.getDepServerId();
            String nodeId = depServerBean.getServerNodeId();
            String depServerIpIp = depServerBean.getDepServerIp();
            String flagDelete = depServerBean.getFlagDelete();

            PropertiesUtils propertiesUtil = SpringUtil.getBean(PropertiesUtils.class);
            String beatUrl = depServerIpIp + "/service/beat";
            String result = HttpUtil.sendGet(beatUrl);
            ServerMonitorBeanMapper serverMonitorBeanMapper = SpringUtil.getBean(ServerMonitorBeanMapper.class);
            DEPServerMonitorBean record = serverMonitorBeanMapper.selectByPrimaryKey(depServerId);
            if (null == record) {
                record = new DEPServerMonitorBean();
                record.setDepServerId(depServerId);
                record.setDepServerIp(depServerIpIp);
                record.setProbeResult(result.equalsIgnoreCase("error")?"N":"Y");
                record.setProbeTime(new Date());
                serverMonitorBeanMapper.insert(record);
            } else {
                record.setDepServerId(depServerId);
                record.setDepServerIp(depServerIpIp);
                record.setProbeResult(result.equalsIgnoreCase("error")?"N":"Y");
                record.setProbeTime(new Date());
                serverMonitorBeanMapper.updateByPrimaryKey(record);
            }
            logger.info("\nDepServerBeatThread 执行成功");
        } catch (Exception e) {
            logger.error("\nDepServerBeatThread 执行失败: " + e.getLocalizedMessage());
        }
    }
}