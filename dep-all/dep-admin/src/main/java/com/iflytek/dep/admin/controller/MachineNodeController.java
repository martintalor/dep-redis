package com.iflytek.dep.admin.controller;

import com.iflytek.dep.admin.model.LocalServerNode;
import com.iflytek.dep.admin.model.dto.MachineNodeDetailDto;
import com.iflytek.dep.admin.model.dto.MachineNodeDto;
import com.iflytek.dep.admin.model.vo.MachineNodeCountVo;
import com.iflytek.dep.admin.model.vo.MachineNodeTypeVo;
import com.iflytek.dep.admin.model.vo.MachineNodeVo;
import com.iflytek.dep.admin.model.vo.PageVo;
import com.iflytek.dep.admin.service.LocalServerNodeService;
import com.iflytek.dep.admin.service.MachineNodeService;
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

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.controller
 * @Description:
 * @date 2019/2/23--17:27
 */
@Api(value = "物理节点管理接口", tags = {"数据交换平台与业务系统接口类"})
@Auth
@RestController()
@RequestMapping("machineNode")
public class MachineNodeController {


    Logger logger = LoggerFactory.getLogger(MachineNodeController.class);

    @Value("${logicServerNode.serverNodeId}")
    private String serverNodeId;

    @Autowired
    private MachineNodeService machineNodeService;

    @Autowired
    private LocalServerNodeService localServerNodeService;

    @ApiOperation(value = "物理节点列表接口", notes = "物理节点列表接口")
    @PostMapping("/list")
    public ResponseBean<PageVo<MachineNodeVo>> listMachineNode(@RequestBody MachineNodeDto machineNodeDto) {
        try {
            if (!StringUtils.isEmpty(machineNodeDto.getNodeRemark())) {
                machineNodeDto.setNodeRemark(EscapeSql.escapeSql(machineNodeDto.getNodeRemark()));
            }

            machineNodeDto.setServerNodeId(serverNodeId);
            if (!StringUtils.isEmpty(serverNodeId)) {
                LocalServerNode localServerNode = localServerNodeService.selectByServerNodeId(serverNodeId);
                if (localServerNode != null) {
                    machineNodeDto.setServerNodeTypeDm(localServerNode.getServerNodeTypeDm());//分平台只能看到本逻辑节点下的物理节点
                }
            }

            return new ResponseBean(machineNodeService.selectAll(machineNodeDto));
        } catch (Exception e) {
            logger.error("\n查询物理节点列表失败:", e);
            return new ResponseBean("查询物理节点列表失败");
        }
    }

    @ApiOperation(value = "添加物理节点接口", notes = "添加物理节点接口")
    @PostMapping("/add")
    public ResponseBean addMachineNode(@RequestBody MachineNodeDetailDto machineNodeDetailDto) {
        try {
            machineNodeService.addMachineNode(machineNodeDetailDto);
            return new ResponseBean();
        } catch (Exception e) {
            logger.error("\n添加物理节点失败:", e);
            return new ResponseBean("添加物理节点失败");
        }
    }

    @ApiOperation(value = "查看物理节点详情", notes = "查看物理节点详情")
    @PostMapping("/detail")
    public ResponseBean getMachineNode(@RequestParam String nodeId) {
        try {
            return new ResponseBean(machineNodeService.getMachineNode(nodeId));
        } catch (Exception e) {
            logger.error("\n查看物理节点详情失败:", e);
            return new ResponseBean("查看物理节点详情失败");
        }
    }


    @ApiOperation(value = "编辑物理节点", notes = "编辑物理节点")
    @PostMapping("/update")
    public ResponseBean updateMachineNode(@RequestBody MachineNodeDetailDto machineNodeDetailDto) {
        try {
            machineNodeService.updateMachineNode(machineNodeDetailDto);
            return new ResponseBean();
        } catch (Exception e) {
            logger.error("\n编辑物理节点失败:", e);
            return new ResponseBean("编辑物理节点失败");
        }
    }


    @ApiOperation(value = "物理节点启用", notes = "物理节点启用")
    @PostMapping("/open")
    public ResponseBean openMachineNode(@RequestParam String nodeId) {
        try {
            machineNodeService.openMachineNode(nodeId);
            return new ResponseBean();
        } catch (Exception e) {
            logger.error("\n物理节点启用失败:", e);
            return new ResponseBean("物理节点启用失败");
        }
    }

    @ApiOperation(value = "物理节点停用", notes = "物理节点停用")
    @PostMapping("/close")
    public ResponseBean closeMachineNode(@RequestParam String nodeId) {
        try {
            machineNodeService.closeMachineNode(nodeId);
            return new ResponseBean();
        } catch (Exception e) {
            logger.error("\n物理节点停用失败:", e);
            return new ResponseBean("物理节点停用失败");
        }
    }

    @ApiOperation(value = "物理节点列表", notes = "物理节点列表")
    @RequestMapping(value = "listNode", method = RequestMethod.GET)
    public ResponseBean<List<MachineNodeTypeVo>> listNode(@RequestParam(required = false) String type) {
        try {
            return new ResponseBean(machineNodeService.listNode(type));
        } catch (Exception e) {
            logger.error("\n获取物理节点列表失败:", e);
            return new ResponseBean("获取物理节点列表失败");
        }
    }

    @ApiOperation(value = "物理节点统计信息", notes = "物理节点统计信息")
    @RequestMapping(value = "countNode", method = RequestMethod.GET)
    public ResponseBean<List<MachineNodeCountVo>> countNode() {
        try {
            return new ResponseBean(machineNodeService.countNode());
        } catch (Exception e) {
            logger.error("\n查看物理节点统计信息失败:", e);
            return new ResponseBean("查看物理节点统计信息失败");
        }
    }

    @ApiOperation(value = "删除物理节点", notes = "删除物理节点")
    @PostMapping("/delete")
    public ResponseBean deleteMachineNode(@RequestParam String nodeId) {
        try {
            return machineNodeService.deleteMachineNode(nodeId);
        } catch (Exception e) {
            logger.error("\n删除物理节点失败:", e);
            return new ResponseBean("删除物理节点失败");
        }
    }
}