package com.iflytek.dep.admin.model.dto;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.dto
 * @Description:
 * @date 2019/2/26--10:20
 */
public class MachineNodeDto {

    /**
     * 物理节点名称
     */
    private String nodeRemark;

    /**
     * 是否启用（Y/N）
     */
    private String flagEnable;


    /**
     * 是否运行正常（Y/N）
     */
    private String probeResult;

    private String serverNodeId;

    /**
     * 逻辑节点类型  01表示中心节点
     */
    private String serverNodeTypeDm;

    private int currentPageNo;

    private int pageSize;

    public String getNodeRemark() {
        return nodeRemark;
    }

    public void setNodeRemark(String nodeRemark) {
        this.nodeRemark = nodeRemark;
    }

    public String getFlagEnable() {
        return flagEnable;
    }

    public void setFlagEnable(String flagEnable) {
        this.flagEnable = flagEnable;
    }

    public String getProbeResult() {
        return probeResult;
    }

    public void setProbeResult(String probeResult) {
        this.probeResult = probeResult;
    }

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId;
    }

    public int getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(int currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getServerNodeTypeDm() {
        return serverNodeTypeDm;
    }

    public void setServerNodeTypeDm(String serverNodeTypeDm) {
        this.serverNodeTypeDm = serverNodeTypeDm;
    }
}