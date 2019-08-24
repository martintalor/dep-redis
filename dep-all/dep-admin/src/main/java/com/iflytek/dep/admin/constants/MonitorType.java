package com.iflytek.dep.admin.constants;

public enum MonitorType {

    DEPSERVER_BEAT(1, "DEPSERVER心跳"),
    FTP_BEAT(2, "FTP心跳"),
    FTP_FILE_DEL(3, "FTP文件删除"),
    DEP_FILE_DEL(4, "DEPSERVER本地文件删除"),
    FTP_FILE_MOV(5, "FTP文件移动");

    private final int value;
    private final String desc;

    MonitorType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public static MonitorType getType(int value) {
        for (MonitorType item : MonitorType.values()) {
            if (value == item.getValue()) {
                return item;
            }
        } return null;
    }

}
