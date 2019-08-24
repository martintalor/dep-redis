package com.iflytek.dep.admin.service;

import com.iflytek.dep.admin.model.MachineNode;
import com.iflytek.dep.admin.model.OrgType;
import com.iflytek.dep.admin.model.dto.DataPackageDto;
import com.iflytek.dep.admin.model.dto.monitor.DataPackTransStatDto;
import com.iflytek.dep.admin.model.dto.monitor.HealthDto;
import com.iflytek.dep.admin.model.dto.monitor.RightChartDto;
import com.iflytek.dep.admin.model.dto.monitor.TrendStatDataDto;
import com.iflytek.dep.admin.model.vo.DataPackageVo;
import com.iflytek.dep.admin.model.vo.PageVo;
import com.iflytek.dep.admin.model.vo.monitor.MonitorDisplayFrontHealthVo;
import com.iflytek.dep.admin.model.vo.monitor.MonitorDisplayPackStatVo;
import com.iflytek.dep.admin.model.vo.monitor.MonitorDisplayPackStateVo;
import com.iflytek.dep.admin.model.vo.monitor.MonitorDisplayTrendVo;

import java.util.List;
import java.util.Map;

/**
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.admin.service
 * @Description:
 * @date 2019/2/28
 */
public interface MonitorDisplayService {

    MonitorDisplayPackStatVo getPackStatResult(DataPackTransStatDto monitorViewDto);

    PageVo<MonitorDisplayFrontHealthVo> getFrontEndHealthList(HealthDto monitorViewDto);

    Map<String, List<MonitorDisplayTrendVo>> getTrendStatData(TrendStatDataDto monitorViewDto);

    List<MonitorDisplayTrendVo> getTransFlowList(RightChartDto monitorViewDto);

    Double getServerNormalPercent(RightChartDto monitorViewDto);

    List<Map<String, Object>> getPackStatResultForAllBranch(DataPackTransStatDto monitorViewDto);

    Map<String, Object> getTrendStatDataForCenter(TrendStatDataDto monitorViewDto);

    Map<String, Object> getTrendStatDataDayOrWeek(TrendStatDataDto monitorViewDto);

    List<Map<String,Object>> getPackStatResultForCenter(DataPackTransStatDto monitorViewDto);

    List<Map<String,Object>> getTransTimeResult(DataPackTransStatDto monitorViewDto);

//    PageVo<DataPackageVo> listDataPackage(DataPackageDto dataPackageDto);

    List<MonitorDisplayPackStateVo> packStateList(DataPackageDto dataPackageDto);

    PageVo<DataPackageVo> listDataPackageForCenter(DataPackageDto dataPackageDto);

    List<MachineNode> listNodeByServerNodeId(String dataPackageDto);

    OrgType selectByPrimaryKey(String orgTypeDm);
}
