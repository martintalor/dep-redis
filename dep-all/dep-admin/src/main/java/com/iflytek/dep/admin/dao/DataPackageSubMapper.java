package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.DataPackageSub;
import com.iflytek.dep.admin.model.vo.PackageVo;

import java.util.List;

public interface DataPackageSubMapper {
    int deleteByPrimaryKey(String subPackageId);

    int insert(DataPackageSub record);

    DataPackageSub selectByPrimaryKey(String subPackageId);

    List<DataPackageSub> selectAll();

    int updateByPrimaryKey(DataPackageSub record);

    List<PackageVo> listPackageSub(String packageId);
}