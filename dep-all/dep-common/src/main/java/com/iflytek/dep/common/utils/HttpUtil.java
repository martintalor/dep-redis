package com.iflytek.dep.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.utils
 * @Description: HttpUtil
 * @date 2019/2/27--19:20
 */
public class HttpUtil {
    /**
     * 向指定 URL 发送get方法的请求
     *
     * @param url 发送请求的 URL
     * @param
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url); // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection(); // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*"); connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect(); // 获取所有响应头字段
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            return "error";
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
            }
        }
    }
}
