package com.iflytek.dep.admin.model.vo;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description:
 * @date 2019/2/26--9:59
 */
public class MachineNodeVo {
    /**
     * 物理节点id
     */
    private String nodeId;

    /**
     * 物理节点名称
     */
    private String nodeRemark;

    /**
     * 所属逻辑节点
     */
    private String serverNodeId;

    /**
     * 所属逻辑节点名称
     */
    private String serverNodeName;

    /**
     * 逻辑节点类型  01表示中心节点
     */
    private String serverNodeTypeDm;

    /**
     * 是否启用（Y/N）
     */
    private String flagEnable;


    /**
     * 是否运行正常（Y/N）
     */
    private String probeResult;

    private int sendNumSucc;

    private int sendNumFail;

    /**
     * 发送数量
     */
    private int totalSend;


    /**
     * 接收数量
     */
    private int totalReceive;

    /**
     * 是否运行正常（Y/N）
     */
    private String nodeTypeDm;

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

    public int getTotalSend() {
        this.totalSend = this.sendNumFail + this.sendNumSucc;
        return totalSend;
    }

    public void setTotalSend(int totalSend) {
        this.totalSend = totalSend;
    }

    public int getTotalReceive() {
        return totalReceive;
    }

    public void setTotalReceive(int totalReceive) {
        this.totalReceive = totalReceive;
    }

    public String getNodeTypeDm() {
        return nodeTypeDm;
    }

    public void setNodeTypeDm(String nodeTypeDm) {
        this.nodeTypeDm = nodeTypeDm;
    }

    public int getSendNumSucc() {
        return sendNumSucc;
    }

    public void setSendNumSucc(int sendNumSucc) {
        this.sendNumSucc = sendNumSucc;
    }

    public int getSendNumFail() {
        return sendNumFail;
    }

    public void setSendNumFail(int sendNumFail) {
        this.sendNumFail = sendNumFail;
    }

    public String getServerNodeTypeDm() {
        return serverNodeTypeDm;
    }

    public void setServerNodeTypeDm(String serverNodeTypeDm) {
        this.serverNodeTypeDm = serverNodeTypeDm;
    }
}