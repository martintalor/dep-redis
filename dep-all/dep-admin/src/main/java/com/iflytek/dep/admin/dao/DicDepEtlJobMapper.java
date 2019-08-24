package com.iflytek.dep.admin.dao;


import com.iflytek.dep.admin.model.EtlJobRecorders;
import com.iflytek.dep.admin.model.JobType;
import com.iflytek.dep.admin.model.dto.monitor.MonitorEtlDto;
import com.iflytek.dep.admin.model.vo.monitor.MonitorEtlVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DicDepEtlJobMapper {

    List<JobType> selectByJobType(@Param("jobType") String jobType);

    List<MonitorEtlVo> listMonitorEtl(@Param("monitorEtlDto") MonitorEtlDto monitorEtlDto);

    Integer selectCountEtlRecord(@Param("monitorEtlDto") MonitorEtlDto monitorEtlDto);

    List<EtlJobRecorders> selectEtlRecord(@Param("monitorEtlDto") MonitorEtlDto monitorEtlDto);
}