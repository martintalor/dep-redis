package com.iflytek.dep.admin.model.vo.monitor;

import java.math.BigDecimal;

/**
 * 发送量趋势VO
 * Created by xiliu5 on 2019/3/1.
 */
public class MonitorDisplayTrendVo {
    private String trendDate;
    private BigDecimal trendCounter;
    private BigDecimal trendSize;

    public BigDecimal getTrendSize() {
        return trendSize;
    }

    public void setTrendSize(BigDecimal trendSize) {
        this.trendSize = trendSize;
    }

    public String getTrendDate() {
        return trendDate;
    }

    public void setTrendDate(String trendDate) {
        this.trendDate = trendDate;
    }

    public BigDecimal getTrendCounter() {
        return trendCounter;
    }

    public void setTrendCounter(BigDecimal trendCounter) {
        this.trendCounter = trendCounter;
    }
}
