package com.iflytek.dep.admin.controller;

import com.iflytek.dep.admin.constants.GlobalSendStateEnum;
import com.iflytek.dep.admin.model.dto.DataPackageDto;
import com.iflytek.dep.admin.model.dto.PackageIdAndToNodeDto;
import com.iflytek.dep.admin.model.vo.DataPackageVo;
import com.iflytek.dep.admin.model.vo.PackageVo;
import com.iflytek.dep.admin.model.vo.StateVo;
import com.iflytek.dep.admin.service.DataPackageService;
import com.iflytek.dep.common.utils.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.controller
 * @Description: 数据包管理
 * @date 2019/2/28--19:49
 */
@Api(value = "数据包管理接口类", tags = {"数据交换平台与业务系统接口类"})
//@Auth
@RestController
@RequestMapping("dataPackage")
public class DataPackageController {

    Logger logger = LoggerFactory.getLogger(DataPackageController.class);

    @Autowired
    private DataPackageService dataPackageService;

    @ApiOperation(value = "数据包列表接口", notes = "数据包列表接口")
    @PostMapping("/list")
    public ResponseBean<List<DataPackageVo>> listDataPackage(@RequestBody DataPackageDto dataPackageDto) {
        try {
            return new ResponseBean(dataPackageService.listDataPackage(dataPackageDto));
        } catch (Exception e) {
            logger.error("\n查询数据包列表失败:", e);
            return new ResponseBean("查询数据包列表失败");
        }
    }

    @ApiOperation(value = "查看数据包详情", notes = "查看数据包详情")
    @PostMapping("/detail")
    public ResponseBean getDataPackage(@RequestParam String packageId, @RequestParam String toNodeId) {
        try {
            return new ResponseBean(dataPackageService.getDataPackage(packageId, toNodeId));
        } catch (Exception e) {
            logger.error("\n查看数据包详情失败:", e);
            return new ResponseBean("查看数据包详情失败");
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
    public ResponseBean getNodeLink(@RequestBody PackageVo packageVo) {
        try {
            return new ResponseBean(dataPackageService.getNodeLink(packageVo));
        } catch (Exception e) {
            logger.error("\n查看数据包链路信息失败:", e);
            return new ResponseBean("查看数据包链路信息失败");
        }
    }

    @ApiOperation(value = "查看数据包链路详情信息", notes = "查看数据包链路详情信息")
    @PostMapping("/linkMessageDetail")
    public ResponseBean getNodeLinkDetail(@RequestBody PackageVo packageVo) {
        try {
            return new ResponseBean(dataPackageService.getNodeLinkDetail(packageVo));
        } catch (Exception e) {
            logger.error("\n查看数据包链路信息失败:", e);
            return new ResponseBean("查看数据包链路详情信息失败");
        }
    }

    @ApiOperation(value = "数据包状态列表", notes = "数据包状态列表")
    @GetMapping("/states")
    public ResponseBean getStates() {
        List states = new ArrayList();
        try {
            for (GlobalSendStateEnum dto : GlobalSendStateEnum.values()) {
                states.add(new StateVo(dto.getStateCode(),dto.getStateName()));
            }
            return new ResponseBean(states);
        } catch (Exception e) {
            logger.error("\n查看数据包状态列表失败:", e);
            return new ResponseBean("查看数据包状态列表失败");
        }
    }

    @ApiOperation(value = "数据包交换重试", notes = "数据包交换重试")
    @PostMapping("/reTry")
    public ResponseBean reTryPackage(@RequestBody PackageIdAndToNodeDto dto) {
        try {
            if (StringUtils.isEmpty(dto.getPackageIdAndToNodeId())) {
                return new ResponseBean("必填参数不能为空");
            }
            String[] ids = dto.getPackageIdAndToNodeId().split(";");
            for (String id : ids) {
                String[] packageIdAndToNodeIds = id.split("#");
                ResponseBean result = dataPackageService.reTryPackage(packageIdAndToNodeIds[0], packageIdAndToNodeIds[1]);
                // 未获取到重试服务直接返回
                if (!StringUtils.isEmpty(result.getMessage()) && result.getMessage().equals("未获取到服务")) {
                    return result;
                }
            }
            return new ResponseBean();
        } catch (Exception e) {
            logger.error("\n数据包交换重试失败:", e);
            return new ResponseBean("数据包交换重试失败");
        }
    }

    @ApiOperation(value = "数据包接收状态人工确认", notes = "数据包接收状态人工确认")
    @PostMapping("/confirmState")
    public ResponseBean confirmState(@RequestParam String packageId, @RequestParam String toNodeId) {
        try {
            dataPackageService.confirmState(packageId, toNodeId);
            return new ResponseBean();
        } catch (Exception e) {
            logger.error("\n数据包接收状态人工确认失败:", e);
            return new ResponseBean("数据包接收状态人工确认失败");
        }
    }


}