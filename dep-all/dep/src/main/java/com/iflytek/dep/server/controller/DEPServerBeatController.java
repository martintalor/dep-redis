package com.iflytek.dep.server.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.controller
 * @Description:
 * @date 2019/2/7--10:00
 */
@Api(value="DEP-server心跳响应接口类",tags={"定时探测接口类"})
@RestController
@RequestMapping("/service")
public class DEPServerBeatController {
    private Logger logger = LoggerFactory.getLogger(DEPServerBeatController.class);

    @ApiOperation(value = "server心跳接口", notes = "server心跳接口")
    @GetMapping("/beat")
    public String beat() {
        return  "ok";
    }
}