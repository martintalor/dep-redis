package com.iflytek.dep.admin.model;

import java.util.Date;

public class MachineMonitor {
    private String monitorId;

    private String nodeId;

    private Date probeTime;

    private String probeResult;

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId == null ? null : monitorId.trim();
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId == null ? null : nodeId.trim();
    }

    public Date getProbeTime() {
        return probeTime;
    }

    public void setProbeTime(Date probeTime) {
        this.probeTime = probeTime;
    }

    public String getProbeResult() {
        return probeResult;
    }

    public void setProbeResult(String probeResult) {
        this.probeResult = probeResult == null ? null : probeResult.trim();
    }
}