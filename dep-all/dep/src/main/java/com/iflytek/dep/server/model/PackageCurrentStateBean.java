package com.iflytek.dep.server.model;

import java.util.Date;

public class PackageCurrentStateBean {
    private String packageId;

    private String nodeId;

    private String toNodeId;

    private String processId;

    private String sendStateDm;

    private String operateStateDm;

    private Date createTime;

    private Date updateTime;

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId == null ? null : packageId.trim();
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId == null ? null : nodeId.trim();
    }

    public String getToNodeId() {
        return toNodeId;
    }

    public void setToNodeId(String toNodeId) {
        this.toNodeId = toNodeId == null ? null : toNodeId.trim();
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId == null ? null : processId.trim();
    }

    public String getSendStateDm() {
        return sendStateDm;
    }

    public void setSendStateDm(String sendStateDm) {
        this.sendStateDm = sendStateDm == null ? null : sendStateDm.trim();
    }

    public String getOperateStateDm() {
        return operateStateDm;
    }

    public void setOperateStateDm(String operateStateDm) {
        this.operateStateDm = operateStateDm == null ? null : operateStateDm.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}