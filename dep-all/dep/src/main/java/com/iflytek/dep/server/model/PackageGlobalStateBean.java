package com.iflytek.dep.server.model;

import java.util.Date;

public class PackageGlobalStateBean {
    private String packageId;

    private String toNodeId;

    private String nodeId;

    private String globalStateDm;

    private Date createTime;

    private Date updateTime;

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId == null ? null : packageId.trim();
    }

    public String getToNodeId() {
        return toNodeId;
    }

    public void setToNodeId(String toNodeId) {
        this.toNodeId = toNodeId == null ? null : toNodeId.trim();
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId == null ? null : nodeId.trim();
    }

    public String getGlobalStateDm() {
        return globalStateDm;
    }

    public void setGlobalStateDm(String globalStateDm) {
        this.globalStateDm = globalStateDm == null ? null : globalStateDm.trim();
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