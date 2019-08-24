package com.iflytek.dep.admin.controller.scheduled;


import com.iflytek.dep.admin.constants.MonitorType;
import com.iflytek.dep.admin.service.FTPScheduledService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dzr
 * @version V1.0
 * @Package com.iflytek.dep.server.controller
 * @Description:FTPScheduledController
 * @date 2019/2/7--10:00
 */
@Api(value="FTP-server定时任务接口类",tags={"定时探测接口类"})
@RestController
@RequestMapping("/service/ftpScheduled")
public class FTPScheduledController {
    private Logger logger = LoggerFactory.getLogger(FTPScheduledController.class);

    @Autowired
    @Qualifier("ftpScheduledService")
    private FTPScheduledService ftpScheduledService;

    /**
     * 开启定时任务
     * @param ftpId
     * @param type
     * @return
     */
    @ApiOperation(value = "ftpMonitor启动", notes = "ftpMonitor启动")
    @GetMapping("/start")
    public boolean start(@RequestParam(name = "ftpId") String ftpId, @RequestParam(name = "type") Integer type) {
        return ftpScheduledService.start(ftpId, MonitorType.getType(type));
    }

    /**
     * 关闭定时任务
     *
     * @param ftpId
     * @param type
     * @return
     */
    @ApiOperation(value = "ftpMonitor关闭", notes = "ftpMonitor关闭")
    @GetMapping("/close")
    public boolean close(@RequestParam(name = "ftpId") String ftpId, @RequestParam(name = "type") Integer type) {
        return ftpScheduledService.close(ftpId, MonitorType.getType(type));
    }
}