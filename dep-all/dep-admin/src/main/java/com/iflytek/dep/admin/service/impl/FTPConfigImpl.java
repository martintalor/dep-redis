package com.iflytek.dep.admin.service.impl;



import com.iflytek.dep.admin.dao.FTPConfigMapper;
import com.iflytek.dep.admin.dao.MachineNodeMapper;
import com.iflytek.dep.admin.model.FTPConfig;
import com.iflytek.dep.admin.model.MachineNode;
import com.iflytek.dep.admin.service.FTPConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.service.impl
 * @Description: FTPConfigImpl
 * @date 2019/2/27--19:20
 */
@Service("ftpConfigService")
public class FTPConfigImpl implements FTPConfigService {

    @Autowired
    private FTPConfigMapper ftpConfigMapper;

    @Autowired
    private MachineNodeMapper machineNodeMapper;

    @Override
    public List<FTPConfig> listFTPConfig(String serverNodeId) {
        List<FTPConfig> list = ftpConfigMapper.selectAllByServerNodeId(serverNodeId);
        return list;
    }

    @Override
    public List<MachineNode> listDocServer(String serverNodeId) {
        return machineNodeMapper.listDocServer(serverNodeId);
    }

}