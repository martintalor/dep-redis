package com.iflytek.dep.admin.model.dto.monitor;

/**
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.dto
 * @Description:
 * @date 2019/3/1
 */
public class TrendStatDataDto {

    private String serverNodeId;

    /**
     * 查询发送量(send)还是接收量(recv)
     */
    private String sendOrRecv;

    /**
     * 查询成功或失败的
     */
    private String succOrFail;

    /**
     * 日期类型 今天 today ; 本周-week ; 本月 month ; 全年 year
     */
    private String trendDateType;

    /**
     * 开始日期 格式 yyyy-MM-dd
     */
    private String startTime;

    /**
     * 结束日期 格式 yyyy-MM-dd
     */
    private String endTime;

    private long days;

    public String getTrendDateType() {
        return trendDateType;
    }

    public void setTrendDateType(String trendDateType) {
        this.trendDateType = trendDateType;
    }

    public String getSendOrRecv() {
        return sendOrRecv;
    }

    public void setSendOrRecv(String sendOrRecv) {
        this.sendOrRecv = sendOrRecv;
    }

    public String getSuccOrFail() {
        return succOrFail;
    }

    public void setSuccOrFail(String succOrFail) {
        this.succOrFail = succOrFail;
    }

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }
}