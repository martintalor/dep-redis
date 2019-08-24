package com.iflytek.dep.admin.model.vo;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description:
 * @date 2019/2/25--15:48
 */
public class LocalServerNodeVo {

    private String serverNodeId;

    private String serverNodeName;

    private String orgTypeDm;

    private String orgTypeMc;

    private String serverNodeTypeDm;

    private String serverNodeTypeMc;

    private int totalMachine;

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId;
    }

    public String getServerNodeName() {
        return serverNodeName;
    }

    public void setServerNodeName(String serverNodeName) {
        this.serverNodeName = serverNodeName;
    }

    public String getOrgTypeDm() {
        return orgTypeDm;
    }

    public void setOrgTypeDm(String orgTypeDm) {
        this.orgTypeDm = orgTypeDm;
    }

    public String getOrgTypeMc() {
        return orgTypeMc;
    }

    public void setOrgTypeMc(String orgTypeMc) {
        this.orgTypeMc = orgTypeMc;
    }

    public String getServerNodeTypeDm() {
        return serverNodeTypeDm;
    }

    public void setServerNodeTypeDm(String serverNodeTypeDm) {
        this.serverNodeTypeDm = serverNodeTypeDm;
    }

    public String getServerNodeTypeMc() {
        return serverNodeTypeMc;
    }

    public void setServerNodeTypeMc(String serverNodeTypeMc) {
        this.serverNodeTypeMc = serverNodeTypeMc;
    }

    public int getTotalMachine() {
        return totalMachine;
    }

    public void setTotalMachine(int totalMachine) {
        this.totalMachine = totalMachine;
    }
}