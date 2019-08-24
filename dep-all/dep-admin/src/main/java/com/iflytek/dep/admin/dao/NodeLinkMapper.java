package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.NodeLink;
import com.iflytek.dep.admin.model.NodeRoute;
import com.iflytek.dep.admin.model.vo.NodeLinkVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NodeLinkMapper {
    int deleteByPrimaryKey(String linkId);

    int insert(NodeLink record);

    NodeLink selectByPrimaryKey(String linkId);

    List<NodeLink> selectAll();

    int updateByPrimaryKey(NodeLink record);

    List<String> selectToNodesByPackageId(String packageId);

    List<NodeLinkVo> selectByPackageId(@Param("packageId") String packageId, @Param("toNode") String toNode);

    List<NodeLinkVo> selectByIds(@Param("packageId") String packageId, @Param("serverNodeId") String serverNodeId);

    Integer selectCountByNodeRoute(NodeRoute nodeRoute);
}