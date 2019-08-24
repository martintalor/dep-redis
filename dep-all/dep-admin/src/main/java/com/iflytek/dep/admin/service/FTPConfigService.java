package com.iflytek.dep.admin.service;


import com.iflytek.dep.admin.model.FTPConfig;
import com.iflytek.dep.admin.model.MachineNode;

import java.util.List;

/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.service.scheduled
 * @Description: FTPConfigService
 * @date 2019/2/27--19:20
 */
public interface FTPConfigService {
     List<FTPConfig> listFTPConfig(String serverNodeId);

     List<MachineNode> listDocServer(String serverNodeId);
}
