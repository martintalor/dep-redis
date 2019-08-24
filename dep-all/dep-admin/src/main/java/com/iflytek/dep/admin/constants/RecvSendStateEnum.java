package com.iflytek.dep.admin.constants;

/**
 * 节点流转状态,10已生成； 20已接收；21接收中；30发送成功；31正在发送；00异常
 * Created by xiliu5 on 2019/3/2.
 */
public enum RecvSendStateEnum {
    GENERATED("10", "已生成"),
    RECVED("20", "已接收"),
    RECVING("21", "接收中"),
    SENDSUCC("30", "发送成功"),
    SENDING("31", "正在发送"),
    FAIL("00", "异常");

    RecvSendStateEnum(String stateCode, String stateName) {
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

    public static RecvSendStateEnum getByStateCode(String stateCode) {
        for (RecvSendStateEnum stateEnum : RecvSendStateEnum.values()) {
            if (stateEnum.getStateCode().equals(stateCode)) {
                return stateEnum;
            }
        }
        return null;
    }
}
