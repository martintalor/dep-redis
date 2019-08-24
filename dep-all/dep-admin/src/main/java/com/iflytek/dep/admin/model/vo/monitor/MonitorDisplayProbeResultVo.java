package com.iflytek.dep.admin.model.vo.monitor;

/**
 * 物理节点状态统计VO
 * Created by xiliu5 on 2019/3/2.
 */
public class MonitorDisplayProbeResultVo {
    private String probeResult;//探测结果，Y为ok，N为error
    private Integer resultCnt;//节点数量

    public String getProbeResult() {
        return probeResult;
    }

    public void setProbeResult(String probeResult) {
        this.probeResult = probeResult;
    }

    public Integer getResultCnt() {
        return resultCnt;
    }

    public void setResultCnt(Integer resultCnt) {
        this.resultCnt = resultCnt;
    }
}
