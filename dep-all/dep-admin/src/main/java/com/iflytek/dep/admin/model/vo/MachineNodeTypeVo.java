package com.iflytek.dep.admin.model.vo;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description:
 * @date 2019/2/26--21:45
 */
public class MachineNodeTypeVo {

    /**
     * 物理节点id
     */
    private String nodeId;

    /**
     * 物理节点名称
     */
    private String nodeRemark;

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
}