package com.iflytek.dep.server.constants;

/**
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Package com.iflytek.dep.server.redis
 * @Description:
 * @date 2019/6/23--11:15
 */
public enum RedisQueueType {

    // 数据包下载处理
    PENG_DOWN_PKG("downloadTaskList-pkg"),
    DOING_DOWN_PKG("downloadTaskList-pkg-map"),

    // 数据包打包上传处理
    PENG_UP_PKG("uploadTaskList-pkg"),
    DOING_UP_PKG("uploadTaskList-pkg-map"),

    PENG_DOWN_ACK("downloadTaskList-ack"),
    DOING_DOWN_ACK("downloadTaskList-ack-map"),

    // 数据包下载入库处理
    PENG_DOWN_ETL("downloadTaskList-etl"),
    DOING_DOWN_ETL("downloadTaskList-etl-map"),


    // 数据包下载入库处理
    PENG_CA("caTaskList"),
    DOING_CA("caTaskList-map");

    private final String code;

    RedisQueueType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
