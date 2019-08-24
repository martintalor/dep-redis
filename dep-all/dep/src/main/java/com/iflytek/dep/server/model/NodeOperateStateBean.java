package com.iflytek.dep.server.model;

import java.math.BigDecimal;
import java.util.Date;

public class NodeOperateStateBean {
    private String uuid;

    private String processId;

    private String operateStateDm;

    private Short orderId;

    private Date createTime;

    private Date updateTime;

    private BigDecimal spendTime;

    public BigDecimal getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(BigDecimal spendTime) {
        this.spendTime = spendTime;
    }

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

    public String getOperateStateDm() {
        return operateStateDm;
    }

    public void setOperateStateDm(String operateStateDm) {
        this.operateStateDm = operateStateDm == null ? null : operateStateDm.trim();
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