package com.iflytek.dep.admin.model;

public class ServerNodeType {
    private String serverNodeTypeDm;

    private String serverNodeTypeMc;

    public String getServerNodeTypeDm() {
        return serverNodeTypeDm;
    }

    public void setServerNodeTypeDm(String serverNodeTypeDm) {
        this.serverNodeTypeDm = serverNodeTypeDm == null ? null : serverNodeTypeDm.trim();
    }

    public String getServerNodeTypeMc() {
        return serverNodeTypeMc;
    }

    public void setServerNodeTypeMc(String serverNodeTypeMc) {
        this.serverNodeTypeMc = serverNodeTypeMc == null ? null : serverNodeTypeMc.trim();
    }
}