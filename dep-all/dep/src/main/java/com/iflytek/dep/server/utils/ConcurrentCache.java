package com.iflytek.dep.server.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.utils
 * @Description:有关缓存的读取
 * @date 2019/2/22--11:25
 */
public class ConcurrentCache {
    private static ConcurrentHashMap<String, Object> cacheMap = new ConcurrentHashMap<>();
    private ConcurrentCache(){}


    /**
     *@描述
     *@参数  [key]
     *@返回值  java.lang.Object
     *@创建人  朱一帆
     *@创建时间  2019/2/22
     *@修改人和其它信息
     */
    public static Object getFieldValue(String key) {
        // 缓存已有的tag以及根据tag查询的数据
        if (cacheMap.containsKey(key)) {
            return cacheMap.get(key);
        } else {
            return null;
        }
    }


    /**
     *@描述
     *@参数  [key, value]
     *@返回值  void
     *@创建人  朱一帆
     *@创建时间  2019/2/22
     *@修改人和其它信息
     */
    public static void setFieldValue(String key, Object value) {
        cacheMap.put(key, value);
    }

    /**
     *@描述
     *@参数  [key]
     *@返回值  void
     *@创建人  朱一帆
     *@创建时间  2019/2/22
     *@修改人和其它信息
     */
    public static void removeValue(String key) {
    	if (cacheMap.containsKey(key)) {
    		cacheMap.remove(key);
    	}
    }

    /**
     *@描述
     *@参数  [key]
     *@返回值  boolean
     *@创建人  朱一帆
     *@创建时间  2019/2/22
     *@修改人和其它信息
     */
    public static boolean contanisKey(String key){

    	if(StringUtils.isNotBlank(key)){
    		return cacheMap.containsKey(key);
    	}
    	return false;
    }
}
