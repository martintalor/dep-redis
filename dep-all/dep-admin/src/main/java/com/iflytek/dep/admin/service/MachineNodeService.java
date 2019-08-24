package com.iflytek.dep.admin.service;

import com.iflytek.dep.admin.model.dto.MachineNodeDetailDto;
import com.iflytek.dep.admin.model.dto.MachineNodeDto;
import com.iflytek.dep.admin.model.vo.MachineNodeCountVo;
import com.iflytek.dep.admin.model.vo.MachineNodeTypeVo;
import com.iflytek.dep.admin.model.vo.MachineNodeVo;
import com.iflytek.dep.admin.model.vo.PageVo;
import com.iflytek.dep.common.utils.ResponseBean;

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service
 * @Description:
 * @date 2019/2/23--17:20
 */
public interface MachineNodeService {


    PageVo<MachineNodeVo> selectAll(MachineNodeDto machineNodeDto);

    void addMachineNode(MachineNodeDetailDto machineNodeDetailDto);

    MachineNodeDetailDto getMachineNode(String nodeId);

    void updateMachineNode(MachineNodeDetailDto machineNodeDetailDto);

    void openMachineNode(String nodeId);

    void closeMachineNode(String nodeId);

    List<MachineNodeTypeVo> listNode(String type);

    List<MachineNodeCountVo> countNode();

    ResponseBean deleteMachineNode(String nodeId);
}
