package com.iflytek.dep.server.controller;


import com.iflytek.dep.server.service.impl.EtlServiceImpl;
import com.iflytek.dep.server.utils.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.server.controller
 * @Description:
 * @date 2019/4/23
 */
@Api(value = "ETL通知重试接口", tags = {"ETL通知重试接口"})
@RestController
@RequestMapping("/etlInfo")
public class ETLInfoRetryController {
    private Logger logger = LoggerFactory.getLogger(ETLInfoRetryController.class);

    @Autowired
    private EtlServiceImpl etlService;

    @ApiOperation(value = "ETL通知重试接口", notes = "ETL通知重试接口")
    @GetMapping("/retry")
    public ResponseBean retry() throws Exception {


        etlService.notifyEtl();

        return new ResponseBean();
    }


}