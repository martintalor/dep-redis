package com.iflytek.dep.admin.service.impl;

import cn.hutool.http.HttpUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iflytek.dep.admin.constants.GlobalSendStateEnum;
import com.iflytek.dep.admin.constants.OperateStateEnum;
import com.iflytek.dep.admin.constants.RecvSendStateEnum;
import com.iflytek.dep.admin.dao.*;
import com.iflytek.dep.admin.model.*;
import com.iflytek.dep.admin.model.dto.DataPackageDto;
import com.iflytek.dep.admin.model.vo.*;
import com.iflytek.dep.admin.service.DataPackageService;
import com.iflytek.dep.admin.utils.CommonConstants;
import com.iflytek.dep.common.utils.ResponseBean;
import com.iflytek.dep.common.utils.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service.impl
 * @Description:
 * @date 2019/2/28--20:29
 */
@Service
public class DataPackageServiceImpl implements DataPackageService {

    Logger logger = LoggerFactory.getLogger(DataPackageService.class);

    @Value("${logicServerNode.serverNodeId}")
    private String serverNodeId; // 当前逻辑节点

    @Value("${is.center}")
    private String center;//当前节点是否是中心

    @Autowired
    private DataPackageMapper dataPackageMapper;

    @Autowired
    private DataPackageSubMapper dataPackageSubMapper;

    @Autowired
    private NodeLinkMapper nodeLinkMapper;

    @Autowired
    private PackageCurrentStateMapper packageCurrentStateMapper;

    @Autowired
    private MachineNodeMapper machineNodeMapper;

    @Autowired
    private DataNodeProcessMapper dataNodeProcessMapper;

    @Autowired
    private DEPServerMapper depServerMapper;

    @Autowired
    private PackageGlobalStateMapper packageGlobalStateMapper;


