package com.iflytek.dep.server.model;

public class NodeRouteBean {
    private String leftNodeId;

    private String rightNodeId;

    private String leftServerNode;

    private String rightServerNode;

    private String routeType;

    public String getLeftNodeId() {
        return leftNodeId;
    }

    public void setLeftNodeId(String leftNodeId) {
        this.leftNodeId = leftNodeId == null ? null : leftNodeId.trim();
    }

    public String getRightNodeId() {
        return rightNodeId;
    }

    public void setRightNodeId(String rightNodeId) {
        this.rightNodeId = rightNodeId == null ? null : rightNodeId.trim();
    }

    public String getLeftServerNode() {
        return leftServerNode;
    }

    public void setLeftServerNode(String leftServerNode) {
        this.leftServerNode = leftServerNode == null ? null : leftServerNode.trim();
    }

    public String getRightServerNode() {
        return rightServerNode;
    }

    public void setRightServerNode(String rightServerNode) {
        this.rightServerNode = rightServerNode == null ? null : rightServerNode.trim();
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }


    @Override
    public String toString() {
        return "NodeRouteBean{" +
                "leftNodeId='" + leftNodeId + '\'' +
                ", rightNodeId='" + rightNodeId + '\'' +
                ", leftServerNode='" + leftServerNode + '\'' +
                ", rightServerNode='" + rightServerNode + '\'' +
                ", routeType='" + routeType + '\'' +
                '}';
    }
}