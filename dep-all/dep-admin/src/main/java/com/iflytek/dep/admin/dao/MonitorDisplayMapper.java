package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.dto.DataPackageDto;
import com.iflytek.dep.admin.model.dto.monitor.DataPackTransStatDto;
import com.iflytek.dep.admin.model.dto.monitor.HealthDto;
import com.iflytek.dep.admin.model.dto.monitor.RightChartDto;
import com.iflytek.dep.admin.model.dto.monitor.TrendStatDataDto;
import com.iflytek.dep.admin.model.vo.DataPackageVo;
import com.iflytek.dep.admin.model.vo.monitor.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MonitorDisplayMapper {

    /**
     * 查询数据包的收发数量统计
     * @param monitorViewDto
     */
    List<Map<String,Object>> getPackStatResult(DataPackTransStatDto monitorViewDto);

    /**
     * 查询数据包的耗时统计
     * @param monitorViewDto
     */
    List<Map<String,Object>> getTransTimeResult(DataPackTransStatDto monitorViewDto);

    List<MonitorDisplayFrontHealthVo> getFrontEndHealthList(HealthDto healthDto);

    List<MonitorDisplayTrendVo> getTrendStatData(TrendStatDataDto monitorViewDto);

    List<MonitorDisplayTrendVo> getTransFlowList(RightChartDto monitorViewDto);

    List<Map<String, Object>> getPackStatResultForAllBranch(DataPackTransStatDto monitorViewDto);

    List<Map<String, Object>> getRecvTopForCenter(TrendStatDataDto monitorViewDto);

    List<Map<String, Object>> getSendTopForCenter(TrendStatDataDto monitorViewDto);

    List<Map<String,Object>> getPackStatResultForCenter(DataPackTransStatDto monitorViewDto);

    List<MonitorDisplayPackStateVo> packStateList(DataPackageDto dataPackageDto);

    List<MonitorDisplayProbeResultVo> getDepServerProbeResult(RightChartDto monitorViewDto);

    List<MonitorDisplayProbeResultVo> getFtpServerProbeResult(RightChartDto monitorViewDto);

    List<MonitorDisplayTrendVo> getTrendStatDataForSendFail(TrendStatDataDto monitorViewDto);

    List<DataPackageVo> selectAllForMonitorCenter(@Param("dataPackageDto") DataPackageDto dataPackageDto);

    MonitorDisplayFrontHealthVo getFrontEndHealthStatByNodeId(@Param("nodeId") String nodeId);

    List<MonitorDisplayTrendDayOrWeekVo> getTrendStatDataDayOrWeek(TrendStatDataDto monitorViewDto);

    List<MonitorDisplayTrendDayOrWeekVo> getTrendStatDataDayOrWeekCenter(TrendStatDataDto monitorViewDto);
}