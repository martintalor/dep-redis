package com.iflytek.dep.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iflytek.dep.admin.dao.DicDepEtlJobMapper;
import com.iflytek.dep.admin.model.EtlJobRecorders;
import com.iflytek.dep.admin.model.JobType;
import com.iflytek.dep.admin.model.dto.monitor.MonitorEtlDto;
import com.iflytek.dep.admin.model.vo.PageVo;
import com.iflytek.dep.admin.model.vo.monitor.MonitorEtlVo;
import com.iflytek.dep.admin.service.MonitorEtlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.service.impl
 * @Description:
 * @date 2019/7/22--14:01
 */
@Service
public class MonitorEtlServiceImpl implements MonitorEtlService {

    public static String CRON_JOB = "1";
    public static String NOT_CRON_JOB = "0";

    @Autowired
    DicDepEtlJobMapper dicDepEtlJobMapper;

    @Override
    public List<JobType> listJobType() {
        List<JobType> list = new ArrayList<JobType>();
        list.add(new JobType(CRON_JOB, "定时", dicDepEtlJobMapper.selectByJobType(CRON_JOB)));
        list.add(new JobType(NOT_CRON_JOB, "非定时", dicDepEtlJobMapper.selectByJobType(NOT_CRON_JOB)));
        return list;
    }

    @Override
    public PageVo listMonitorEtl(MonitorEtlDto monitorEtlDto) {
        PageHelper.startPage(monitorEtlDto.getCurrentPageNo(), monitorEtlDto.getPageSize());
        List<MonitorEtlVo> MonitorEtlVos = dicDepEtlJobMapper.listMonitorEtl(monitorEtlDto);
        PageInfo<MonitorEtlVo> pageInfo = new PageInfo<>(MonitorEtlVos);
        return new PageVo(monitorEtlDto.getCurrentPageNo(), monitorEtlDto.getPageSize(), (int) pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public Integer selectCountEtlRecord(MonitorEtlDto dto) {
        return dicDepEtlJobMapper.selectCountEtlRecord(dto);
    }

    @Override
    public List<EtlJobRecorders> selectEtlRecord(MonitorEtlDto dto) {
        return dicDepEtlJobMapper.selectEtlRecord(dto);
    }

}