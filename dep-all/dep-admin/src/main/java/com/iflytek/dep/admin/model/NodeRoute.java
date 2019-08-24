package com.iflytek.dep.admin.model;

import java.util.Date;

public class NodeRoute {
    private String leftNodeId;
    private String rightNodeId;
    private String leftServerNode;
    private String rightServerNode;
    private String routeName;
    private Date createTime;

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

}