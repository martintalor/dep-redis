package com.iflytek.dep.server.constants;

public enum GlobalState {

    FAIL("00", "异常"),
    EXCHANGEING("01", "交换中"),
    FINISHED("02", "已完成");

    private final String code;
    private final String name;

    GlobalState(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
