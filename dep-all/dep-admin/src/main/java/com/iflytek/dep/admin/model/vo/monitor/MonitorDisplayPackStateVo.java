package com.iflytek.dep.admin.model.vo.monitor;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 操作记录VO
 * Created by xiliu5 on 2019/3/3.
 */
public class MonitorDisplayPackStateVo {
    private String nodeId;
    private String nodeRemark;
    private String operateStateDm;
    private String sendStateDm;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private long transTime;

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

    /**
     * 计算耗时
     * @return
     */
    public long getTransTime() {
        this.transTime = updateTime.getTime() - createTime.getTime();
        return transTime;
    }

    public void setTransTime(long transTime) {
        this.transTime = transTime;
    }

    public String getOperateStateDm() {
        return operateStateDm;
    }

    public void setOperateStateDm(String operateStateDm) {
        this.operateStateDm = operateStateDm;
    }

    public String getSendStateDm() {
        return sendStateDm;
    }

    public void setSendStateDm(String sendStateDm) {
        this.sendStateDm = sendStateDm;
    }
}
