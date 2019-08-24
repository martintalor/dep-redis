package com.iflytek.dep.server.utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Package com.iflytek.dep.server.utils
 * @Description:
 * @date 2019/4/17--14:35
 */
public class ConcurrentCacheSync {

    public static ConcurrentHashMap<String,String> dataMap = new ConcurrentHashMap<String,String>();

    /**
     *@描述 获取value
     *@param key
     *@return
     *@返回值  java.lang.String
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/4/17
     *@修改人和其它信息
     */
    public static String getValue(String key) {

        String value = "";
        synchronized (dataMap) {
           value = dataMap.get(key);
        }
        return value;
    }

    public static String setValue(String key, String value) {
        synchronized (dataMap) {
            dataMap.put(key, value);
        }
        return value;
    }

    public static String lockToken(String key) {
        String token = "false";
        synchronized (dataMap) {
            if ( !StringUtil.isEmpty(key) ) {
                token =  dataMap.get(key);
            }
        }
        return token;
    }

//    public static ReadWriteLock lock = new ReentrantReadWriteLock();//创建读写锁的实例
//
//    public static Object getData(String key){
//        lock.readLock().lock();//读取前先上锁
//        Object val=null;
//        try{
//            val = dataMap.get(key);
//            if(val == null){
//                // Must release read lock before acquiring write lock
//                lock.readLock().unlock();
//                lock.writeLock().lock();
//                try{
//                    //可能已经由其他线程写入数据
//                    if(val == null){
//                        //dataMap.put(key, "");//query from db
//                        val = queryDataFromDB(key);
//                    }
//                }finally{
//                    //Downgrade by acquiring read lock before releasing write lock
//                    lock.readLock().lock();
//                    // Unlock write, still hold read
//                    lock.writeLock().unlock();
//                }
//            }
//        }finally{
//            lock.readLock().unlock();//最后一定不要忘记释放锁
//        }
//        System.out.println("get data key="+key+">val="+val);
//        return val;
//    }
//
//    static Object queryDataFromDB(String key){
//        Object val = new Random().nextInt(1000);
//        dataMap.put(key, String.valueOf(val) );
//        System.out.println("write into data key="+key+">val="+val);
//        return val;
//    }




}
