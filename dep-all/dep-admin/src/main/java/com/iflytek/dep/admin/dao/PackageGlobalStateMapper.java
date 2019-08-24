package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.PackageGlobalState;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PackageGlobalStateMapper {
    int deleteByPrimaryKey(@Param("packageId") String packageId, @Param("toNodeId") String toNodeId);

    int insert(PackageGlobalState record);

    PackageGlobalState selectByPrimaryKey(@Param("packageId") String packageId, @Param("toNodeId") String toNodeId);

    List<PackageGlobalState> selectAll();

    int updateByPrimaryKey(PackageGlobalState record);

    int updateStateByIds(@Param("packageId") String packageId, @Param("toNode") String toNode);
}