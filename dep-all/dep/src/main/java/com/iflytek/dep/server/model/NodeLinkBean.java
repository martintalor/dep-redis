package com.iflytek.dep.server.model;

public class NodeLinkBean {
    private String linkId;

    private String packageId;

    private String toNodeId;

    private String leftNodeId;

    private String rightNodeId;

    private Short orderId;

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId == null ? null : linkId.trim();
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId == null ? null : packageId.trim();
    }

    public String getToNodeId() {
        return toNodeId;
    }

    public void setToNodeId(String toNodeId) {
        this.toNodeId = toNodeId == null ? null : toNodeId.trim();
    }

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

    public Short getOrderId() {
        return orderId;
    }

    public void setOrderId(Short orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "NodeLinkBean{" +
                "linkId='" + linkId + '\'' +
                ", packageId='" + packageId + '\'' +
                ", toNodeId='" + toNodeId + '\'' +
                ", leftNodeId='" + leftNodeId + '\'' +
                ", rightNodeId='" + rightNodeId + '\'' +
                ", orderId=" + orderId +
                '}';
    }
}