package com.iflytek.dep.admin.service;

import com.iflytek.dep.admin.model.DEPServer;
import com.iflytek.dep.admin.model.dto.DEPServerDto;
import com.iflytek.dep.admin.model.vo.DEPServerVo;
import com.iflytek.dep.admin.model.vo.PageVo;

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service
 * @Description: DEPServer 管理模块服务
 * @date 2019/2/27--19:20
 */
public interface DEPServerService {

    public PageVo<DEPServerVo> listDEPServer(DEPServerDto depServerDto);

    public int addDEPServer(DEPServer depServer);

    public int updateDEPServer(DEPServer depServer);

    public int deleteDEPServer(String depServerId);

    public List<DEPServer> listDEPServer(String serverNodeId);

}
