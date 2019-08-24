package com.iflytek.dep.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iflytek.dep.admin.dao.NodeAppMapper;
import com.iflytek.dep.admin.model.NodeApp;
import com.iflytek.dep.admin.model.dto.NodeAppDto;
import com.iflytek.dep.admin.model.vo.LocalServerNodeVo;
import com.iflytek.dep.admin.model.vo.NodeAppVo;
import com.iflytek.dep.admin.model.vo.PageVo;
import com.iflytek.dep.admin.service.NodeAppNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service.impl
 * @Description:
 * @date 2019/2/26--21:04
 */
@Service
public class NodeAppNodeServiceImpl implements NodeAppNodeService {

    @Autowired
    private NodeAppMapper nodeAppMapper;

    @Override
    public PageVo<NodeAppVo> listNodeApp(NodeAppDto nodeAppDto) {
        PageHelper.startPage(nodeAppDto.getCurrentPageNo(), nodeAppDto.getPageSize());
        List<NodeAppVo> nodeAppVos = nodeAppMapper.selectAll(nodeAppDto);
        PageInfo<NodeAppVo> pageInfo = new PageInfo<>(nodeAppVos);
        return new PageVo(nodeAppDto.getCurrentPageNo(), nodeAppDto.getPageSize(), (int) pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public int addNodeApp(NodeApp nodeApp) {
        return nodeAppMapper.insert(nodeApp);
    }

    @Override
    public int updateNodeApp(NodeApp nodeApp) {
        return nodeAppMapper.updateByPrimaryKey(nodeApp);
    }

    @Override
    public int deleteNodeApp(String appId) {
        return nodeAppMapper.deleteByPrimaryKey(appId);
    }
}