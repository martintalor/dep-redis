package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.DepEtlJobRecordersDetail;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface DepEtlJobRecordersDetailMapper {
    int deleteByPrimaryKey(String id);

    int insert(DepEtlJobRecordersDetail record);

    int insertSelective(DepEtlJobRecordersDetail record);

    DepEtlJobRecordersDetail selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(DepEtlJobRecordersDetail record);

    int updateByPrimaryKey(DepEtlJobRecordersDetail record);

    int selectStatusNumber(@Param("packageId") String packageId, @Param("jobStatus") BigDecimal jobStatus);

}