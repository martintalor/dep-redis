package com.iflytek.dep.admin.controller;

import com.iflytek.dep.admin.model.DEPServer;
import com.iflytek.dep.admin.model.dto.DEPServerDto;
import com.iflytek.dep.admin.model.vo.DEPServerVo;
import com.iflytek.dep.admin.model.vo.PageVo;
import com.iflytek.dep.admin.service.DEPServerService;
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

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.controller
 * @Description:  DEPServer 管理模块类
 * @date 2019/2/27--19:11
 */
@Api(value = "DEPServer管理接口类", tags = {"数据交换平台与业务系统接口类"})
@Auth
@RestController
@RequestMapping("depServer")
public class DEPServerController {

    @Autowired
    private DEPServerService depServerService;

    Logger logger = LoggerFactory.getLogger(DEPServerController.class);

    @ApiOperation(value = "查询DEPServer列表", notes = "查询DEPServer列表")
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public ResponseBean<PageVo<DEPServerVo>> listDEPServer(@RequestBody DEPServerDto depServerDto) {
        try {
            if (!StringUtils.isEmpty(depServerDto.getDepServerRemark())) {
                depServerDto.setDepServerRemark(EscapeSql.escapeSql(depServerDto.getDepServerRemark()));
            }
            return new ResponseBean(depServerService.listDEPServer(depServerDto));
        } catch (Exception e) {
            logger.error("\n查询DEPServer列表失败:", e);
            return new ResponseBean("查询DEPServer列表失败");
        }
    }

    @ApiOperation(value = "添加DEPServer", notes = "添加DEPServer")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseBean<Integer> addDEPServer(@RequestBody DEPServer depServer) {
        try {
            return new ResponseBean(depServerService.addDEPServer(depServer));
        } catch (Exception e) {
            logger.error("\n添加DEPServer失败:", e);
            return new ResponseBean("添加DEPServer失败");
        }
    }

    @ApiOperation(value = "编辑DEPServer", notes = "编辑DEPServer")
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ResponseBean<Integer> updateDEPServer(@RequestBody DEPServer depServer) {
        try {
            return new ResponseBean(depServerService.updateDEPServer(depServer));
        } catch (Exception e) {
            logger.error("\n编辑DEPServer失败:", e);
            return new ResponseBean("编辑DEPServer失败");
        }
    }

    @ApiOperation(value = "删除DEPServer", notes = "删除DEPServer")
    @PostMapping("/delete")
    public ResponseBean deleteDEPServer(@RequestParam String depServerId) {
        try {
            return new ResponseBean(depServerService.deleteDEPServer(depServerId));
        } catch (Exception e) {
            logger.error("\n删除DEPServer失败:", e);
            return new ResponseBean("删除DEPServer失败");
        }
    }

}