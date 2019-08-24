package com.iflytek.dep.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iflytek.dep.admin.dao.*;
import com.iflytek.dep.admin.model.LocalServerNode;
import com.iflytek.dep.admin.model.OrgType;
import com.iflytek.dep.admin.model.ServerNodeType;
import com.iflytek.dep.admin.model.dto.LocalServerNodeDto;
import com.iflytek.dep.admin.model.dto.MachineNodeDto;
import com.iflytek.dep.admin.model.vo.*;
import com.iflytek.dep.admin.service.LocalServerNodeService;
import com.iflytek.dep.common.utils.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service.impl
 * @Description: 逻辑节点服务实现
 * @date 2019/2/25--15:25
 */
@Service
public class LocalServerNodeServiceImpl implements LocalServerNodeService {

    @Autowired
    private LocalServerNodeMapper localServerNodeMapper;

    @Autowired
    private OrgTypeMapper orgTypeMapper;

    @Autowired
    private ServerNodeTypeMapper serverNodeTypeMapper;

    @Autowired
    private MachineNodeMapper machineNodeMapper;

    @Autowired
    private DEPServerMapper depServerMapper;

    @Override
    public PageVo<LocalServerNodeVo> listLocalServerNode(LocalServerNodeDto localServerNodeDto) {
        PageHelper.startPage(localServerNodeDto.getCurrentPageNo(), localServerNodeDto.getPageSize());
        List<LocalServerNodeVo> localServerNodeVos = localServerNodeMapper.selectAll(localServerNodeDto);
        if (!CollectionUtils.isEmpty(localServerNodeVos)) {
            for (LocalServerNodeVo dto : localServerNodeVos) {
                dto.setTotalMachine(machineNodeMapper.countNode(dto.getServerNodeId()));
            }
        }
        PageInfo<LocalServerNodeVo> pageInfo = new PageInfo<>(localServerNodeVos);
        return new PageVo(localServerNodeDto.getCurrentPageNo(), localServerNodeDto.getPageSize(), (int) pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public int addLocalServerNode(LocalServerNode localServerNode) {
        return localServerNodeMapper.insert(localServerNode);
    }

    @Override
    public int updateLocalServerNode(LocalServerNode localServerNode) {
        return localServerNodeMapper.updateByPrimaryKey(localServerNode);
    }

    @Override
    public List<OrgType> listOrgType() {
        return orgTypeMapper.selectAll();
    }

    @Override
    public List<ServerNodeType> listServerNodeType() {
        return serverNodeTypeMapper.selectAll();
    }

    @Override
    public List<LocalServerNodeTypeVo> listNode() {
        return localServerNodeMapper.listNode();
    }

    @Override
    public LocalServerNode selectByServerNodeId(String serverNodeId) {
        return localServerNodeMapper.selectByPrimaryKey(serverNodeId);
    }

    @Override
    public ResponseBean deleteLocalServerNode(String serverNodeId) {
        // 查看逻辑节点是否被物理节点绑定
        List<String> serverNodeNames = machineNodeMapper.getServerNodeNameById(serverNodeId);
        if (!CollectionUtils.isEmpty(serverNodeNames)) {
            return new ResponseBean("物理节点【" + serverNodeNames.get(0) + "】绑定当前节点，不可删除");
        }
        // 查看逻辑节点是否被depServer绑定
        List<String> depServerNames = depServerMapper.getDEPServerNameById(serverNodeId);
        if (!CollectionUtils.isEmpty(depServerNames)) {
            return new ResponseBean("DEP server【" + depServerNames.get(0) + "】绑定当前节点，不可删除");
        }
        return new ResponseBean(localServerNodeMapper.deleteByPrimaryKey(serverNodeId));
    }

    @Override
    public PageVo<MachineNodeVo> getLocalServerNode(MachineNodeDto machineNodeDto) {
        PageHelper.startPage(machineNodeDto.getCurrentPageNo(), machineNodeDto.getPageSize());
        List<MachineNodeVo> machineNodeVos = machineNodeMapper.selectAll(machineNodeDto);
        PageInfo<MachineNodeVo> pageInfo = new PageInfo<>(machineNodeVos);
        return new PageVo(machineNodeDto.getCurrentPageNo(), machineNodeDto.getPageSize(), (int) pageInfo.getTotal(), pageInfo.getList());
    }

}