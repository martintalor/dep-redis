package com.iflytek.dep.admin.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.iflytek.dep.admin.model.vo.DirectoryVo;
import com.iflytek.dep.admin.model.vo.FTPConfigVo;
import com.iflytek.dep.admin.model.vo.FileVo;
import com.iflytek.dep.admin.service.FileViewService;
import com.iflytek.dep.common.annotation.Auth;
import com.iflytek.dep.common.utils.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.controller
 * @Description:
 * @date 2019/3/6--11:28
 */
@Api(value = "文件查看接口类", tags = {"数据交换平台与业务系统接口类"})
@Auth
@RestController
@RequestMapping("fileView")
public class FileViewController {

    Logger logger = LoggerFactory.getLogger(FileViewController.class);

    @Value("${logicServerNode.serverNodeId}")
    public String serverNodeId;

    @Autowired
    private FileViewService fileViewService;

    @ApiOperation(value = "ftp节点列表", notes = "ftp节点列表")
    @RequestMapping(value = "listFTP", method = RequestMethod.GET)
    public ResponseBean<List<FTPConfigVo>> listFTP() {
        try {
            return new ResponseBean(fileViewService.listFTP(serverNodeId));
        } catch (Exception e) {
            logger.error("\n获取ftp节点列表失败:", e);
            return new ResponseBean("获取ftp节点列表失败");
        }
    }

    @ApiOperation(value = "ftp目录信息", notes = "ftp目录信息")
    @RequestMapping(value = "listDirectories", method = RequestMethod.GET)
    public ResponseBean<List<DirectoryVo>> listDirectories(@RequestParam String ftpId) {
        try {
            List<DirectoryVo> list = fileViewService.listDirectories(ftpId);
            if (CollectionUtil.isNotEmpty(list)) {
                for (DirectoryVo directoryVo : list) {
                    checkChildren(directoryVo);
                }
            }
            return new ResponseBean(list);
        } catch (Exception e) {
            logger.error("\n获取ftp目录信息失败:", e);
            return new ResponseBean("获取ftp目录信息失败");
        }
    }

    //避免循环引用
    private void checkChildren(DirectoryVo directoryVo) {
        if (directoryVo.getChildren().isEmpty()) {
            directoryVo.setChildren(null);
        } else {
            for (DirectoryVo childDir : directoryVo.getChildren()) {
                checkChildren(childDir);
            }
        }
    }

    @ApiOperation(value = "ftp文件信息", notes = "ftp文件信息")
    @RequestMapping(value = "listFiles", method = RequestMethod.GET)
    public ResponseBean<List<FileVo>> listFiles(@RequestParam String ftpId, @RequestParam String directory) {
        try {
            return new ResponseBean(fileViewService.listFiles(ftpId,directory));
        } catch (Exception e) {
            logger.error("\n获取ftp文件信息失败:", e);
            return new ResponseBean("获取ftp文件信息失败");
        }
    }
}