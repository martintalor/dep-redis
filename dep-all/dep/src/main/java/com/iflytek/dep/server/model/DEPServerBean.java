package com.iflytek.dep.server.model;

/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.model
 * @Description:
 * @date 2019/2/27--19:19
 */
public class DEPServerBean {

    private String depServerId;

    private String depServerRemark;

    private String serverNodeId;

    private String serverNodeRemark;

    private String depServerIp;

    private String  flagDelete;

    public String getDepServerId() { return depServerId; }

    public void setDepServerId(String depServerId) { this.depServerId = depServerId; }

    public String getDepServerRemark() { return depServerRemark; }

    public void setDepServerRemark(String depServerRemark) { this.depServerRemark = depServerRemark; }

    public String getServerNodeId() { return serverNodeId; }

    public void setServerNodeId(String serverNodeId) { this.serverNodeId = serverNodeId; }

    public String getServerNodeRemark() { return serverNodeRemark; }

    public void setServerNodeRemark(String serverNodeRemark) { this.serverNodeRemark = serverNodeRemark; }

    public String getDepServerIp() { return depServerIp; }

    public void setDepServerIp(String depServerIp) { this.depServerIp = depServerIp; }

    public String getFlagDelete() { return flagDelete; }

    public void setFlagDelete(String flagDelete) { this.flagDelete = flagDelete; }
}