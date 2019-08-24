package com.iflytek.dep.admin.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description:
 * @date 2019/2/27
 */
public class NodeRouteVo {
    private String primarykeyStr;
    private String leftNodeId;
    private String rightNodeId;
    private String leftServerNode;
    private String rightServerNode;
    private String routeName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String leftNodeName;
    private String rightNodeName;

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLeftNodeName() {
        return leftNodeName;
    }

    public void setLeftNodeName(String leftNodeName) {
        this.leftNodeName = leftNodeName;
    }

    public String getRightNodeName() {
        return rightNodeName;
    }

    public void setRightNodeName(String rightNodeName) {
        this.rightNodeName = rightNodeName;
    }

    public String getLeftNodeId() {
        return leftNodeId;
    }

    public void setLeftNodeId(String leftNodeId) {
        this.leftNodeId = leftNodeId;
    }

    public String getRightNodeId() {
        return rightNodeId;
    }

    public void setRightNodeId(String rightNodeId) {
        this.rightNodeId = rightNodeId;
    }

    public String getLeftServerNode() {
        return leftServerNode;
    }

    public void setLeftServerNode(String leftServerNode) {
        this.leftServerNode = leftServerNode;
    }

    public String getRightServerNode() {
        return rightServerNode;
    }

    public void setRightServerNode(String rightServerNode) {
        this.rightServerNode = rightServerNode;
    }

    public String getPrimarykeyStr() {
        return leftNodeId + "_" + rightNodeId;
    }

    public void setPrimarykeyStr(String primarykeyStr) {
        this.primarykeyStr = primarykeyStr;
    }
}