    @Override
    public PageVo<DataPackageVo> listDataPackage(DataPackageDto dataPackageDto) {
        PageHelper.startPage(dataPackageDto.getCurrentPageNo(), dataPackageDto.getPageSize());
        List<DataPackageVo> dataPackageVos = dataPackageMapper.selectAll(dataPackageDto);
        if (!CollectionUtils.isEmpty(dataPackageVos)) {
            for (DataPackageVo dto : dataPackageVos) {
                if (null != dto.getGlobalStateDm() && null != GlobalSendStateEnum.getByStateCode(dto.getGlobalStateDm())) {
                    dto.setGlobalStateMc(GlobalSendStateEnum.getByStateCode(dto.getGlobalStateDm()).getStateName());
                    // 判断是否可以重试
                    if (dto.getGlobalStateDm().equals("00") && dto.getServerNodeId().equals(serverNodeId)) {
                        dto.setIsRetry(1);
                    }
                }
                //查看是否拆包 0未拆包 1拆包
                List<PackageVo> listPackageSub = dataPackageSubMapper.listPackageSub(dto.getPackageId());
                if (!CollectionUtils.isEmpty(listPackageSub)) {
                    dto.setIsUnpack(1);
                }
            }
        }
        PageInfo<DataPackageVo> pageInfo = new PageInfo<>(dataPackageVos);
        return new PageVo(dataPackageDto.getCurrentPageNo(), dataPackageDto.getPageSize(), (int) pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public DataPackageVo getDataPackage(String packageId, String toNodeId) {
        DataPackageVo dataPackageVo = dataPackageMapper.selectByPrimaryKey(packageId, toNodeId);
        //查看是否拆包 0未拆包 1拆包
        List<PackageVo> listPackageSub = dataPackageSubMapper.listPackageSub(packageId);
        if (null != dataPackageVo && !CollectionUtils.isEmpty(listPackageSub)) {
            dataPackageVo.setIsUnpack(1);
        }
        //状态码翻译
        if (null != dataPackageVo && null != dataPackageVo.getGlobalStateDm() && null != GlobalSendStateEnum.getByStateCode(dataPackageVo.getGlobalStateDm())) {
            dataPackageVo.setGlobalStateMc(GlobalSendStateEnum.getByStateCode(dataPackageVo.getGlobalStateDm()).getStateName());
        }
        return dataPackageVo;
    }

    @Override
    public List<PackageVo> listPackageSub(String packageId) {
        List<PackageVo> listPackage = new ArrayList<PackageVo>();
        // 添加主包名称
        PackageVo packageVo = new PackageVo();
        packageVo.setPackageId(packageId);
        listPackage.add(packageVo);
        List<PackageVo> listPackageSub = dataPackageSubMapper.listPackageSub(packageId);
        if (null != listPackageSub) {
            Collections.sort(listPackageSub);
            listPackage.addAll(listPackageSub);
        }
        return listPackage;
    }

    /**
     * 查看链路
     * 1. 先获取发往目的节点
     * 2. 根据当前节点、不同的目的节点构建每一条链路（当前节点为接收节点，只构建单条链路）
     *
     * @param packageVo
     * @return
     */
    @Override
    public List<List<NodeLinkVo>> getNodeLink(PackageVo packageVo) {
        // 通过主包id构建主包、子包链路节点信息（子包的链路和主包相同）
        if (StringUtils.isEmpty(packageVo.getParentId())) {
            packageVo.setParentId(packageVo.getPackageId());
        }
        // 查询目标节点(可能存在多个目标节点)
        List<String> toNodes = nodeLinkMapper.selectToNodesByPackageId(packageVo.getParentId());
        if (CollectionUtils.isEmpty(toNodes)) {
            return null;
        }
        //判断如果是中心的话就不用过滤其他节点将全部链路取出
        if(!Boolean.valueOf(center)){
            // 目标节点归属当前逻辑节点，则过滤其他节点
            List<String> filterNodes = new ArrayList<>();
            for (String toNode : toNodes) {
                MachineNode node = machineNodeMapper.selectByPrimaryKey(toNode);
                if (node.getServerNodeId().equals(serverNodeId)) {
                    filterNodes.add(toNode);
                }
            }
            if (!CollectionUtils.isEmpty(filterNodes)) {
                toNodes.clear();
                toNodes.addAll(filterNodes);
            }
        }
        List<List<NodeLinkVo>> all = new ArrayList<>();
        // 构造每一个目的地的链路
        for (String toNode : toNodes) {
            // 根据toNode获取链路
            List<NodeLinkVo> nodeLinkVos = nodeLinkMapper.selectByPackageId(packageVo.getParentId(), toNode);
            if (!CollectionUtils.isEmpty(nodeLinkVos)) {
                for (NodeLinkVo nodeLinkVo : nodeLinkVos) {
                    // 通过packageId、节点id构建节点状态信息
                    NodeLinkVo state = packageCurrentStateMapper.selectByIDs(nodeLinkVo.getPackageId(), nodeLinkVo.getLeftNodeId(), nodeLinkVo.getToNodeId());
                    if (null != state) {
                        String sendState = state.getSendStateDm();
                        nodeLinkVo.setSendStateDm(sendState);
                        if (null != RecvSendStateEnum.getByStateCode(sendState)) {
                            nodeLinkVo.setSendStateMc(RecvSendStateEnum.getByStateCode(sendState).getStateName());
                        }
                        nodeLinkVo.setOperateStateDm(state.getOperateStateDm());
                        nodeLinkVo.setCreateTime(state.getCreateTime());
                        nodeLinkVo.setUpdateTime(state.getUpdateTime());
                    }
                    // 根据当前逻辑节点判断是否可点击
                    if (nodeLinkVo.getServerNodeId().equals(serverNodeId)) {
                        nodeLinkVo.setFlagClick(true);
                    } else {
                        nodeLinkVo.setFlagClick(false);
                    }
                }
               /* // 获取目的地节点信息
                PackageCurrentState state = packageCurrentStateMapper.selectByPrimaryKey(to.getPackageId(), toNode, toNode);
                if (null != state) {
                    to.setSendStateDm(state.getSendStateDm());
                    to.setOperateStateDm(state.getOperateStateDm());
                    to.setCreateTime(state.getCreateTime());
                    to.setUpdateTime(state.getUpdateTime());
                    if (null != RecvSendStateEnum.getByStateCode(state.getSendStateDm())) {
                        to.setSendStateMc(RecvSendStateEnum.getByStateCode(state.getSendStateDm()).getStateName());
                    }
                }*/
                // 去链路末尾节点用于构建目标节点
                NodeLinkVo last = nodeLinkVos.get(nodeLinkVos.size() - 1);
                // 目标节点的状态 改从全局状态获取
                PackageGlobalState state = packageGlobalStateMapper.selectByPrimaryKey(last.getPackageId().split("\\.")[0], toNode);
                // 全局状态已成功 整条链路已完成
                if (null != state && state.getGlobalStateDm().equals(GlobalSendStateEnum.END.getStateCode())) {
                    // 目标节点前一节点发送成功
                    last.setSendStateDm(RecvSendStateEnum.SENDSUCC.getStateCode());
                }
                // 构建目的地节点状态信息
                NodeLinkVo to = new NodeLinkVo();
                BeanUtils.copyProperties(last, to);
                to.setLinkId(UUIDGenerator.createUUID());
                to.setLeftNodeId(toNode);
                to.setRightNodeId(null);
                to.setOrderId(to.getOrderId() + 1);
                if (null != state && null != to.getSendStateDm()) {
                    to.setCreateTime(state.getCreateTime());
                    to.setUpdateTime(state.getUpdateTime());
                    // 异常
                    if (state.getGlobalStateDm().equals(GlobalSendStateEnum.FAIL.getStateCode())) {
                        to.setSendStateDm(RecvSendStateEnum.FAIL.getStateCode());
                        to.setSendStateMc(RecvSendStateEnum.FAIL.getStateName());
                    } else if (state.getGlobalStateDm().equals(GlobalSendStateEnum.END.getStateCode())) {
                        to.setSendStateDm(RecvSendStateEnum.RECVED.getStateCode());
                        to.setSendStateMc(RecvSendStateEnum.RECVED.getStateName());
                    } else {
                        to.setSendStateDm(RecvSendStateEnum.RECVING.getStateCode());
                        to.setSendStateMc(RecvSendStateEnum.RECVING.getStateName());
                    }
                }
                // 获取目的地节点名称
                MachineNode node = machineNodeMapper.selectByPrimaryKey(toNode);
                if (null != node) {
                    to.setLeftNodeRemark(node.getNodeRemark());
                }
                // 根据当前逻辑节点判断是否可点击
                if (node.getServerNodeId().equals(serverNodeId)) {
                    to.setFlagClick(true);
                } else {
                    to.setFlagClick(false);
                }
                nodeLinkVos.add(to);
                // 统一处理前一逻辑节点的状态信息
                dealWith(nodeLinkVos);
                all.add(nodeLinkVos);
            }
        }
        return all;
    }

    /**
     * 统一处理前一逻辑节点的状态信息
     *
     * @param nodeLinkVos
     */
    private void dealWith(List<NodeLinkVo> nodeLinkVos) {
        for (int i = 0; i < nodeLinkVos.size() - 1; i++) {
            if (!nodeLinkVos.get(i).getServerNodeId().equals(nodeLinkVos.get(i + 1).getServerNodeId()) && (null != nodeLinkVos.get(i + 1).getSendStateDm())) {
                for (int j = 0; j < i + 1; j++) {
                    nodeLinkVos.get(j).setSendStateDm(RecvSendStateEnum.SENDSUCC.getStateCode());
                    nodeLinkVos.get(j).setSendStateMc(RecvSendStateEnum.SENDSUCC.getStateName());
                }
            }
        }
    }

    @Override
    public List<NodeLinkDetailVo> getNodeLinkDetail(PackageVo packageVo) {
        // 通过主包id构建主包、子包链路节点信息（子包的链路和主包相同）
        if (StringUtils.isEmpty(packageVo.getParentId())) {
            packageVo.setParentId(packageVo.getPackageId());
        }
        // 根据主包、逻辑节点获取部分链路
        List<NodeLinkDetailVo> all = new ArrayList<>();
        List<NodeLinkVo> nodeLinkVos = nodeLinkMapper.selectByIds(packageVo.getParentId(), serverNodeId);
        // 获取数据包大小
        BigDecimal packageSize = new BigDecimal(0);
        DataPackage pack = dataPackageMapper.selectByPackageId(packageVo.getPackageId());
        if (null != pack && null != pack.getPackageSize()) {
            packageSize = pack.getPackageSize();
        }
        DataPackageSub subPack = dataPackageSubMapper.selectByPrimaryKey(packageVo.getPackageId());
        if (null != subPack && null != subPack.getPackageSize()) {
            packageSize = subPack.getPackageSize();
        }
        int count = 1; // 中心节点合并个数
        // 构建链路具体信息
        if (!CollectionUtils.isEmpty(nodeLinkVos)) {
            outterLoop:
            for (NodeLinkVo nodeLinkVo : nodeLinkVos) {
                // 对中心节点的节点合并
                if (!CollectionUtils.isEmpty(all)) {
                    for (NodeLinkDetailVo result : all) {
                        if (result.getNodeId().equals(nodeLinkVo.getLeftNodeId())) {
                            count++;
                            // 通过packageId、节点id、toNode构建节点状态信息
                            List<OperateStateVo> states = dataNodeProcessMapper.selectByIDs(nodeLinkVo.getPackageId(), nodeLinkVo.getLeftNodeId(), nodeLinkVo.getToNodeId());
                            setNameByCode(states, result);
                            if (!CollectionUtils.isEmpty(states)) {
                                // 平均传输速率 累计耗时为空，则默认1s
                                result.setAverageTransmissionRate(count * packageSize.doubleValue() + "");
                                if (0 != result.getTotalSpendTime()) {
                                    result.setAverageTransmissionRate(String.format("%.3f", count * packageSize.doubleValue() / (1 * result.getTotalSpendTime())));
                                }
                                result.getList().addAll(states);
                            }
                            continue outterLoop;
                        }
                    }
                }
                NodeLinkDetailVo dto = new NodeLinkDetailVo();
                dto.setNodeId(nodeLinkVo.getLeftNodeId());
                dto.setNodeRemark(nodeLinkVo.getLeftNodeRemark());
                dto.setNodeType(nodeLinkVo.getLeftNodeType());
                // 通过packageId、节点id、toNode构建节点状态信息
                List<OperateStateVo> states = dataNodeProcessMapper.selectByIDs(nodeLinkVo.getPackageId(), nodeLinkVo.getLeftNodeId(), nodeLinkVo.getToNodeId());
                setNameByCode(states, dto);
                if (!CollectionUtils.isEmpty(states)) {
                    // 平均传输速率
                    dto.setAverageTransmissionRate(packageSize.doubleValue() + "");
                    if (0 != dto.getTotalSpendTime()) {
                        dto.setAverageTransmissionRate(String.format("%.3f", packageSize.doubleValue() / (1 * dto.getTotalSpendTime())));
                    }
                    dto.setList(states);
                }
                all.add(dto);
            }
            // 如果要是链路的末节点，需要构建目的地节点的详细信息
            NodeLinkVo last = nodeLinkVos.get(nodeLinkVos.size() - 1);
            String toNode = last.getToNodeId();
            if (last.getRightNodeId().equals(toNode)) {
                NodeLinkDetailVo to = new NodeLinkDetailVo();
                to.setNodeId(last.getToNodeId());
                // 获取目的地节点名称
                MachineNode node = machineNodeMapper.selectByPrimaryKey(toNode);
                if (null != node) {
                    to.setNodeRemark(node.getNodeRemark());
                    to.setNodeType(node.getNodeTypeDm());
                }
                List<OperateStateVo> states = dataNodeProcessMapper.selectByIDs(last.getPackageId(), toNode, toNode);
                setNameByCode(states, to);
                if (!CollectionUtils.isEmpty(states)) {
                    // 平均传输速率
                    if (0 != to.getTotalSpendTime()) {
                        to.setAverageTransmissionRate(String.format("%.3f", packageSize.doubleValue() / (1 * to.getTotalSpendTime())));
                    } else {
                        to.setAverageTransmissionRate(packageSize.doubleValue() + "");
                    }
                    to.setList(states);
                    all.add(to);
                }
            }
        }
        return all;
    }

    @Override
    public ResponseBean reTryPackage(String packageId, String toNodeId) {
        Map<String, Object> map = new HashMap<>();
        packageId = packageId.split("\\.")[0];
        List<DEPServer> depServer = depServerMapper.selectByServerNodeId(serverNodeId);
        if (CollectionUtils.isEmpty(depServer)) {
            return new ResponseBean("未获取到服务");
        }
        if (serverNodeId.equals(CommonConstants.LOGICAL_SERVER_NODE.TYPE)) {
            String url = depServer.get(0).getDepServerIp() + "/service/dataPack/mainRetryDown";
            //  重试所有的数据包
            reTryFailPackage(packageId, url, map);
            return new ResponseBean();
        } else {
            MachineNode node = machineNodeMapper.selectByPrimaryKey(toNodeId);
            if (node.getServerNodeId().equals(serverNodeId)) {
                String url = depServer.get(0).getDepServerIp() + "/service/dataPack/leafRetryDown";
                //  重试所有的数据包
                reTryFailPackage(packageId, url, map);
                return new ResponseBean();
            } else {
                String url = depServer.get(0).getDepServerIp() + "/service/dataPack/leafRetryUp";
                //  重试所有的数据包 只传packageId
                logger.info(packageId + "调用重试服务开始");
                map.put("fileName", packageId);
                HttpUtil.post(url, map, 6000);
                logger.info(packageId + "调用重试服务结束");
                return new ResponseBean();
            }
        }
    }

    /**
     * 数据包接收状态人工确认
     *
     * @param packageId
     * @param toNodeId
     */
    @Override
    public void confirmState(String packageId, String toNodeId) {
        packageId = packageId.split("\\.")[0];
        packageGlobalStateMapper.updateStateByIds(packageId, toNodeId);
    }

    /**
     * 重试失败数据包
     *
     * @param packageId
     * @return
     */
    private void reTryFailPackage(String packageId, String url, Map<String, Object> map) {
        List<String> packages = dataPackageMapper.getFailPackageId(packageId, null);
        if (!CollectionUtils.isEmpty(packages)) {
            for (String s : packages) {
                logger.info(s + "调用重试服务开始");
                map.put("fileName", s);
                HttpUtil.post(url, map, 6000);
                logger.info(s + "调用重试服务结束");
            }
        }
    }

    /**
     * 设置字典
     *
     * @param operateStateVos
     */
    public void setNameByCode(List<OperateStateVo> operateStateVos, NodeLinkDetailVo nodeLinkDetailVo) {
        if (!CollectionUtils.isEmpty(operateStateVos)) {
            long time = nodeLinkDetailVo.getTotalSpendTime();
            for (OperateStateVo dto : operateStateVos) {
                // 翻译状态
                if (null != dto.getOperateStateDm() && null != OperateStateEnum.getByStateCode(dto.getOperateStateDm())) {
                    dto.setOperateStateMc(OperateStateEnum.getByStateCode(dto.getOperateStateDm()).getStateName());
                }
                // 累计总时间
                if (null != dto.getSpendTime()) {
                    time = time + dto.getSpendTime();
                }
            }
            nodeLinkDetailVo.setTotalSpendTime(time);
        }
    }
}