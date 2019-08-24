package com.iflytek.dep.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.utils
 * @Description: 数据初始化
 * @date 2019/2/25--13:41
 */
public class DataInit {

        private static final Logger LOGGER = LoggerFactory.getLogger(DataInit.class);


        @Autowired




        // 在构造方法执行后执行初始化数据的关键；

        /**
         *@描述  init
         *@参数  []
         *@返回值  void
         *@创建人  朱一帆
         *@创建时间  2019/2/25
         *@修改人和其它信息
         */
        @PostConstruct
        public void init() {

            initMap();

        }



        public void initMap() {

            System.out.println("开始初始化信息：");

        }



    /**
     *@描述  getData
     *@参数  [key]
     *@返回值  java.lang.Object
     *@创建人  朱一帆
     *@创建时间  2019/2/25
     *@修改人和其它信息
     */
        public static Object getData(String key){

            return ConcurrentCache.getFieldValue(key);

        }







}
