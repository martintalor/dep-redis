package com.iflytek.dep.server.constants;

public enum ExceptionState {

    UP("001", "上传异常"),
    DOWN("002", "下载异常"),
    UNPACK("003", "解压异常"),
    PACK("004", "压缩异常"),
    DECRYPT("005", "解密异常"),
    ENCRYPT("006", "加密异常");


    private final String code;
    private final String name;

    ExceptionState(String code, String name) {
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
