package com.iflytek.dep.admin.controller;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.github.pagehelper.PageHelper;
import com.iflytek.dep.admin.model.EtlJobRecorders;
import com.iflytek.dep.admin.model.JobType;
import com.iflytek.dep.admin.model.dto.monitor.MonitorEtlDto;
import com.iflytek.dep.admin.model.vo.PageVo;
import com.iflytek.dep.admin.model.vo.monitor.MonitorEtlVo;
import com.iflytek.dep.admin.service.MonitorEtlService;
import com.iflytek.dep.admin.utils.ExcelConstant;
import com.iflytek.dep.admin.utils.TimeUtil;
import com.iflytek.dep.common.utils.ResponseBean;
import com.iflytek.dep.common.utils.UUIDGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.controller
 * @Description: etl统计模块
 * @date 2019/7/22--13:48
 */
@Api(value = "监控大屏-etl统计", tags = {"监控大屏-etl统计接口类"})
@RestController
@RequestMapping("monitorEtl")
public class MonitorEtlController {

    Logger logger = LoggerFactory.getLogger(MonitorEtlController.class);

    @Autowired
    private MonitorEtlService MonitorEtlService;

    @ApiOperation(value = "job类型列表", notes = "job类型列表")
    @RequestMapping(value = "listJobType", method = RequestMethod.POST)
    public ResponseBean<List<JobType>> listJobType() {
        try {
            return new ResponseBean<>(MonitorEtlService.listJobType());
        } catch (Exception e) {
            logger.error("\n获取job类型列表失败:", e);
            return new ResponseBean("获取job类型列表失败");
        }
    }

    @ApiOperation(value = "job监控列表", notes = "job监控列表")
    @RequestMapping(value = "listMonitorEtl", method = RequestMethod.POST)
    public ResponseBean listMonitorEtl(@RequestBody MonitorEtlDto monitorEtlDto) {
        try {
            double totalPackageSize = 0, totalSpendTime = 0, totalRate = 0;
            PageVo page = MonitorEtlService.listMonitorEtl(monitorEtlDto);
            List<MonitorEtlVo> MonitorEtlVos = page.getList();
            if (!CollectionUtils.isEmpty(MonitorEtlVos)) {
                for (MonitorEtlVo vo : MonitorEtlVos) {
                    totalPackageSize = totalPackageSize + vo.getPackageSize();
                    totalSpendTime = totalSpendTime + vo.getSpendTime();
                }
                totalRate = totalPackageSize / totalSpendTime;
            }
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("page", page);
            resultMap.put("totalPackageSize", totalPackageSize);
            resultMap.put("totalSpendTime", totalSpendTime);
            resultMap.put("totalRate", totalRate);
            return new ResponseBean(resultMap);
        } catch (Exception e) {
            logger.error("\n获取job监控列表失败:", e);
            return new ResponseBean("获取job监控列表失败");
        }
    }

