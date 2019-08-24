package com.iflytek.dep.admin.model;


import java.util.Date;

/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.model
 * @Description: DEPServerMonitorBean
 * @date 2019/2/28--19:57
 */
public class DEPServerMonitorBean {

    private String depServerId;

    private String depServerIp;

    private Date probeTime;

    private String probeResult;

    public String getDepServerId() { return depServerId; }

    public void setDepServerId(String depServerId) { this.depServerId = depServerId; }

    public String getDepServerIp() { return depServerIp; }

    public void setDepServerIp(String depServerIp) { this.depServerIp = depServerIp; }

    public Date getProbeTime() { return probeTime; }

    public void setProbeTime(Date probeTime) { this.probeTime = probeTime; }

    public String getProbeResult() { return probeResult; }

    public void setProbeResult(String probeResult) { this.probeResult = probeResult; }
}