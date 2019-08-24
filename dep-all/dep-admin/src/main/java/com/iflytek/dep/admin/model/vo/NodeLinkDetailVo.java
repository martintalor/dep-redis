package com.iflytek.dep.admin.model.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description: NodeLinkDetailVo 链路详情信息
 * @date 2019/3/12--14:37
 */
public class NodeLinkDetailVo<T> {

    private String nodeId;

    private String nodeRemark;

    private String nodeType;

    /**
     * 累计耗时 单位s
     */
    private long totalSpendTime;

    /**
     * 最大上传速率
     */
    private String maxTransmissionRate;

    private String minTransmissionRate;

    private String averageTransmissionRate;

    private List<T> list= new ArrayList<>();

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

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public long getTotalSpendTime() {
        return totalSpendTime;
    }

    public void setTotalSpendTime(long totalSpendTime) {
        this.totalSpendTime = totalSpendTime;
    }

    public String getMaxTransmissionRate() {
        return maxTransmissionRate;
    }

    public void setMaxTransmissionRate(String maxTransmissionRate) {
        this.maxTransmissionRate = maxTransmissionRate;
    }

    public String getMinTransmissionRate() {
        return minTransmissionRate;
    }

    public void setMinTransmissionRate(String minTransmissionRate) {
        this.minTransmissionRate = minTransmissionRate;
    }

    public String getAverageTransmissionRate() {
        return averageTransmissionRate;
    }

    public void setAverageTransmissionRate(String averageTransmissionRate) {
        this.averageTransmissionRate = averageTransmissionRate;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}