    @ApiOperation(value = "导出监控列表", notes = "导出监控列表")
    @RequestMapping(value = "downExcel", method = RequestMethod.GET)
    public ResponseBean downExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String scheduled = request.getParameter("scheduled");
            String jobId = request.getParameter("jobId");
            MonitorEtlDto dto = new MonitorEtlDto();
            dto.setStartTime(startTime == null ? null : TimeUtil.StringToDate(startTime));
            dto.setEndTime(endTime == null ? null : TimeUtil.StringToDate(endTime));
            dto.setScheduled(scheduled);
            dto.setJobId(jobId);
            ServletOutputStream out = null;
            try {
                // 查询总数并封装相关变量(这块直接拷贝就行了不要改)
                Integer totalRowCount = MonitorEtlService.selectCountEtlRecord(dto);
                if (totalRowCount == 0) {
                    return new ResponseBean("监控列表数据为空");
                }

                out = response.getOutputStream();
                ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
                // 设置EXCEL名称
                String fileName = new String((UUIDGenerator.createUUID()).getBytes(), "UTF-8");

                // 设置SHEET名称
                String sheetName = "job监控表";
                // 设置标题
                Table table = new Table(1);
                List<List<String>> titles = new ArrayList<List<String>>();
                titles.add(Arrays.asList("id"));
                titles.add(Arrays.asList("jobId"));
                titles.add(Arrays.asList("jobName"));
                titles.add(Arrays.asList("jobType"));
                titles.add(Arrays.asList("startTime"));
                titles.add(Arrays.asList("endTime"));
                titles.add(Arrays.asList("jobStatus"));
                titles.add(Arrays.asList("preJobKey"));
                titles.add(Arrays.asList("currJobKey"));
                titles.add(Arrays.asList("isEmptyElement"));
                titles.add(Arrays.asList("packageId"));
                titles.add(Arrays.asList("jobParam"));
                titles.add(Arrays.asList("jobResultInfo"));
                titles.add(Arrays.asList("destEndTime"));
                titles.add(Arrays.asList("destCallbackInfo"));
                table.setHead(titles);

                Integer perSheetRowCount = ExcelConstant.PER_SHEET_ROW_COUNT;
                Integer pageSize = ExcelConstant.PER_WRITE_ROW_COUNT;
                Integer sheetCount = totalRowCount % perSheetRowCount == 0 ? (totalRowCount / perSheetRowCount) : (totalRowCount / perSheetRowCount + 1);
                Integer previousSheetWriteCount = perSheetRowCount / pageSize;
                Integer lastSheetWriteCount = totalRowCount % perSheetRowCount == 0 ?
                        previousSheetWriteCount :
                        (totalRowCount % perSheetRowCount % pageSize == 0 ? totalRowCount % perSheetRowCount / pageSize : (totalRowCount % perSheetRowCount / pageSize + 1));


                for (int i = 0; i < sheetCount; i++) {
                    // 创建SHEET
                    Sheet sheet = new Sheet(i, 0);
                    sheet.setSheetName(sheetName + i);

                    // 写数据 这个j的最大值判断直接拷贝就行了，不要改动
                    for (int j = 0; j < (i != sheetCount - 1 ? previousSheetWriteCount : lastSheetWriteCount); j++) {
                        List<List<String>> dataList = new ArrayList<>();
                        // 此处查询并封装数据即可 currentPage, pageSize这俩个变量封装好的 不要改动
                        PageHelper.startPage(j + 1 + previousSheetWriteCount * i, pageSize);
                        List<EtlJobRecorders> etlVOList = this.MonitorEtlService.selectEtlRecord(dto);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if (!CollectionUtils.isEmpty(etlVOList)) {
                            etlVOList.forEach(eachRecorderVO -> {
                                dataList.add(Arrays.asList(
                                        eachRecorderVO.getId(),
                                        eachRecorderVO.getJobId(),
                                        eachRecorderVO.getJobName(),
                                        eachRecorderVO.getJobType(),
                                        formatter.format(eachRecorderVO.getStartTime()),
                                        formatter.format(eachRecorderVO.getEndTime()),
                                        eachRecorderVO.getJobStatus(),
                                        eachRecorderVO.getPreJobKey(),
                                        eachRecorderVO.getCurrJobKey(),
                                        eachRecorderVO.getIsEmptyElement(),
                                        eachRecorderVO.getPackageId(),
                                        eachRecorderVO.getJobParam(),
                                        eachRecorderVO.getJobResultInfo(),
                                        eachRecorderVO.getDestEndTime() == null ? "" : formatter.format(eachRecorderVO.getDestEndTime()),
                                        eachRecorderVO.getDestCallbackInfo()
                                ));
                            });
                        }
                        writer.write0(dataList, sheet, table);
                    }
                }
                // 下载EXCEL
                response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
                response.setContentType("multipart/form-data");
                response.setCharacterEncoding("utf-8");
                writer.finish();
                out.flush();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return new ResponseBean<>();
        } catch (Exception e) {
            logger.error("\n导出监控列表失败:", e);
            return new ResponseBean("导出监控列表失败");
        }
    }
}