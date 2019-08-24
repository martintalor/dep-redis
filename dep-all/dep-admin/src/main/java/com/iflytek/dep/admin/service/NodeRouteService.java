package com.iflytek.dep.admin.service;

import com.iflytek.dep.admin.model.NodeRoute;
import com.iflytek.dep.admin.model.dto.NodeRouteDto;
import com.iflytek.dep.admin.model.vo.NodeRouteVo;
import com.iflytek.dep.admin.model.vo.PageVo;

import java.util.List;

/**
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.admin.service
 * @Description:
 * @date 2019/2/27
 */
public interface NodeRouteService {

    PageVo<NodeRouteVo> list(NodeRouteDto nodeRouteDto);

    int add(NodeRoute nodeRoute);

    int update(NodeRoute nodeRoute);

    void deleteList(List<NodeRoute> nodeRouteList);

    NodeRouteVo selectByPrimarykey(String leftNodeId, String rightNodeId);

    Integer selectLinkCountByNodeRoute(NodeRoute nodeRoute);
}
