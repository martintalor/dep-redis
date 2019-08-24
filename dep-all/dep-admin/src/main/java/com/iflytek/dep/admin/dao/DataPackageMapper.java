package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.DataPackage;
import com.iflytek.dep.admin.model.dto.DataPackageDto;
import com.iflytek.dep.admin.model.vo.DataPackageVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataPackageMapper {
    int deleteByPrimaryKey(String packageId);

    int insert(DataPackage record);

    DataPackageVo selectByPrimaryKey(@Param("packageId") String packageId, @Param("toNodeId") String toNodeId);

    List<DataPackageVo> selectAll(@Param("dataPackageDto") DataPackageDto dataPackageDto);

    int updateByPrimaryKey(DataPackage record);

    DataPackage selectByPackageId(String packageId);

    List<String> getFailPackageId(@Param("packageId") String packageId, @Param("toNodeId") String toNodeId);
}