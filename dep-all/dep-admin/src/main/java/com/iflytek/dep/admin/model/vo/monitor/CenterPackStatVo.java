package com.iflytek.dep.admin.model.vo.monitor;

/**
 * 中心节点-数据包传输统计VO
 * Created by xiliu5 on 2019/3/6.
 */
public class CenterPackStatVo {
    private long recvSuccTotal;
    private double recvSuccSize;
    private long sendTotal;
    private long sendFailTotal;
    private long sendSuccTotal;
    private double sendSuccSize;

    public long getRecvSuccTotal() {
        return recvSuccTotal;
    }

    public void setRecvSuccTotal(long recvSuccTotal) {
        this.recvSuccTotal = recvSuccTotal;
    }

    public double getRecvSuccSize() {
        return recvSuccSize;
    }

    public void setRecvSuccSize(double recvSuccSize) {
        this.recvSuccSize = recvSuccSize;
    }

    public long getSendTotal() {
        this.sendTotal = this.sendFailTotal + this.sendSuccTotal;
        return sendTotal;
    }

    public void setSendTotal(long sendTotal) {
        this.sendTotal = sendTotal;
    }

    public long getSendFailTotal() {
        return sendFailTotal;
    }

    public void setSendFailTotal(long sendFailTotal) {
        this.sendFailTotal = sendFailTotal;
    }

    public long getSendSuccTotal() {
        return sendSuccTotal;
    }

    public void setSendSuccTotal(long sendSuccTotal) {
        this.sendSuccTotal = sendSuccTotal;
    }

    public double getSendSuccSize() {
        return sendSuccSize;
    }

    public void setSendSuccSize(double sendSuccSize) {
        this.sendSuccSize = sendSuccSize;
    }
}
