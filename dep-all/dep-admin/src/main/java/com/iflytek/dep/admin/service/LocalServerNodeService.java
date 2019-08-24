package com.iflytek.dep.admin.service;

import com.iflytek.dep.admin.model.LocalServerNode;
import com.iflytek.dep.admin.model.OrgType;
import com.iflytek.dep.admin.model.ServerNodeType;
import com.iflytek.dep.admin.model.dto.LocalServerNodeDto;
import com.iflytek.dep.admin.model.dto.MachineNodeDto;
import com.iflytek.dep.admin.model.vo.LocalServerNodeTypeVo;
import com.iflytek.dep.admin.model.vo.LocalServerNodeVo;
import com.iflytek.dep.admin.model.vo.MachineNodeVo;
import com.iflytek.dep.admin.model.vo.PageVo;
import com.iflytek.dep.common.utils.ResponseBean;

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service
 * @Description:
 * @date 2019/2/25--15:24
 */
public interface LocalServerNodeService {

    public PageVo<LocalServerNodeVo> listLocalServerNode(LocalServerNodeDto localServerNodeDto);

    public int addLocalServerNode(LocalServerNode localServerNode);

    public int updateLocalServerNode(LocalServerNode localServerNode);

    public List<OrgType> listOrgType();

    public List<ServerNodeType> listServerNodeType();

    public List<LocalServerNodeTypeVo> listNode();

    LocalServerNode selectByServerNodeId(String serverNodeId);

    ResponseBean deleteLocalServerNode(String serverNodeId);

    PageVo<MachineNodeVo> getLocalServerNode(MachineNodeDto machineNodeDto);
}
