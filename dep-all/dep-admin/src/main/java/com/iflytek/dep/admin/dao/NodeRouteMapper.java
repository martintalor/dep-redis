package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.NodeRoute;
import com.iflytek.dep.admin.model.dto.NodeRouteDto;
import com.iflytek.dep.admin.model.vo.NodeRouteVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NodeRouteMapper {

    List<NodeRouteVo> selectAll(NodeRouteDto nodeRouteDto);

    int insertSelective(NodeRoute nodeRoute);

    int updateByPrimaryKeySelective(NodeRoute nodeRoute);

    void deleteList(List<NodeRoute> nodeRouteList);

    NodeRouteVo selectBy(@Param("leftNodeId") String leftNodeId, @Param("rightNodeId") String rightNodeId);
}