package com.iflytek.dep.admin.model;

public class LocalServerNode {
    private String serverNodeId;

    private String serverNodeName;

    private String orgTypeDm;

    private String serverNodeTypeDm;

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId == null ? null : serverNodeId.trim();
    }

    public String getServerNodeName() {
        return serverNodeName;
    }

    public void setServerNodeName(String serverNodeName) {
        this.serverNodeName = serverNodeName == null ? null : serverNodeName.trim();
    }

    public String getOrgTypeDm() {
        return orgTypeDm;
    }

    public void setOrgTypeDm(String orgTypeDm) {
        this.orgTypeDm = orgTypeDm == null ? null : orgTypeDm.trim();
    }

    public String getServerNodeTypeDm() {
        return serverNodeTypeDm;
    }

    public void setServerNodeTypeDm(String serverNodeTypeDm) {
        this.serverNodeTypeDm = serverNodeTypeDm == null ? null : serverNodeTypeDm.trim();
    }
}