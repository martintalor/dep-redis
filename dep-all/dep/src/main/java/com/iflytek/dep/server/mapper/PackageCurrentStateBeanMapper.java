package com.iflytek.dep.server.mapper;

import com.iflytek.dep.server.model.PackageCurrentStateBean;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageCurrentStateBeanMapper {
    int deleteByPrimaryKey(@Param("packageId") String packageId, @Param("nodeId") String nodeId, @Param("toNodeId") String toNodeId);

    int insert(PackageCurrentStateBean record);

    PackageCurrentStateBean selectByPrimaryKey(@Param("packageId") String packageId, @Param("nodeId") String nodeId, @Param("toNodeId") String toNodeId);

    List<PackageCurrentStateBean> selectAll();

    int updateByPrimaryKey(PackageCurrentStateBean record);
}