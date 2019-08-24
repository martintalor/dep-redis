package com.iflytek.dep.admin.service;


/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.service.scheduled
 * @Description: DEPServerScheduledService
 * @date 2019/2/27--19:20
 */
public interface DEPServerScheduledService {
    boolean start(String depServerId);
    boolean close(String depServerId);

    boolean startDelLocalFile();

    boolean closeDelLocalFile();
}
