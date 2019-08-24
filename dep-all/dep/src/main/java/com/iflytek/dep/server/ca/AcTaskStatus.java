package com.iflytek.dep.server.ca;

public enum AcTaskStatus {

    CA_PENGING("00", "CA处理未开始-下载"),
    CA_DOING("01", "CA处理开始-下载");


    private final String code;
    private final String name;

    AcTaskStatus(String code, String name) {
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
