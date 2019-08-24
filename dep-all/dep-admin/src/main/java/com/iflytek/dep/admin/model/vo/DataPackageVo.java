package com.iflytek.dep.admin.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description: DataPackageVo
 * @date 2019/2/28--19:57
 */
public class DataPackageVo {
    private String packageId;

    private BigDecimal packageSize;

    private String globalStateDm;

    private String globalStateMc;

    private String nodeId;

    private String nodeRemark;

    private String serverNodeId;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private int isUnpack;   // 0未拆包  1拆包

    private int isRetry;    // 0不可以重试 1可以重试

    private long spendTime;

    private String toNodeId;

    private String toNodeRemark;

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public BigDecimal getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(BigDecimal packageSize) {
        this.packageSize = packageSize;
    }

    public String getGlobalStateDm() {
        return globalStateDm;
    }

    public void setGlobalStateDm(String globalStateDm) {
        this.globalStateDm = globalStateDm;
    }

    public String getGlobalStateMc() {
        return globalStateMc;
    }

    public void setGlobalStateMc(String globalStateMc) {
        this.globalStateMc = globalStateMc;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeRemark() {
        return nodeRemark;
    }

    public void setNodeRemark(String nodeRemark) {
        this.nodeRemark = nodeRemark;
    }

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId;
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

    public int getIsUnpack() {
        return isUnpack;
    }

    public void setIsUnpack(int isUnpack) {
        this.isUnpack = isUnpack;
    }

    public int getIsRetry() {
        return isRetry;
    }

    public void setIsRetry(int isRetry) {
        this.isRetry = isRetry;
    }

    public long getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(long spendTime) {
        this.spendTime = spendTime;
    }

    public String getToNodeId() {
        return toNodeId;
    }

    public void setToNodeId(String toNodeId) {
        this.toNodeId = toNodeId;
    }

    public String getToNodeRemark() {
        return toNodeRemark;
    }

    public void setToNodeRemark(String toNodeRemark) {
        this.toNodeRemark = toNodeRemark;
    }
}