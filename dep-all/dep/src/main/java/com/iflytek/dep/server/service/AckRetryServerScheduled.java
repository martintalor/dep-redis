package com.iflytek.dep.server.service;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.service
 * @Description:
 * @date 2019/6/4--22:23
 */
public interface AckRetryServerScheduled {

    boolean start();
    boolean close();
}
