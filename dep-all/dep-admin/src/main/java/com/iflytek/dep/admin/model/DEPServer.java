package com.iflytek.dep.admin.model;

public class DEPServer {
    private String depServerId;

    private String depServerRemark;

    private String serverNodeId;

    private String depServerIp;

    private String flagDelete;

    public String getDepServerId() {
        return depServerId;
    }

    public void setDepServerId(String depServerId) {
        this.depServerId = depServerId == null ? null : depServerId.trim();
    }

    public String getDepServerRemark() {
        return depServerRemark;
    }

    public void setDepServerRemark(String depServerRemark) {
        this.depServerRemark = depServerRemark == null ? null : depServerRemark.trim();
    }

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId == null ? null : serverNodeId.trim();
    }

    public String getDepServerIp() {
        return depServerIp;
    }

    public void setDepServerIp(String depServerIp) {
        this.depServerIp = depServerIp == null ? null : depServerIp.trim();
    }

    public String getFlagDelete() {
        return flagDelete;
    }

    public void setFlagDelete(String flagDelete) {
        this.flagDelete = flagDelete == null ? null : flagDelete.trim();
    }
}