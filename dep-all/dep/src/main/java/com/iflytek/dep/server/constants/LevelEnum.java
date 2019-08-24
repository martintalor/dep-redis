package com.iflytek.dep.server.constants;

/** 
* @Author zhuyifan
* @Time 2019年5月24日 上午10:19:30 
* @Version 1.0
*/
public enum LevelEnum {

    LEVEL0(0),// 最高级别
    LEVEL1(1),
    LEVEL2(2),
    LEVEL3(3),// 默认级别
    LEVEL4(4),
    LEVEL5(5),
    LEVEL6(6),
    LEVEL7(7),
    LEVEL8(8),
    LEVEL9(9);// 最低级别

    private int value;

    LevelEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LevelEnum getType(int value) {
        for (LevelEnum item : LevelEnum.values()) {
            if (value == item.getValue()) {
                return item;
            }
        } return null;
    }
}
