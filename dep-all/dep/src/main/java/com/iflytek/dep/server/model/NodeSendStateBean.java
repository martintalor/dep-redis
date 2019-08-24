package com.iflytek.dep.server.model;

import java.util.Date;

public class NodeSendStateBean {
    private String uuid;

    private String processId;

    private String sendStateDm;

    private Short orderId;

    private Date createTime;

    private Date updateTime;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
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

    public Short getOrderId() {
        return orderId;
    }

    public void setOrderId(Short orderId) {
        this.orderId = orderId;
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