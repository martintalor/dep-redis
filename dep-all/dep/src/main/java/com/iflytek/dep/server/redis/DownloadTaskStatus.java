package com.iflytek.dep.server.redis;

public enum DownloadTaskStatus {

    DOWN_DEP_PENGING("00", "DEP处理未开始-接收"),
    DOWN_DEP_DOING("01", "DEP处理开始-接收"),
    DOWN_ETL_PENGING("02", "ETL处理未开始-入库"),
    DOWN_ETL_DOING("03", " ETL处理开始-入库"),
    DOWN_ACK_PENGING("10", "ACK处理未开始-接收"),
    DOWN_ACK_DOING("11", "ACK处理开始-接收");




    private final String code;
    private final String name;

    DownloadTaskStatus(String code, String name) {
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
