package com.iflytek.dep.server.controller;

import com.google.gson.Gson;
import com.iflytek.dep.server.model.CACallBackDto;
import com.iflytek.dep.server.model.CACallDto;
import com.iflytek.dep.server.ca.CaServiceImpl;
import com.iflytek.dep.server.utils.FileUtil;
import com.iflytek.dep.server.utils.HttpClientUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.controller
 * @Description:
 * @date 2019/6/13--22:11
 */
@RestController
@RequestMapping("/efs")
@Api(value = "ca回调接口", tags = {"测试"})
public class CaController {
    private static Logger logger = LoggerFactory.getLogger(CaController.class);

    @Autowired
    CaServiceImpl caServiceImpl;

    @Autowired
    private Environment environment;

    @ApiOperation(value = "生成发送文件夹请求接口", notes = "生成发送文件夹请求接口")
    @RequestMapping("/cryptStatus")
    @ResponseBody
    public Map<String, Object> caCallBack(@RequestBody CACallBackDto cbd){
        logger.info("CA 回调开始 biz_sn:={},mode:={},file_url:={},efs_url:={}",cbd.getBiz_sn(),cbd.getMode(),cbd.getFile_url(),cbd.getEfs_url());
        String biz_sn = cbd.getBiz_sn();
        String mode = cbd.getMode();
        String callBackStatus = cbd.getStatus();
        Map<String, Object> ret =new HashMap<>();
        try{
            caServiceImpl.saveCallBackToDb(biz_sn, mode, callBackStatus);
            ret.put("ret_code", 1);
            ret.put("ret_msg", "成功");
            logger.info("CA 回调结束 biz_sn:={},mode:={},file_url:={},efs_url:={}",cbd.getBiz_sn(),cbd.getMode(),cbd.getFile_url(),cbd.getEfs_url());
        }
        catch (Exception e){
            ret.put("ret_code", 0);
            ret.put("ret_msg", "失败");
            logger.info("CA 回调失败biz_sn:={},mode:={},file_url:={},efs_url:={}",cbd.getBiz_sn(),cbd.getMode(),cbd.getFile_url(),cbd.getEfs_url(),e);
        }
        return ret;
    }

    @ApiOperation(value = "文件加密（解密）接口", notes = "文件加密（解密）接口")
    @RequestMapping("/encryption")
    @ResponseBody
    public Map<String, Object> encryption(@RequestBody CACallDto caCallDto) throws Exception{
        logger.info("文件{}开始 ",caCallDto.getEfs().getMode());

        String filePath =  URLDecoder.decode(caCallDto.getEfs().getFile_url(), "utf-8");
        String efsPath = URLDecoder.decode(caCallDto.getEfs().getEfs_url(), "utf-8");

        FileUtil.copyZipRetry(filePath,efsPath);
        new File(filePath).delete();

        Map<String, Object> ret =new HashMap<>();
        ret.put("ret_code", 1);
        ret.put("ret_msg", "成功");

        String biz_sn = caCallDto.getEfs().getBiz_sn();
        String mode = caCallDto.getEfs().getMode();

        //String serverPort = environment.getProperty("server.port");
        String callback_url = environment.getProperty("ca.callback.url");
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Map<String, String> call =new HashMap<>();
                call.put("biz_sn", biz_sn);
                call.put("status", "success");
                call.put("mode", mode);

                call.put("file_url", filePath);
                call.put("efs_url", efsPath);

                Gson gson = new Gson();
                String json = gson.toJson(call);
                try {
                    HttpClientUtil.doPost(callback_url ,json);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

        logger.info("文件{}结束 ",caCallDto.getEfs().getMode());
        return ret;

    }

}
