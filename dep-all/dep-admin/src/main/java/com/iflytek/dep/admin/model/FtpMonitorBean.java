package com.iflytek.dep.admin.model;


import java.util.Date;

/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.model
 * @Description: FtpMonitorBean
 * @date 2019/2/28--19:57
 */
public class FtpMonitorBean {

    private String ftpId;

    private String nodeId;

    private String machineIp;

    private Date probeTime;

    private String probeResult;

    /**
     * 服务器类型，01是文档服务器，02是FTP服务器
     */
    private String serverType;

    public String getFtpId() { return ftpId; }

    public void setFtpId(String ftpId) { this.ftpId = ftpId; }

    public String getNodeId() { return nodeId; }

    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getMachineIp() { return machineIp; }

    public void setMachineIp(String machineIp) { this.machineIp = machineIp; }

    public Date getProbeTime() { return probeTime; }

    public void setProbeTime(Date probeTime) { this.probeTime = probeTime; }

    public String getProbeResult() { return probeResult; }

    public void setProbeResult(String probeResult) { this.probeResult = probeResult; }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }
}