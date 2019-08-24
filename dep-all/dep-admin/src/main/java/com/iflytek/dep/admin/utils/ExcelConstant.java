package com.iflytek.dep.admin.utils;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.utils
 * @Description:
 * @date 2019/7/23--10:56
 */
public class ExcelConstant {

    /**
     * 每个sheet存储的记录数 100W
     */
    public static final Integer PER_SHEET_ROW_COUNT = 1000000;

    /**
     * 每次向EXCEL写入的记录数(查询每页数据大小) 20W
     */
    public static final Integer PER_WRITE_ROW_COUNT = 200000;

}