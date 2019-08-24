package com.iflytek.dep.admin.service;


/**
 * 文档服务器定时任务服务
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.server.service.scheduled
 * @Description: DocServerScheduledService
 * @date 2019/3/13
 */
public interface DocServerScheduledService {
    boolean start(String nodeId);
    boolean close(String nodeId);
}
