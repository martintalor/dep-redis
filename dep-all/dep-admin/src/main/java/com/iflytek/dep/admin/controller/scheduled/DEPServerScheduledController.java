package com.iflytek.dep.admin.controller.scheduled;


import com.iflytek.dep.admin.service.DEPServerScheduledService;
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
 * @Description:DEPServerScheduledController
 * @date 2019/2/7--10:00
 */
@Api(value="DEP-server定时任务接口类",tags={"定时探测接口类"})
@RestController
@RequestMapping("/service/depServerScheduled")
public class DEPServerScheduledController {
    private Logger logger = LoggerFactory.getLogger(DEPServerScheduledController.class);

    @Autowired
    @Qualifier("depServerScheduledService")
    private DEPServerScheduledService depServerScheduledService;

    /**
     * 开启定时任务
     * @param depServerId
     * @return
     */
    @ApiOperation(value = "depServerMonitor启动", notes = "depServerMonitor启动")
    @GetMapping("/start")
    public boolean start(@RequestParam(name = "depServerId") String depServerId) {
        return depServerScheduledService.start(depServerId);
    }

    /**
     * 关闭定时任务
     *
     * @param depServerId
     * @return
     */
    @ApiOperation(value = "depServerMonitor关闭", notes = "depServerMonitor关闭")
    @GetMapping("/close")
    public boolean close(@RequestParam(name = "depServerId") String depServerId) {
        return depServerScheduledService.close(depServerId);
    }

    /**
     * 开启定时任务
     * @return
     */
    @ApiOperation(value = "定时删除本地文件启动", notes = "定时删除本地文件启动")
    @GetMapping("/startDelLocalFile")
    public boolean startDelLocalFile() {
        return depServerScheduledService.startDelLocalFile();
    }

    /**
     * 关闭定时任务
     *
     * @return
     */
    @ApiOperation(value = "定时删除本地文件启动关闭", notes = "定时删除本地文件启动关闭")
    @GetMapping("/closeDelLocalFile")
    public boolean closeDelLocalFile() {
        return depServerScheduledService.closeDelLocalFile();
    }

}