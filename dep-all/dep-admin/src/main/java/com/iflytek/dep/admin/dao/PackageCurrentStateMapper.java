package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.PackageCurrentState;
import com.iflytek.dep.admin.model.vo.NodeLinkVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PackageCurrentStateMapper {
    int deleteByPrimaryKey(@Param("packageId") String packageId, @Param("nodeId") String nodeId, @Param("toNodeId") String toNodeId);

    int insert(PackageCurrentState record);

    PackageCurrentState selectByPrimaryKey(@Param("packageId") String packageId, @Param("nodeId") String nodeId, @Param("toNodeId") String toNodeId);

    List<PackageCurrentState> selectAll();

    int updateByPrimaryKey(PackageCurrentState record);

    NodeLinkVo selectByIDs(@Param("packageId") String packageId, @Param("nodeId") String nodeId, @Param("toNode") String toNode);
}