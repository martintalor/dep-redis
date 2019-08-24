package com.iflytek.dep.admin.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description:
 * @date 2019/3/1--17:02
 */
public class NodeLinkVo {

    private String linkId;

    private String packageId;

    private String toNodeId;

    private String toNodeRemark;

    private String leftNodeId;

    private String leftNodeRemark;

    private String leftNodeType;

    private String rightNodeId;

    private String rightNodeRemark;

    private String serverNodeId;

    // 是否可点击查看
    private boolean flagClick;

    private int orderId;

    private String sendStateDm;

    private String sendStateMc;

    private String operateStateDm;

    private String operateStateMc;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
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

    public String getLeftNodeId() {
        return leftNodeId;
    }

    public void setLeftNodeId(String leftNodeId) {
        this.leftNodeId = leftNodeId;
    }

    public String getLeftNodeRemark() {
        return leftNodeRemark;
    }

    public void setLeftNodeRemark(String leftNodeRemark) {
        this.leftNodeRemark = leftNodeRemark;
    }

    public String getLeftNodeType() {
        return leftNodeType;
    }

    public void setLeftNodeType(String leftNodeType) {
        this.leftNodeType = leftNodeType;
    }

    public String getRightNodeId() {
        return rightNodeId;
    }

    public void setRightNodeId(String rightNodeId) {
        this.rightNodeId = rightNodeId;
    }

    public String getRightNodeRemark() {
        return rightNodeRemark;
    }

    public void setRightNodeRemark(String rightNodeRemark) {
        this.rightNodeRemark = rightNodeRemark;
    }

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId;
    }

    public boolean isFlagClick() {
        return flagClick;
    }

    public void setFlagClick(boolean flagClick) {
        this.flagClick = flagClick;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getSendStateDm() {
        return sendStateDm;
    }

    public void setSendStateDm(String sendStateDm) {
        this.sendStateDm = sendStateDm;
    }

    public String getSendStateMc() {
        return sendStateMc;
    }

    public void setSendStateMc(String sendStateMc) {
        this.sendStateMc = sendStateMc;
    }

    public String getOperateStateDm() {
        return operateStateDm;
    }

    public void setOperateStateDm(String operateStateDm) {
        this.operateStateDm = operateStateDm;
    }

    public String getOperateStateMc() {
        return operateStateMc;
    }

    public void setOperateStateMc(String operateStateMc) {
        this.operateStateMc = operateStateMc;
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