package com.iflytek.dep.admin.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo.monitor
 * @Description: OperateStateVo 操作状态信息
 * @date 2019/3/12--14:57
 */
public class OperateStateVo {

    private String id;

    /**
     * 操作code
     */
    private String operateStateDm;

    /**
     * 操作描述
     */
    private String operateStateMc;

    private String toNodeId;

    private String toNodeRemark;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private Long spendTime;

    private int orderId;

    private int state = 1;// 默认成功1

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Long getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(Long spendTime) {
        this.spendTime = spendTime;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}