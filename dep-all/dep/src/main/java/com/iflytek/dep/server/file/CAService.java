package com.iflytek.dep.server.file;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.iflytek.dep.server.utils.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.file
 * @Description:
 * @date 2019/6/13--21:33
 */
public class CAService {

    private static Logger logger = LoggerFactory.getLogger(CAService.class);

    public static String entrypt(String appcode, String apppwd, String url, String file_url, String efs_url, String callback_url, String biz_sn,String container_name,String receiver) throws Exception {
        //long startTime1 = System.currentTimeMillis();    //获取开始时间
        logger.info("调用ca地址{}，加密源文件{}，加密后地址{}", url, file_url, efs_url);
        JSONObject data = new JSONObject();
        data.put("api_key", appcode);
        data.put("api_secret", apppwd);
        JSONObject efs = new JSONObject();
        efs.put("mode", "encrypt");
        //String biz_sn =  UUID.randomUUID().toString();
        efs.put("biz_sn", biz_sn);
        efs.put("file_url", URLEncoder.encode(file_url, "UTF-8"));
        efs.put("efs_url", URLEncoder.encode(efs_url, "UTF-8"));
        efs.put("callback_url", URLEncoder.encode(callback_url, "UTF-8"));
        efs.put("container_name",container_name);
        efs.put("receiver",receiver);
        data.put("efs", efs);
        String post = HttpClientUtil.doPost(url, data.toString());
        if (post == null) {
            return null;
        }
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(post, HashMap.class);
        double retCode = (double) map.get("ret_code");
        //System.out.println("文件加密完成入库花费总时间为： " + ( System.currentTimeMillis() - startTime1) + "ms");
        //System.out.println("返回消息："+post.trim());
        logger.info("调用ca加密结果{}", post);
        return String.valueOf(retCode);
    }

    public static String decrypt(String appcode, String apppwd, String url, String file_url, String efs_url, String callback_url, String biz_sn,String container_name,String receiver) throws Exception {
        //long startTime1 = System.currentTimeMillis();    //获取开始时间
        logger.info("调用ca地址{}，解密源文件{}，解密后地址{}", url, file_url, efs_url);
        JSONObject data = new JSONObject();
        data.put("api_key", appcode);
        data.put("api_secret", apppwd);
        JSONObject efs = new JSONObject();
        efs.put("mode", "decrypt");
        //String biz_sn =  UUID.randomUUID().toString();
        efs.put("biz_sn", biz_sn);
        efs.put("file_url", URLEncoder.encode(file_url, "UTF-8"));
        efs.put("efs_url", URLEncoder.encode(efs_url, "UTF-8"));
        efs.put("callback_url", URLEncoder.encode(callback_url, "UTF-8"));
        efs.put("container_name",container_name);
        efs.put("receiver",receiver);
        data.put("efs", efs);
        String post = HttpClientUtil.doPost(url, data.toString());
        if (post == null) {
            return null;
        }
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(post, HashMap.class);
        double retCode = (double) map.get("ret_code");
        //System.out.println("文件解密完成入库花费总时间为： " + ( System.currentTimeMillis() - startTime1) + "ms");
        //System.out.println("返回消息："+post.trim());
        logger.info("调用ca解密结果{}", post);
        return String.valueOf(retCode);
    }
}
