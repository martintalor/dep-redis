package com.iflytek.dep.admin.controller.scheduled;


import com.iflytek.dep.admin.service.DocServerScheduledService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *
 * 文档服务器探测的定时任务
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.server.controller
 * @Description:DocServerScheduledController
 * @date 2019/3/13
 */
@Api(value="Doc-server定时任务接口类",tags={"定时探测接口类"})
@RestController
@RequestMapping("/service/docServerScheduled")
public class DocServerScheduledController {
    private Logger logger = LoggerFactory.getLogger(DocServerScheduledController.class);

    @Resource(name="docServerService")
    DocServerScheduledService docServerScheduledService;

    /**
     * 开启定时任务
     * @return
     */
    @ApiOperation(value = "docServerMonitor启动", notes = "docServerMonitor启动")
    @GetMapping("/start")
    public boolean start(@RequestParam(name = "nodeId") String nodeId) {
        return docServerScheduledService.start(nodeId);
    }

    /**
     * 关闭定时任务
     *
     * @return
     */
    @ApiOperation(value = "docServerMonitor关闭", notes = "docServerMonitor关闭")
    @GetMapping("/close")
    public boolean close(@RequestParam(name = "nodeId") String nodeId) {
        return docServerScheduledService.close(nodeId);
    }

}