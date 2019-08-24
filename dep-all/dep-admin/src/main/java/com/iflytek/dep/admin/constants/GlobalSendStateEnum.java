package com.iflytek.dep.admin.constants;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.constants
 * @Description: 当前数据包全局状态 00异常 01交换中 02已完成
 * @date 2019/3/13--14:37
 */
public enum GlobalSendStateEnum {

    FAIL("00", "异常"),
    SENDING("01", "处理中"),
    END("02", "已完成");

    GlobalSendStateEnum(String stateCode, String stateName) {
        this.stateCode = stateCode;
        this.stateName = stateName;
    }

    private String stateCode;
    private String stateName;

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public static GlobalSendStateEnum getByStateCode(String stateCode) {
        for (GlobalSendStateEnum stateEnum : GlobalSendStateEnum.values()) {
            if (stateEnum.getStateCode().equals(stateCode)) {
                return stateEnum;
            }
        }
        return null;
    }
}