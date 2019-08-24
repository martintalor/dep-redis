package com.iflytek.dep.server.constants;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.constants
 * @Description:
 * @date 2019/6/21--18:42
 */
public enum ReceiverType {

    POLICE("1","APP-G1"),
    PROCURATORATE("2","APP-J1"),
    COURT("3","APP-F1"),
    JUDICIAL("4","APP-S1"),
    COMMISSION("5","APP-Z1");

    private final String code;
    private final String name;

    ReceiverType(String code, String name) {
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
