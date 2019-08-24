package com.iflytek.dep.server.model;

public class MachineNodeBean {
    private String nodeId;

    private String machineIp;

    private String serverNodeId;

    private String nodeRemark;

    private String nodeTypeDm;

    private String flagEnable;

    private String flagDelete;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId == null ? null : nodeId.trim();
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp == null ? null : machineIp.trim();
    }

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId == null ? null : serverNodeId.trim();
    }

    public String getNodeRemark() {
        return nodeRemark;
    }

    public void setNodeRemark(String nodeRemark) {
        this.nodeRemark = nodeRemark == null ? null : nodeRemark.trim();
    }

    public String getNodeTypeDm() {
        return nodeTypeDm;
    }

    public void setNodeTypeDm(String nodeTypeDm) {
        this.nodeTypeDm = nodeTypeDm == null ? null : nodeTypeDm.trim();
    }

    public String getFlagEnable() {
        return flagEnable;
    }

    public void setFlagEnable(String flagEnable) {
        this.flagEnable = flagEnable == null ? null : flagEnable.trim();
    }

    public String getFlagDelete() {
        return flagDelete;
    }

    public void setFlagDelete(String flagDelete) {
        this.flagDelete = flagDelete == null ? null : flagDelete.trim();
    }
}