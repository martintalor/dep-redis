package com.iflytek.dep.admin.service;

import com.iflytek.dep.admin.model.EtlJobRecorders;
import com.iflytek.dep.admin.model.JobType;
import com.iflytek.dep.admin.model.dto.monitor.MonitorEtlDto;
import com.iflytek.dep.admin.model.vo.PageVo;

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service
 * @Description:
 * @date 2019/7/22--14:00
 */
public interface MonitorEtlService {

    List<JobType> listJobType();

    PageVo listMonitorEtl(MonitorEtlDto monitorEtlDto);

    Integer selectCountEtlRecord(MonitorEtlDto dto);

    List<EtlJobRecorders> selectEtlRecord(MonitorEtlDto dto);
}
