package com.iflytek.dep.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iflytek.dep.admin.dao.*;
import com.iflytek.dep.admin.model.FTPConfig;
import com.iflytek.dep.admin.model.MachineNode;
import com.iflytek.dep.admin.model.OrgType;
import com.iflytek.dep.admin.model.dto.MachineNodeDetailDto;
import com.iflytek.dep.admin.model.dto.MachineNodeDto;
import com.iflytek.dep.admin.model.vo.*;
import com.iflytek.dep.admin.service.MachineNodeService;
import com.iflytek.dep.admin.utils.CommonConstants;
import com.iflytek.dep.common.utils.ResponseBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.iflytek.dep.admin.utils.CommonConstants.SERVER_NODE_TYPE.DISABLE;
import static com.iflytek.dep.admin.utils.CommonConstants.SERVER_NODE_TYPE.ENABLE;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service.impl
 * @Description:
 * @date 2019/2/23--17:22
 */
@Service
public class MachineNodeServiceImpl implements MachineNodeService {

    @Autowired
    private MachineNodeMapper machineNodeMapper;
    @Autowired
    private FTPConfigMapper ftpConfigMapper;
    @Autowired
    private LocalServerNodeMapper localServerNodeMapper;
    @Autowired
    private NodeAppMapper nodeAppMapper;
    @Autowired
    private OrgTypeMapper orgTypeMapper;


    @Override
    public PageVo<MachineNodeVo> selectAll(MachineNodeDto machineNodeDto) {
        PageHelper.startPage(machineNodeDto.getCurrentPageNo(), machineNodeDto.getPageSize());
        List<MachineNodeVo> machineNodeVos = machineNodeMapper.selectAll(machineNodeDto);
        PageInfo<MachineNodeVo> pageInfo = new PageInfo<>(machineNodeVos);

        List<MachineNodeVo> resultList = pageInfo.getList();

        //如果当前节点是中心平台，还需要统计分平台的收发数量
        if (machineNodeDto.getServerNodeTypeDm().equals("01")) {
            for (MachineNodeVo machineNodeVo : resultList) {
                if (!machineNodeVo.getServerNodeTypeDm().equals("01")) {
                    machineNodeVo.setProbeResult(null);//中心平台无法查询分平台的节点状态，置为Null

                    //当前逻辑节点是中心平台，分平台的物理节点收发数量单独查询
                    MachineNodeVo machineNodeVo1 = machineNodeMapper.selectSendRecvStatForBranch(machineNodeVo.getNodeId());
                    if (machineNodeVo1 != null) {
                        machineNodeVo.setSendNumFail(machineNodeVo1.getSendNumFail());
                        machineNodeVo.setSendNumSucc(machineNodeVo1.getSendNumSucc());
                        machineNodeVo.setTotalReceive(machineNodeVo1.getTotalReceive());
                    }
                }
            }
        }

        return new PageVo(machineNodeDto.getCurrentPageNo(), machineNodeDto.getPageSize(), (int) pageInfo.getTotal(), resultList);

    }

    @Override
    @Transactional
    public void addMachineNode(MachineNodeDetailDto machineNodeDetailDto) {
        // 构造物理节点信息，并入库
        MachineNode machineNode = new MachineNode();
        machineNode.setNodeId(machineNodeDetailDto.getNodeId());
        machineNode.setMachineIp(machineNodeDetailDto.getMachineIp());
        machineNode.setServerNodeId(machineNodeDetailDto.getServerNodeId());
        machineNode.setNodeRemark(machineNodeDetailDto.getNodeRemark());
        machineNode.setNodeTypeDm(machineNodeDetailDto.getNodeTypeDm());
        machineNode.setFlagDelete(ENABLE);
        machineNode.setFlagEnable(ENABLE);
        machineNodeMapper.insert(machineNode);
        // 物理节点是ftp，构造ftp配置信息
        if (machineNodeDetailDto.getNodeTypeDm().equals(CommonConstants.SERVER_NODE_TYPE.FTP_SERVER)) {
            FTPConfig config = new FTPConfig();
            BeanUtils.copyProperties(machineNodeDetailDto, config);
            config.setFtpId("FTP_" + machineNodeDetailDto.getNodeId());
            config.setFtpIp(machineNodeDetailDto.getMachineIp());
            ftpConfigMapper.insert(config);
        }
    }

