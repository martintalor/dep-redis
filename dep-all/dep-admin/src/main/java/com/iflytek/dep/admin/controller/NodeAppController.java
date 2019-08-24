package com.iflytek.dep.admin.controller;

import com.iflytek.dep.admin.model.NodeApp;
import com.iflytek.dep.admin.model.dto.NodeAppDto;
import com.iflytek.dep.admin.model.vo.NodeAppVo;
import com.iflytek.dep.admin.model.vo.PageVo;
import com.iflytek.dep.admin.service.NodeAppNodeService;
import com.iflytek.dep.admin.utils.EscapeSql;
import com.iflytek.dep.common.utils.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.controller
 * @Description:
 * @date 2019/2/26--21:00
 */
@Api(value = "应用管理接口类", tags = {"数据交换平台与业务系统接口类"})
//@Auth
@RestController
@RequestMapping("nodeApp")
public class NodeAppController {

    @Autowired
    private NodeAppNodeService nodeAppNodeService;

    Logger logger = LoggerFactory.getLogger(NodeAppController.class);

    @ApiOperation(value = "查询应用列表", notes = "查询应用列表")
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public ResponseBean<PageVo<NodeAppVo>> listNodeApp(@RequestBody NodeAppDto nodeAppDto) {
        try {
            if (!StringUtils.isEmpty(nodeAppDto.getAppId())) {
                nodeAppDto.setAppId(EscapeSql.escapeSql(nodeAppDto.getAppId()));
            }
            if (!StringUtils.isEmpty(nodeAppDto.getAppName())) {
                nodeAppDto.setAppName(EscapeSql.escapeSql(nodeAppDto.getAppName()));
            }
            return new ResponseBean(nodeAppNodeService.listNodeApp(nodeAppDto));
        } catch (Exception e) {
            logger.error("\n查询应用列表失败:", e);
            return new ResponseBean("查询应用列表失败");
        }
    }

    @ApiOperation(value = "添加应用", notes = "添加应用")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseBean<Integer> addNodeApp(@RequestBody NodeApp nodeApp) {
        try {
            return new ResponseBean(nodeAppNodeService.addNodeApp(nodeApp));
        } catch (Exception e) {
            logger.error("\n添加应用失败:", e);
            return new ResponseBean("添加应用失败");
        }
    }

    @ApiOperation(value = "编辑应用", notes = "编辑应用")
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ResponseBean<Integer> updateNodeApp(@RequestBody NodeApp nodeApp) {
        try {
            return new ResponseBean(nodeAppNodeService.updateNodeApp(nodeApp));
        } catch (Exception e) {
            logger.error("\n编辑应用失败:", e);
            return new ResponseBean("编辑应用失败");
        }
    }

    @ApiOperation(value = "删除应用", notes = "删除应用")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public ResponseBean<Integer> deleteNodeApp(@RequestParam String appId) {
        try {
            return new ResponseBean(nodeAppNodeService.deleteNodeApp(appId));
        } catch (Exception e) {
            logger.error("\n删除应用失败:", e);
            return new ResponseBean("删除应用失败");
        }
    }
}