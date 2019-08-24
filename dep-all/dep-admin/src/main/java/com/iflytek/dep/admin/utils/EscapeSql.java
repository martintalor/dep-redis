package com.iflytek.dep.admin.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by xiliu5 on 2019/3/7.
 */
public class EscapeSql {

    /**
     * sql 符号转义
     *
     * @param key 搜索关键字
     * @return 转义后的 key
     * @author zyzhang15
     * @create 2018年5月28日下午2:13:34
     * @version 1.0
     */
    public static String escapeSql(String key) {
        if (StringUtils.isNotEmpty(key)) {
            // 排除模糊搜索中的通配符: _, %, /
            // replace("/", "//")必须是第一个,因为 '/' 是转义符本身
            key = key.replace("/", "//").replace("_", "/_").replace("%", "/%");
        }
        return key;
    }
}
