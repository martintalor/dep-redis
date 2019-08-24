package com.iflytek.dep.server.model;

import java.util.Date;

public class DataNodeProcessBean {
    private String processId;

    private String packageId;

    private String nodeId;

    private String toNodeId;

    private Date createTime;

    private String fromNodeId;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId == null ? null : processId.trim();
    }

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getFromNodeId() {
        return fromNodeId;
    }

    public void setFromNodeId(String fromNodeId) {
        this.fromNodeId = fromNodeId == null ? null : fromNodeId.trim();
    }

    @Override
    public String toString() {
        return "DataNodeProcessBean{" +
                "processId='" + processId + '\'' +
                ", packageId='" + packageId + '\'' +
                ", nodeId='" + nodeId + '\'' +
                ", toNodeId='" + toNodeId + '\'' +
                ", createTime=" + createTime +
                ", fromNodeId='" + fromNodeId + '\'' +
                '}';
    }
}