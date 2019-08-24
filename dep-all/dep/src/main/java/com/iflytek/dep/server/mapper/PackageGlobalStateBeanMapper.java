package com.iflytek.dep.server.mapper;

import com.iflytek.dep.server.model.PackageGlobalStateBean;
import com.iflytek.dep.server.model.UnfinishedPack;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageGlobalStateBeanMapper {
    int deleteByPrimaryKey(@Param("packageId") String packageId, @Param("toNodeId") String toNodeId);

    int insert(PackageGlobalStateBean record);

    PackageGlobalStateBean selectByPrimaryKey(@Param("packageId") String packageId, @Param("toNodeId") String toNodeId);

    List<PackageGlobalStateBean> selectAll();

    int updateByPrimaryKey(PackageGlobalStateBean record);

    int getFailPackageId(@Param("packageId") String packageId, @Param("toNodeId") String toNodeId);

    List<UnfinishedPack> getUnfinishedPackageId(@Param("serverNodeId") String serverNodeId);

    int updateUnfinishedById(@Param("packageId") String packageId);

    int updateFinishedById(@Param("packageId") String packageId, @Param("toNodeId") String toNodeId);

    int updateExceptionStateById(@Param("packageId") String packageId);

    List<UnfinishedPack> getUnfinishedPackageList(@Param("currentNodeId")String currentNodeId);

    List<PackageGlobalStateBean> selectByPrimaryKeys(List<String> packageIds);
}