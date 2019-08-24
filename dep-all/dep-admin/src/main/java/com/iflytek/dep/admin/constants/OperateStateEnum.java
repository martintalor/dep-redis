package com.iflytek.dep.admin.constants;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.constants
 * @Description: 操作状态
 * A 状态起始节点独有
 * B 状态中心节点独有
 * C 节点目标节点独有
 * 节点流转状态,10已生成； 20已接收；21接收中；30发送成功；31正在发送；00异常
 * @date 2019/3/12--19:48
 */
public enum OperateStateEnum {
    START_COMPRESSING("A01", "起始节点压缩中"),
    START_COMPRESS("A02", "起始节点压缩完成"),
    START_ENCRYPTING("A11", "起始节点加密中"),
    START_ENCRYPT("A12", "起始节点加密完成"),
    CENTER_DECRYPTING("B01", "中心节点解密中"),
    CENTER_DECRYPT("B02", "中心节点解密完成"),
    CENTER_ENCRYPTING("B11", "中心节点加密中"),
    CENTER_ENCRYPT("B12", "中心节点加密完成"),
    END_DECRYPTING("C01", "目标节点解密中"),
    END_DECRYPT("C02", "目标节点解密完成"),
    END_DECOMPRESSING("C11", "目标节点解压中"),
    END_DECOMPRESS("C12", "目标节点解压完成"),

    GENERATED("10", "已生成"),
    RECVED("20", "已接收"),
    RECVING("21", "接收中"),
    SENDSUCC("30", "发送成功"),
    SENDING("31", "正在发送"),
    FAIL("00", "异常");


    OperateStateEnum(String stateCode, String stateName) {
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

    public static OperateStateEnum getByStateCode(String stateCode) {
        for (OperateStateEnum stateEnum : OperateStateEnum.values()) {
            if (stateEnum.getStateCode().equals(stateCode)) {
                return stateEnum;
            }
        }
        return null;
    }

}