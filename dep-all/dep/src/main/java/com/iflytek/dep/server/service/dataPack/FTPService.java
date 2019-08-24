package com.iflytek.dep.server.service.dataPack;

import com.iflytek.dep.server.mapper.FTPConfigMapper;
import com.iflytek.dep.server.model.FTPConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.server.ftp
 * @Description: FTPService
 * @date 2019/3/4--18:44
 */
@Service
public class FTPService {

    @Autowired
    private FTPConfigMapper ftpConfigMapper;

    public List<FTPConfig> selectByServerNodeId(String serverNodeId) {
        return ftpConfigMapper.selectByServerNodeId(serverNodeId);
    }

    public FTPConfig selectByNodeId(String nodeId) {
        return ftpConfigMapper.selectByNodeId(nodeId);
    }
}