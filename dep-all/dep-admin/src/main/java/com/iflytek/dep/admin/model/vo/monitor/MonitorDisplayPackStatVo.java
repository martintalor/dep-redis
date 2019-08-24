package com.iflytek.dep.admin.model.vo.monitor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 数据包传输统计
 * Created by xiliu5 on 2019/2/28.
 */
public class MonitorDisplayPackStatVo {

    private Integer sendNumToday;
    private BigDecimal sendSizeToday;
    private Double sendNumCompareWeekPercent;
    private Double sendNumCompareYesterdayPrecent;
    private Integer recvNumToday;
    private BigDecimal recvSizeToday;
    private Double sendSuccPercent;
    private Double sendSuccCompareWeekPercent;
    private Double sendSuccCompareYesterdayPercent;
    private List<Map<String, Object>> transTimePercent;

    public Integer getSendNumToday() {
        return sendNumToday;
    }

    public void setSendNumToday(Integer sendNumToday) {
        this.sendNumToday = sendNumToday;
    }

    public BigDecimal getSendSizeToday() {
        return sendSizeToday;
    }

    public void setSendSizeToday(BigDecimal sendSizeToday) {
        this.sendSizeToday = sendSizeToday;
    }

    public Double getSendNumCompareWeekPercent() {
        return sendNumCompareWeekPercent;
    }

    public void setSendNumCompareWeekPercent(Double sendNumCompareWeekPercent) {
        this.sendNumCompareWeekPercent = sendNumCompareWeekPercent;
    }

    public Double getSendNumCompareYesterdayPrecent() {
        return sendNumCompareYesterdayPrecent;
    }

    public void setSendNumCompareYesterdayPrecent(Double sendNumCompareYesterdayPrecent) {
        this.sendNumCompareYesterdayPrecent = sendNumCompareYesterdayPrecent;
    }

    public Integer getRecvNumToday() {
        return recvNumToday;
    }

    public void setRecvNumToday(Integer recvNumToday) {
        this.recvNumToday = recvNumToday;
    }

    public BigDecimal getRecvSizeToday() {
        return recvSizeToday;
    }

    public void setRecvSizeToday(BigDecimal recvSizeToday) {
        this.recvSizeToday = recvSizeToday;
    }

    public Double getSendSuccPercent() {
        return sendSuccPercent;
    }

    public void setSendSuccPercent(Double sendSuccPercent) {
        this.sendSuccPercent = sendSuccPercent;
    }

    public Double getSendSuccCompareWeekPercent() {
        return sendSuccCompareWeekPercent;
    }

    public void setSendSuccCompareWeekPercent(Double sendSuccCompareWeekPercent) {
        this.sendSuccCompareWeekPercent = sendSuccCompareWeekPercent;
    }

    public Double getSendSuccCompareYesterdayPercent() {
        return sendSuccCompareYesterdayPercent;
    }

    public void setSendSuccCompareYesterdayPercent(Double sendSuccCompareYesterdayPercent) {
        this.sendSuccCompareYesterdayPercent = sendSuccCompareYesterdayPercent;
    }

    public List<Map<String, Object>> getTransTimePercent() {
        return transTimePercent;
    }

    public void setTransTimePercent(List<Map<String, Object>> transTimePercent) {
        this.transTimePercent = transTimePercent;
    }
}
