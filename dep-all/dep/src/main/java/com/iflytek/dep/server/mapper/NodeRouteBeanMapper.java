package com.iflytek.dep.server.mapper;

import java.util.List;

import com.iflytek.dep.server.model.NodeOperateStateBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRouteBeanMapper {
    int deleteByPrimaryKey(@Param("leftNodeId") String leftNodeId, @Param("rightNodeId") String rightNodeId);

    int insert(com.iflytek.dep.server.model.NodeRouteBean record);

    com.iflytek.dep.server.model.NodeRouteBean selectByPrimaryKey(@Param("leftNodeId") String leftNodeId, @Param("rightNodeId") String rightNodeId);

    List<com.iflytek.dep.server.model.NodeRouteBean> selectAll();

    int updateByPrimaryKey(com.iflytek.dep.server.model.NodeRouteBean record);

}