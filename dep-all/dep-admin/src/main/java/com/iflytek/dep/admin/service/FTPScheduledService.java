package com.iflytek.dep.admin.service;


import com.iflytek.dep.admin.constants.MonitorType;

/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.service.scheduled
 * @Description: FTPScheduledService
 * @date 2019/2/27--19:20
 */
public interface FTPScheduledService {
    boolean start(String ftpId, MonitorType type);
    boolean close(String ftpId, MonitorType type);
}
