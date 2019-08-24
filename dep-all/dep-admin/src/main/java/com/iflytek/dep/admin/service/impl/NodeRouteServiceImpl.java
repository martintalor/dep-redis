package com.iflytek.dep.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iflytek.dep.admin.dao.MachineNodeMapper;
import com.iflytek.dep.admin.dao.NodeLinkMapper;
import com.iflytek.dep.admin.dao.NodeRouteMapper;
import com.iflytek.dep.admin.model.MachineNode;
import com.iflytek.dep.admin.model.NodeRoute;
import com.iflytek.dep.admin.model.dto.NodeRouteDto;
import com.iflytek.dep.admin.model.vo.NodeRouteVo;
import com.iflytek.dep.admin.model.vo.PageVo;
import com.iflytek.dep.admin.service.NodeRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.admin.service.impl
 * @Description:
 * @date 2019/2/27
 */
@Service
public class NodeRouteServiceImpl implements NodeRouteService {

    @Autowired
    private NodeRouteMapper nodeRouteMapper;
    @Autowired
    private MachineNodeMapper machineNodeMapper;
    @Autowired
    private NodeLinkMapper nodeLinkMapper;

    @Override
    public PageVo<NodeRouteVo> list(NodeRouteDto nodeRouteDto) {
        PageHelper.startPage(nodeRouteDto.getCurrentPageNo(), nodeRouteDto.getPageSize());
        List<NodeRouteVo> nodeRouteVos = nodeRouteMapper.selectAll(nodeRouteDto);
        PageInfo<NodeRouteVo> pageInfo = new PageInfo<>(nodeRouteVos);
        return new PageVo(nodeRouteDto.getCurrentPageNo(), nodeRouteDto.getPageSize(), (int) pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public int add(NodeRoute nodeRoute) {
        MachineNode leftNode = machineNodeMapper.selectByPrimaryKey(nodeRoute.getLeftNodeId());
        MachineNode rightNode = machineNodeMapper.selectByPrimaryKey(nodeRoute.getRightNodeId());

        if (leftNode != null) {
            nodeRoute.setLeftServerNode(leftNode.getServerNodeId());
        }
        if (rightNode != null) {
            nodeRoute.setRightServerNode(rightNode.getServerNodeId());
        }

        return nodeRouteMapper.insertSelective(nodeRoute);
    }

    @Override
    public int update(NodeRoute nodeRoute) {
        return nodeRouteMapper.updateByPrimaryKeySelective(nodeRoute);
    }

    @Override
    public void deleteList(List<NodeRoute> nodeRouteList) {
        nodeRouteMapper.deleteList(nodeRouteList);
    }

    @Override
    public NodeRouteVo selectByPrimarykey(String leftNodeId, String rightNodeId) {

        return nodeRouteMapper.selectBy(leftNodeId, rightNodeId);
    }

    @Override
    public Integer selectLinkCountByNodeRoute(NodeRoute nodeRoute) {
        return nodeLinkMapper.selectCountByNodeRoute(nodeRoute);
    }

}