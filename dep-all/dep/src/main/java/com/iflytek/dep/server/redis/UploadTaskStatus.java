package com.iflytek.dep.server.redis;

public enum UploadTaskStatus {

    UP_ETL_PENGING("00", "ETL处理未开始-出库"),
    UP_ETL_DOING("01", " ETL处理开始-出库"),

    UP_DEP_PENGING("02", "DEP处理未开始-发送"),
    UP_DEP_DOING("03", "DEP处理开始-发送");

    private final String code;
    private final String name;

    UploadTaskStatus(String code, String name) {
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
