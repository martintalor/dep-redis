package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.MachineNode;
import com.iflytek.dep.admin.model.dto.MachineNodeDto;
import com.iflytek.dep.admin.model.vo.MachineNodeCountVo;
import com.iflytek.dep.admin.model.vo.MachineNodeTypeVo;
import com.iflytek.dep.admin.model.vo.MachineNodeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MachineNodeMapper {
    int deleteByPrimaryKey(String nodeId);

    int insert(MachineNode record);

    MachineNode selectByPrimaryKey(String nodeId);

    List<MachineNodeVo> selectAll(@Param("machineNodeDto") MachineNodeDto machineNodeDto);

    int updateByPrimaryKey(@Param("machineNode") MachineNode machineNode);

    List<MachineNodeTypeVo> listNode(@Param("type") String type);

    List<MachineNode> listNodeByServerNodeId(@Param("serverNodeId") String serverNodeId);

    /**
     * 根据serverNodeId统计物理节点个数
     * @param serverNodeId
     * @return
     */
    int countNode(@Param("serverNodeId") String serverNodeId);

    /**
     * 根据机构类型分组统计物理节点个数
     * @param orgTypeDm
     * @return
     */
    MachineNodeCountVo countNodeByOryType(@Param("orgTypeDm") String orgTypeDm);

    List<String> getServerNodeNameById(@Param("serverNodeId") String serverNodeId);

    MachineNodeVo selectSendRecvStatForBranch(String nodeId);

    List<MachineNode> listDocServer(@Param("serverNodeId") String serverNodeId);
}