package com.iflytek.dep.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iflytek.dep.admin.dao.DEPServerMapper;
import com.iflytek.dep.admin.model.DEPServer;
import com.iflytek.dep.admin.model.dto.DEPServerDto;
import com.iflytek.dep.admin.model.vo.DEPServerVo;
import com.iflytek.dep.admin.model.vo.PageVo;
import com.iflytek.dep.admin.service.DEPServerService;
import com.iflytek.dep.admin.utils.CommonConstants;
import com.iflytek.dep.common.utils.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service.impl
 * @Description: DEPServer 管理模块服务实现
 * @date 2019/2/27--19:20
 */
@Service("depServerService")
public class DEPServerServiceImpl implements DEPServerService {

    @Autowired
    private DEPServerMapper depServerMapper;

    @Override
    public PageVo<DEPServerVo> listDEPServer(DEPServerDto depServerDto) {
        PageHelper.startPage(depServerDto.getCurrentPageNo(), depServerDto.getPageSize());
        List<DEPServerVo> depServerVos = depServerMapper.selectAll(depServerDto);
        PageInfo<DEPServerVo> pageInfo = new PageInfo<>(depServerVos);
        return new PageVo(depServerDto.getCurrentPageNo(), depServerDto.getPageSize(), (int) pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public int addDEPServer(DEPServer depServer) {
        depServer.setDepServerId(UUIDGenerator.createUUID());
        depServer.setFlagDelete(CommonConstants.FLAG_DELETE.FALSE);
        return depServerMapper.insert(depServer);
    }

    @Override
    public int updateDEPServer(DEPServer depServer) {
        return depServerMapper.updateByPrimaryKey(depServer);
    }

    @Override
    public int deleteDEPServer(String depServerId) {
        return depServerMapper.deleteByPrimaryKey(depServerId);
    }

    @Override
    public List<DEPServer> listDEPServer(String serverNodeId) {
        List<DEPServer> list = depServerMapper.selectByServerNodeId(serverNodeId);
        return list;
    }
}