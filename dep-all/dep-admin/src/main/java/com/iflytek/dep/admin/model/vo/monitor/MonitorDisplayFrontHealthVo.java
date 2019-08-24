package com.iflytek.dep.admin.model.vo.monitor;

import java.math.BigDecimal;

/**
 *
 * 前置机健康状态
 * Created by xiliu5 on 2019/3/1.
 */
public class MonitorDisplayFrontHealthVo {
    private String nodeId;

    private String machineIp;

    private String serverNodeId;

    private String nodeRemark;

    private String nodeTypeDm;

    private String probeResult;

    private Integer sendNumSucc;

    private Integer sendNumFail;

    private Integer sendNumTotal;

    private BigDecimal sendSize;

    private BigDecimal recvSize;

    private BigDecimal sendSizeSending;
    private BigDecimal sendSizeSendSucc;
    private BigDecimal sendSizeSendFail;
    private BigDecimal recvSizeRecving;
    private BigDecimal recvSizeRecvSucc;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId;
    }

    public String getNodeRemark() {
        return nodeRemark;
    }

    public void setNodeRemark(String nodeRemark) {
        this.nodeRemark = nodeRemark;
    }

    public String getNodeTypeDm() {
        return nodeTypeDm;
    }

    public void setNodeTypeDm(String nodeTypeDm) {
        this.nodeTypeDm = nodeTypeDm;
    }

    public String getProbeResult() {
        return probeResult;
    }

    public void setProbeResult(String probeResult) {
        this.probeResult = probeResult;
    }

    public Integer getSendNumSucc() {
        return sendNumSucc;
    }

    public void setSendNumSucc(Integer sendNumSucc) {
        this.sendNumSucc = sendNumSucc;
    }

    public Integer getSendNumTotal() {
        this.sendNumTotal = this.sendNumFail + this.sendNumSucc;
        return sendNumTotal;
    }

    public void setSendNumTotal(Integer sendNumTotal) {
        this.sendNumTotal = sendNumTotal;
    }

    public BigDecimal getSendSize() {
        this.sendSize = this.sendSizeSending.add(this.sendSizeSendSucc).add(this.sendSizeSendFail);
        return sendSize;
    }

    public void setSendSize(BigDecimal sendSize) {
        this.sendSize = sendSize;
    }

    public BigDecimal getRecvSize() {
        this.recvSize = this.recvSizeRecving.add(this.recvSizeRecvSucc);
        return recvSize;
    }

    public void setRecvSize(BigDecimal recvSize) {
        this.recvSize = recvSize;
    }

    public BigDecimal getSendSizeSending() {
        return sendSizeSending;
    }

    public void setSendSizeSending(BigDecimal sendSizeSending) {
        this.sendSizeSending = sendSizeSending;
    }

    public BigDecimal getSendSizeSendSucc() {
        return sendSizeSendSucc;
    }

    public void setSendSizeSendSucc(BigDecimal sendSizeSendSucc) {
        this.sendSizeSendSucc = sendSizeSendSucc;
    }

    public BigDecimal getSendSizeSendFail() {
        return sendSizeSendFail;
    }

    public void setSendSizeSendFail(BigDecimal sendSizeSendFail) {
        this.sendSizeSendFail = sendSizeSendFail;
    }

    public BigDecimal getRecvSizeRecving() {
        return recvSizeRecving;
    }

    public void setRecvSizeRecving(BigDecimal recvSizeRecving) {
        this.recvSizeRecving = recvSizeRecving;
    }

    public BigDecimal getRecvSizeRecvSucc() {
        return recvSizeRecvSucc;
    }

    public void setRecvSizeRecvSucc(BigDecimal recvSizeRecvSucc) {
        this.recvSizeRecvSucc = recvSizeRecvSucc;
    }

    public Integer getSendNumFail() {
        return sendNumFail;
    }

    public void setSendNumFail(Integer sendNumFail) {
        this.sendNumFail = sendNumFail;
    }
}
