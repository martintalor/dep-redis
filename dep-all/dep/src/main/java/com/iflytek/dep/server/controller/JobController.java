package com.iflytek.dep.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 业务系统接口
 */
@Api(value="业务系统接口类",tags={"数据交换平台与业务系统接口类"})
@RestController
public class JobController {
    @ApiOperation(value = "测试接口",notes="测试接口")
    @GetMapping("/test")
    public String test(){
        return System.currentTimeMillis()+"";
    }
}
