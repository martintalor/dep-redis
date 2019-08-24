package com.iflytek.dep.admin.controller;

import com.iflytek.dep.admin.model.LocalServerNode;
import com.iflytek.dep.admin.model.ServerNodeType;
import com.iflytek.dep.admin.model.dto.LocalServerNodeDto;
import com.iflytek.dep.admin.model.dto.MachineNodeDto;
import com.iflytek.dep.admin.model.vo.LocalServerNodeTypeVo;
import com.iflytek.dep.admin.model.vo.LocalServerNodeVo;
import com.iflytek.dep.admin.model.vo.PageVo;
import com.iflytek.dep.admin.service.LocalServerNodeService;
import com.iflytek.dep.admin.utils.EscapeSql;
import com.iflytek.dep.common.annotation.Auth;
import com.iflytek.dep.common.utils.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.controller
 * @Description:逻辑节点管理
 * @date 2019/2/25--15:27
 */
@Api(value = "逻辑节点管理接口类", tags = {"数据交换平台与业务系统接口类"})
@Auth
@RestController
@RequestMapping("localServerNode")
public class LocalServerNodeController {

    Logger logger = LoggerFactory.getLogger(LocalServerNodeController.class);

    @Autowired
    private LocalServerNodeService localServerNodeService;

    @ApiOperation(value = "查询逻辑节点列表", notes = "查询逻辑节点列表")
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public ResponseBean<PageVo<LocalServerNodeVo>> listLocalServerNode(@RequestBody LocalServerNodeDto localServerNodeDto) {
        try {
            if (!StringUtils.isEmpty(localServerNodeDto.getServerNodeId())) {
                localServerNodeDto.setServerNodeId(EscapeSql.escapeSql(localServerNodeDto.getServerNodeId()));
            }
            if (!StringUtils.isEmpty(localServerNodeDto.getServerNodeName())) {
                localServerNodeDto.setServerNodeName(EscapeSql.escapeSql(localServerNodeDto.getServerNodeName()));
            }
            return new ResponseBean(localServerNodeService.listLocalServerNode(localServerNodeDto));
        } catch (Exception e) {
            logger.error("\n查询逻辑节点列表失败:", e);
            return new ResponseBean("查询逻辑节点列表失败");
        }
    }

    @ApiOperation(value = "添加逻辑节点", notes = "添加逻辑节点")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseBean<Integer> addServerNode(@RequestBody LocalServerNode localServerNode) {
        try {
            return new ResponseBean(localServerNodeService.addLocalServerNode(localServerNode));
        } catch (Exception e) {
            logger.error("\n添加逻辑节点失败:", e);
            return new ResponseBean("添加逻辑节点失败");
        }
    }

    @ApiOperation(value = "编辑逻辑节点", notes = "编辑逻辑节点")
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ResponseBean<Integer> updateServerNode(@RequestBody LocalServerNode localServerNode) {
        try {
            return new ResponseBean(localServerNodeService.updateLocalServerNode(localServerNode));
        } catch (Exception e) {
            logger.error("\n编辑逻辑节点失败:", e);
            return new ResponseBean("编辑逻辑节点失败");
        }
    }

    @ApiOperation(value = "查询机构类型列表", notes = "查询机构类型列表")
    @RequestMapping(value = "listOrgType", method = RequestMethod.GET)
    public ResponseBean<List<LocalServerNodeVo>> listOrgType() {
        try {
            return new ResponseBean(localServerNodeService.listOrgType());
        } catch (Exception e) {
            logger.error("\n查询机构类型列表失败:", e);
            return new ResponseBean("查询机构类型列表失败");
        }
    }

    @ApiOperation(value = "查询服务器类型列表", notes = "查询服务器类型列表")
    @RequestMapping(value = "listServerNodeType", method = RequestMethod.GET)
    public ResponseBean<List<ServerNodeType>> listServerNodeType() {
        try {
            return new ResponseBean(localServerNodeService.listServerNodeType());
        } catch (Exception e) {
            logger.error("\n查询服务器类型列表失败:", e);
            return new ResponseBean("查询服务器类型列表失败");
        }
    }

    @ApiOperation(value = "逻辑节点列表", notes = "逻辑节点列表")
    @RequestMapping(value = "listNode", method = RequestMethod.GET)
    public ResponseBean<List<LocalServerNodeTypeVo>> listNode() {
        try {
            return new ResponseBean(localServerNodeService.listNode());
        } catch (Exception e) {
            logger.error("\n获取逻辑节点列表失败:", e);
            return new ResponseBean("获取逻辑节点列表失败");
        }
    }

    @ApiOperation(value = "删除逻辑节点", notes = "删除逻辑节点")
    @PostMapping("/delete")
    public ResponseBean deleteLocalServerNode(@RequestParam String serverNodeId) {
        try {
            return localServerNodeService.deleteLocalServerNode(serverNodeId);
        } catch (Exception e) {
            logger.error("\n删除物理节点失败:", e);
            return new ResponseBean("删除物理节点失败");
        }
    }

    @ApiOperation(value = "查询逻辑节点详情", notes = "查询逻辑节点详情")
    @PostMapping("/detail")
    public ResponseBean getLocalServerNode(@RequestBody MachineNodeDto machineNodeDto) {
        try {
            return new ResponseBean(localServerNodeService.getLocalServerNode(machineNodeDto));
        } catch (Exception e) {
            logger.error("\n查询逻辑节点详情失败:", e);
            return new ResponseBean("查询逻辑节点详情失败");
        }
    }

}