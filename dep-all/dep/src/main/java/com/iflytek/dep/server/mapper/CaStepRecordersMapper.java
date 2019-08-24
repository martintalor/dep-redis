package com.iflytek.dep.server.mapper;

import com.iflytek.dep.server.model.CaStepRecorders;
import com.iflytek.dep.server.model.SectionStepRecorders;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaStepRecordersMapper {
    int deleteByPrimaryKey(String bizSn);

    int insert(CaStepRecorders record);

    int insertSelective(CaStepRecorders record);

    CaStepRecorders selectByPrimaryKey(String bizSn);

    List<CaStepRecorders> getAllRecords();

    List<CaStepRecorders> getNoExecuteRecords();

    int updateByPrimaryKeySelective(CaStepRecorders record);

    int updateByPrimaryKey(CaStepRecorders record);

    List<CaStepRecorders> selectByPackageId(String packageId);
}