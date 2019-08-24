package com.iflytek.dep.admin.controller;

import com.iflytek.dep.admin.model.dto.DataPackageDto;
import com.iflytek.dep.admin.model.dto.monitor.DataPackTransStatDto;
import com.iflytek.dep.admin.model.dto.monitor.HealthDto;
import com.iflytek.dep.admin.model.dto.monitor.RightChartDto;
import com.iflytek.dep.admin.model.dto.monitor.TrendStatDataDto;
import com.iflytek.dep.admin.model.vo.PackageVo;
import com.iflytek.dep.admin.model.vo.PageVo;
import com.iflytek.dep.admin.model.vo.monitor.MonitorDisplayFrontHealthVo;
import com.iflytek.dep.admin.model.vo.monitor.MonitorDisplayPackStatVo;
import com.iflytek.dep.admin.model.vo.monitor.MonitorDisplayTrendVo;
import com.iflytek.dep.admin.service.DataPackageService;
import com.iflytek.dep.admin.service.MonitorDisplayService;
import com.iflytek.dep.admin.utils.EscapeSql;
import com.iflytek.dep.common.annotation.Auth;
import com.iflytek.dep.common.utils.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.admin.controller
 * @Description:
 * @date 2019/2/28
 */
@Api(value = "监控大屏-分平台接口类", tags = {"监控大屏-分平台接口类"})
@RestController
@RequestMapping("monitorBranch")
@Auth
public class MonitorBranchController {

    @Value("${logicServerNode.serverNodeId}")
    private String serverNodeId;

    @Autowired
    private MonitorDisplayService monitorDisplayService;

    @Autowired
    private DataPackageService dataPackageService;

    Logger logger = LoggerFactory.getLogger(MonitorBranchController.class);

    @ApiOperation(value = "数据包传输统计", notes = "数据包传输统计")
    @RequestMapping(value = "dataPackTransStat", method = RequestMethod.POST)
    public ResponseBean dataPackTransStat(@RequestBody DataPackTransStatDto monitorViewDto) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        try {
            monitorViewDto.setServerNodeId(serverNodeId);

            Future<MonitorDisplayPackStatVo> packStatFuture = executorService.submit(() -> monitorDisplayService.getPackStatResult(monitorViewDto));
            Future<List<Map<String, Object>>> transTimeFuture = executorService.submit(() -> monitorDisplayService.getTransTimeResult(monitorViewDto));

            MonitorDisplayPackStatVo packStatResult = packStatFuture.get();
            List<Map<String, Object>> transTimeResult = transTimeFuture.get();
            packStatResult.setTransTimePercent(transTimeResult);

            return new ResponseBean(packStatResult);
        } catch (Exception e) {
            logger.error("\n查询数据包传输统计失败:", e);
            return new ResponseBean("查询数据包传输统计失败");
        } finally {
            executorService.shutdown();
        }
    }

    @ApiOperation(value = "发送量趋势统计、接收量趋势统计", notes = "发送量趋势统计、接收量趋势统计")
    @RequestMapping(value = "getTrendStatData", method = RequestMethod.POST)
    public ResponseBean getTrendStatData(@RequestBody TrendStatDataDto monitorViewDto) {
        try {
            monitorViewDto.setServerNodeId(serverNodeId);
            Map<String, List<MonitorDisplayTrendVo>> trendVoList = monitorDisplayService.getTrendStatData(monitorViewDto);
            return new ResponseBean(trendVoList);
        } catch (Exception e) {
            logger.error("\n查询发送量趋势统计、接收量趋势统计失败:", e);
            return new ResponseBean("查询发送量趋势统计、接收量趋势统计失败");
        }
    }

    @ApiOperation(value = "前置机健康状况", notes = "前置机健康状况")
    @RequestMapping(value = "frontEndHealthList", method = RequestMethod.POST)
    public ResponseBean frontEndHealthList(@RequestBody HealthDto monitorViewDto) {
        try {
            monitorViewDto.setPageSize(10000);
            monitorViewDto.setServerNodeId(serverNodeId);
            PageVo<MonitorDisplayFrontHealthVo> pageVo = monitorDisplayService.getFrontEndHealthList(monitorViewDto);
            return new ResponseBean(pageVo.getList());
        } catch (Exception e) {
            logger.error("\n查询前置机健康状况失败:", e);
            return new ResponseBean("查询前置机健康状况失败");
        }
    }

    @ApiOperation(value = "传输流量监测、服务健康状况", notes = "传输流量监测、服务健康状况")
    @RequestMapping(value = "rightChart", method = RequestMethod.POST)
    public ResponseBean rightChart(@RequestBody RightChartDto monitorViewDto) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        try {
            monitorViewDto.setServerNodeId(serverNodeId);

            Future<List<MonitorDisplayTrendVo>> futureTask1 = executorService.submit(() -> monitorDisplayService.getTransFlowList(monitorViewDto));
            Future<Double> futureTask2 = executorService.submit(() -> monitorDisplayService.getServerNormalPercent(monitorViewDto));

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("transFlowList", futureTask1.get());
            resultMap.put("serverNormalPercent", futureTask2.get().intValue()==100?100:0);

            return new ResponseBean(resultMap);
        } catch (Exception e) {
            logger.error("\n查询传输流量监测、服务健康状况失败:", e);
            return new ResponseBean("查询传输流量监测、服务健康状况失败");
        } finally {
            executorService.shutdown();
        }
    }

    @ApiOperation(value = "数据包列表接口", notes = "数据包列表接口")
    @PostMapping("/listDataPackage")
    public ResponseBean listDataPackage(@RequestBody DataPackageDto dataPackageDto) {
        try {
            if (StringUtils.isEmpty(dataPackageDto.getPackageId())) {
                dataPackageDto.setPackageId(null);
            } /*else {
                dataPackageDto.setPackageId(EscapeSql.escapeSql(dataPackageDto.getPackageId()));
            }*/
            return new ResponseBean(monitorDisplayService.listDataPackageForCenter(dataPackageDto));
        } catch (Exception e) {
            logger.error("\n查询数据包列表失败:", e);
            return new ResponseBean("查询数据包列表失败");
        }
    }

    @ApiOperation(value = "查看数据子包列表", notes = "查看数据子包列表")
    @PostMapping("/listPackageSub")
    public ResponseBean listPackageSub(@RequestParam String packageId) {
        try {
            return new ResponseBean(dataPackageService.listPackageSub(packageId));
        } catch (Exception e) {
            logger.error("\n查看数据子包列表失败:", e);
            return new ResponseBean("查看数据子包列表失败");
        }
    }

    @ApiOperation(value = "查看数据包链路信息", notes = "查看数据包链路信息")
    @PostMapping("/linkMessage")
    public ResponseBean linkMessage(@RequestBody PackageVo packageVo) {
        try {
            return new ResponseBean(dataPackageService.getNodeLink(packageVo));
        } catch (Exception e) {
            logger.error("\n查看数据包链路信息失败:", e);
            return new ResponseBean("查看数据包链路信息失败");
        }
    }


}
