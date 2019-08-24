package com.iflytek.dep.admin.controller;

import com.iflytek.dep.admin.constants.RecvSendStateEnum;
import com.iflytek.dep.admin.model.dto.monitor.DataPackTransStatDto;
import com.iflytek.dep.admin.model.dto.monitor.TrendStatDataDto;
import com.iflytek.dep.admin.model.vo.monitor.CenterPackStatVo;
import com.iflytek.dep.admin.service.MonitorDisplayService;
import com.iflytek.dep.common.utils.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.admin.controller
 * @Description:
 * @date 2019/3/2
 */
@Api(value = "监控大屏-中心平台接口类", tags = {"监控大屏-中心平台接口类"})
@RestController
@RequestMapping("monitorCenter")
//@Auth
public class MonitorCenterController {

    @Value("${logicServerNode.serverNodeId}")
    private String serverNodeId;

    @Autowired
    private MonitorDisplayService monitorDisplayService;

    Logger logger = LoggerFactory.getLogger(MonitorCenterController.class);

    @ApiOperation(value = "数据包传输统计", notes = "数据包传输统计")
    @RequestMapping(value = "dataPackTransStat", method = RequestMethod.POST)
    public ResponseBean dataPackTransStat(@RequestBody DataPackTransStatDto monitorViewDto) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        try {
            monitorViewDto.setServerNodeId(serverNodeId);

            //分平台数据统计
            Future<List<Map<String, Object>>> branchListFuture = executorService.submit(() -> monitorDisplayService.getPackStatResultForAllBranch(monitorViewDto));
            //中心平台数据统计
            Future<List<Map<String, Object>>> centerListFuture = executorService.submit(() -> monitorDisplayService.getPackStatResultForCenter(monitorViewDto));
            //统计传输耗时
            Future<List<Map<String, Object>>> transTimeListFuture = executorService.submit(() -> monitorDisplayService.getTransTimeResult(monitorViewDto));

            //获取结果
            List<Map<String, Object>> branchList = branchListFuture.get();
            List<Map<String, Object>> centerList = centerListFuture.get();
            List<Map<String, Object>> transTimeList = transTimeListFuture.get();

            String num2String = "";
            //计算今日总发送量
            long totalSendCountToday = 0;
            double totalSendSumToday = 0;
            for (Map<String, Object> map : centerList) {
                if ("SEND_FAIL_TODAY".equals((String) map.get("STAT_TYPE")) || "SUCC_TODAY".equals((String) map.get("STAT_TYPE"))) {
                    totalSendCountToday += (new BigDecimal(map.get("STAT_COUNT") + num2String)).longValue();
                    totalSendSumToday += (new BigDecimal(map.get("STAT_SUM") + num2String)).doubleValue();
                }
            }
            Map<String, Object> mapSendTotal = new HashMap<>();
            mapSendTotal.put("STAT_TYPE", "SEND_TODAY");
            mapSendTotal.put("STAT_SUM", totalSendSumToday);
            mapSendTotal.put("STAT_COUNT", totalSendCountToday);
            centerList.add(mapSendTotal);

            //按平台机构分组统计
            Map<String, CenterPackStatVo> packStat = new HashMap<>();
            CenterPackStatVo gCenterPackStatVo = new CenterPackStatVo();
            CenterPackStatVo jCenterPackStatVo = new CenterPackStatVo();
            CenterPackStatVo fCenterPackStatVo = new CenterPackStatVo();
            CenterPackStatVo sCenterPackStatVo = new CenterPackStatVo();
            CenterPackStatVo zCenterPackStatVo = new CenterPackStatVo();
            List<Map<String, Object>> gPackStat = branchList.parallelStream().filter(map -> map.get("ORGTYPE") != null && ((String) map.get("ORGTYPE")).equals("G")).collect(Collectors.toList());
            List<Map<String, Object>> jPackStat = branchList.parallelStream().filter(map -> map.get("ORGTYPE") != null && ((String) map.get("ORGTYPE")).equals("J")).collect(Collectors.toList());
            List<Map<String, Object>> fPackStat = branchList.parallelStream().filter(map -> map.get("ORGTYPE") != null && ((String) map.get("ORGTYPE")).equals("F")).collect(Collectors.toList());
            List<Map<String, Object>> sPackStat = branchList.parallelStream().filter(map -> map.get("ORGTYPE") != null && ((String) map.get("ORGTYPE")).equals("S")).collect(Collectors.toList());
            List<Map<String, Object>> zPackStat = branchList.parallelStream().filter(map -> map.get("ORGTYPE") != null && ((String) map.get("ORGTYPE")).equals("Z")).collect(Collectors.toList());

            gPackStat.forEach(map->{
                setStatData(gCenterPackStatVo, map);
            });
            jPackStat.forEach(map->{
                setStatData(jCenterPackStatVo, map);
            });
            fPackStat.forEach(map->{
                setStatData(fCenterPackStatVo, map);
            });
            sPackStat.forEach(map->{
                setStatData(sCenterPackStatVo, map);
            });
            zPackStat.forEach(map->{
                setStatData(zCenterPackStatVo, map);
            });
            packStat.put(monitorDisplayService.selectByPrimaryKey("G").getOrgTypeMc(), gCenterPackStatVo);
            packStat.put(monitorDisplayService.selectByPrimaryKey("J").getOrgTypeMc(), jCenterPackStatVo);
            packStat.put(monitorDisplayService.selectByPrimaryKey("F").getOrgTypeMc(), fCenterPackStatVo);
            packStat.put(monitorDisplayService.selectByPrimaryKey("S").getOrgTypeMc(), sCenterPackStatVo);
            packStat.put(monitorDisplayService.selectByPrimaryKey("Z").getOrgTypeMc(), zCenterPackStatVo);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("centerList", centerList);
            resultMap.put("transTimeList", transTimeList);
            resultMap.put("packStat", packStat);

            return new ResponseBean(resultMap);
        } catch (Exception e) {
            logger.error("\n查询路由列表失败:", e);
            return new ResponseBean("数据包传输统计查询失败");
        } finally {
            executorService.shutdown();
        }
    }

    private void setStatData(CenterPackStatVo centerPackStatVo, Map<String, Object> map) {
        String num2String = "";
        String sendState = (String) map.get("SENDSTATE");
        long packageCnt = new BigDecimal(map.get("PACKAGECNT") + num2String).longValue();
        double packageSize = new BigDecimal(map.get("PACKAGESIZE") + num2String).doubleValue();

        if (sendState.equals(RecvSendStateEnum.RECVED.getStateCode())) {
            centerPackStatVo.setRecvSuccTotal(packageCnt);
            centerPackStatVo.setRecvSuccSize(packageSize);
        } else if (sendState.equals(RecvSendStateEnum.SENDSUCC.getStateCode())) {
            centerPackStatVo.setSendSuccTotal(packageCnt);
            centerPackStatVo.setSendSuccSize(packageSize);
        } else if (sendState.equals(RecvSendStateEnum.FAIL.getStateCode())) {
            centerPackStatVo.setSendFailTotal(packageCnt);
        }
    }

    @ApiOperation(value = "发送量趋势统计、接收量趋势统计", notes = "发送量趋势统计、接收量趋势统计")
    @RequestMapping(value = "getTrendStatData", method = RequestMethod.POST)
    public ResponseBean getTrendStatData(@RequestBody TrendStatDataDto monitorViewDto) {
        try {
            monitorViewDto.setServerNodeId(serverNodeId);
            Map<String, Object> trendVoList = monitorDisplayService.getTrendStatDataForCenter(monitorViewDto);
            return new ResponseBean(trendVoList);
        } catch (Exception e) {
            logger.error("\n查询失败:", e);
            return new ResponseBean("发送量趋势统计、接收量趋势统计查询失败");
        }
    }

    @ApiOperation(value = "周和日的发送量统计、接收量统计", notes = "周和日的发送量统计、接收量统计")
    @RequestMapping(value = "getTrendStatDataDayOrWeek", method = RequestMethod.POST)
    public ResponseBean getTrendStatDataDayOrWeek(@RequestBody TrendStatDataDto monitorViewDto) {
        try {
            Map<String, Object> dayOrWeekList = monitorDisplayService.getTrendStatDataDayOrWeek(monitorViewDto);
            return new ResponseBean(dayOrWeekList);
        } catch (Exception e) {
            logger.error("\n查询失败:", e);
            return new ResponseBean("发送量趋势统计、接收量趋势统计查询失败");
        }
    }

    @ApiOperation(value = "根据逻辑节点ID查询物理节点列表接口", notes = "根据逻辑节点ID查询物理节点列表接口")
    @PostMapping("/listNodeByServerNodeId")
    public ResponseBean listNodeByServerNodeId() {
        try {
            return new ResponseBean(monitorDisplayService.listNodeByServerNodeId(serverNodeId));
        } catch (Exception e) {
            logger.error("\n查询数据包列表失败:", e);
            return new ResponseBean("根据逻辑节点ID查询物理节点列表失败");
        }
    }

}
