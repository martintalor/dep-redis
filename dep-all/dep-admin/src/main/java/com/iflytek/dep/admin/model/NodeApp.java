package com.iflytek.dep.admin.model;

public class NodeApp {
    private String appId;

    private String nodeId;

    private String calUrl;

    private String appName;

    private String appRemark;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId == null ? null : nodeId.trim();
    }

    public String getCalUrl() {
        return calUrl;
    }

    public void setCalUrl(String calUrl) {
        this.calUrl = calUrl == null ? null : calUrl.trim();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    public String getAppRemark() {
        return appRemark;
    }

    public void setAppRemark(String appRemark) {
        this.appRemark = appRemark == null ? null : appRemark.trim();
    }
}