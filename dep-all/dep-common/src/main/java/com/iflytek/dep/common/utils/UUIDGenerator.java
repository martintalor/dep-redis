package com.iflytek.dep.common.utils;

import java.util.UUID;

public class UUIDGenerator {

    public static String createUUID() {
        String code = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
        return code;
    }
}