    @Override
    public MachineNodeDetailDto getMachineNode(String nodeId) {
        MachineNodeDetailDto dto = new MachineNodeDetailDto();
        MachineNode node = machineNodeMapper.selectByPrimaryKey(nodeId);
        BeanUtils.copyProperties(node, dto);
        if (node.getNodeTypeDm().equals(CommonConstants.SERVER_NODE_TYPE.FTP_SERVER)) {
            FTPConfig config = ftpConfigMapper.selectByNodeId(nodeId);
            BeanUtils.copyProperties(config, dto);
        }
        return dto;
    }

    @Override
    public void updateMachineNode(MachineNodeDetailDto machineNodeDetailDto) {
        MachineNode node = machineNodeMapper.selectByPrimaryKey(machineNodeDetailDto.getNodeId());
        // 先更新物理节点
        MachineNode machineNode = new MachineNode();
        machineNode.setNodeId(machineNodeDetailDto.getNodeId());
        machineNode.setMachineIp(machineNodeDetailDto.getMachineIp());
        machineNode.setServerNodeId(machineNodeDetailDto.getServerNodeId());
        machineNode.setNodeRemark(machineNodeDetailDto.getNodeRemark());
        machineNodeMapper.updateByPrimaryKey(machineNode);
        // 物理节点是ftp，在更新ftp配置信息
        if (node.getNodeTypeDm().equals(CommonConstants.SERVER_NODE_TYPE.FTP_SERVER)) {
            FTPConfig config = new FTPConfig();
            BeanUtils.copyProperties(machineNodeDetailDto, config);
            config.setFtpIp(machineNodeDetailDto.getMachineIp());
            ftpConfigMapper.updateByNodeId(config);
        }
    }

    @Override
    public void openMachineNode(String nodeId) {
        MachineNode machineNode = new MachineNode();
        machineNode.setNodeId(nodeId);
        machineNode.setFlagEnable(ENABLE);
        machineNodeMapper.updateByPrimaryKey(machineNode);
    }

    @Override
    public void closeMachineNode(String nodeId) {
        MachineNode machineNode = new MachineNode();
        machineNode.setNodeId(nodeId);
        machineNode.setFlagEnable(DISABLE);
        machineNodeMapper.updateByPrimaryKey(machineNode);
    }

    @Override
    public List<MachineNodeTypeVo> listNode(String type) {
        return machineNodeMapper.listNode(type);
    }

    @Override
    public List<MachineNodeCountVo> countNode() {
        List<MachineNodeCountVo> countVos = new ArrayList<>();
        List<OrgType> orgTypes = orgTypeMapper.selectAll();
        if (!CollectionUtils.isEmpty(orgTypes)) {
            for (OrgType dto : orgTypes) {
                MachineNodeCountVo count = machineNodeMapper.countNodeByOryType(dto.getOrgTypeDm());
                if (null != count) {
                    count.setServerNodeId(dto.getOrgTypeDm());
                    count.setServerNodeName(dto.getOrgTypeMc());
                    countVos.add(count);
                } else {
                    countVos.add(new MachineNodeCountVo(dto.getOrgTypeDm(), dto.getOrgTypeMc(), 0, 0));
                }
            }
        }
        return countVos;
    }

    @Override
    @Transactional
    public ResponseBean deleteMachineNode(String nodeId) {
        // 查看物理节点是否被应用绑定
        List<String> appNames = nodeAppMapper.getAppNameByNodeId(nodeId);
        if (!CollectionUtils.isEmpty(appNames)) {
            return new ResponseBean("应用【" + appNames.get(0) + "】绑定当前节点，不可删除");
        }
        // 删除逻辑节点（逻辑节点类型为ftp，先删除ftp配置）
        ftpConfigMapper.deleteByNodeId(nodeId);
        return new ResponseBean(machineNodeMapper.deleteByPrimaryKey(nodeId));
    }
}