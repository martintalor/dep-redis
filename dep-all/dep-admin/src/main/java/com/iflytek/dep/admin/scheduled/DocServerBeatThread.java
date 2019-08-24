package com.iflytek.dep.admin.scheduled;

import com.iflytek.dep.admin.dao.FtpMonitorBeanMapper;
import com.iflytek.dep.admin.model.FtpMonitorBean;
import com.iflytek.dep.admin.model.MachineNode;
import com.iflytek.dep.common.utils.SpringUtil;
import com.iflytek.dep.common.utils.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Date;


/**
 * 文档服务器探测
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.server.scheduled
 * @Description:
 * @date 2019/3/13
 */
public class DocServerBeatThread implements Runnable {

    private Logger logger = LoggerFactory.getLogger(DocServerBeatThread.class);
    MachineNode machineNodeBean;

    public DocServerBeatThread(MachineNode machineNodeBean) {
        this.machineNodeBean = machineNodeBean;
    }

    @Override
    public void run() {
        try {
            String result = ping(machineNodeBean.getMachineIp()) ? "Y" : "N";
            FtpMonitorBeanMapper ftpMonitorBeanMapper = SpringUtil.getBean(FtpMonitorBeanMapper.class);
            FtpMonitorBean record = ftpMonitorBeanMapper.selectDocServerMonitorByNodeId(machineNodeBean.getNodeId());
            if (null == record) {
                record = new FtpMonitorBean();
                record.setFtpId(UUIDGenerator.createUUID());
                record.setMachineIp(machineNodeBean.getMachineIp());
                record.setNodeId(machineNodeBean.getNodeId());
                record.setProbeResult(result);
                record.setProbeTime(new Date());
                record.setServerType("01");
                ftpMonitorBeanMapper.insert(record);
            } else {
                record.setMachineIp(machineNodeBean.getMachineIp());
                record.setNodeId(machineNodeBean.getNodeId());
                record.setProbeResult(result);
                record.setProbeTime(new Date());
                record.setServerType("01");
                ftpMonitorBeanMapper.updateByPrimaryKey(record);
            }
            logger.info("\nDocServerBeatThread 执行成功");
        } catch (Exception e) {
            logger.error("\nDocServerBeatThread 执行失败: " + e.getLocalizedMessage());
        }
    }

    /**
     * 使用时应注意，如果远程服务器设置了防火墙或相关的配制，可能会影响到结果
     * @param ipAddress
     * @return
     * @throws Exception
     */
    private static boolean ping(String ipAddress) throws Exception {
        int  timeOut =  3000 ;  //超时应该在3钞以上
        boolean status = InetAddress.getByName(ipAddress).isReachable(timeOut);
        // 当返回值是true时，说明host是可用的，false则不可。
        return status;
    }

}