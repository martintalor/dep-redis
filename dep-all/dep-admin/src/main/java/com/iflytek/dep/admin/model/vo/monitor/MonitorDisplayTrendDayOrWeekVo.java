package com.iflytek.dep.admin.model.vo.monitor;

import java.math.BigDecimal;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo.monitor
 * @Description:
 * @date 2019/6/10--22:08
 */
public class MonitorDisplayTrendDayOrWeekVo {
    private String serverNodeId;
    private BigDecimal trendSize;

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId;
    }

    public BigDecimal getTrendSize() {
        return trendSize;
    }

    public void setTrendSize(BigDecimal trendSize) {
        this.trendSize = trendSize;
    }
}
