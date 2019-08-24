package com.iflytek.dep.admin.model.dto;

/**
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.dto
 * @Description:
 * @date 2019/2/27
 */
public class NodeRouteDto extends BaseDto {

    private String leftNodeId;
    private String rightNodeId;
    private String routeName;
    private String createTimeStart;
    private String createTimeEnd;

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getLeftNodeId() {
        return leftNodeId;
    }

    public void setLeftNodeId(String leftNodeId) {
        this.leftNodeId = leftNodeId;
    }

    public String getRightNodeId() {
        return rightNodeId;
    }

    public void setRightNodeId(String rightNodeId) {
        this.rightNodeId = rightNodeId;
    }

    public String getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }
}