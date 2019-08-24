package com.iflytek.dep.server.utils;

import com.google.gson.Gson;
import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.server.constants.LevelEnum;
import com.iflytek.dep.server.constants.RedisQueueType;
import com.iflytek.dep.server.model.CACallBackDto;
import com.iflytek.dep.server.model.CaStepRecorders;
import com.iflytek.dep.server.redis.DownloadTaskStatus;
import com.iflytek.dep.server.redis.PackageInfo;
import com.iflytek.dep.server.redis.UploadTaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Component
public final class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    /**
     *@描述 存入task任务
     *@参数  [fileInfo, fileName, redisQueue]
     *@返回值  void
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/6/23
     *@修改人和其它信息
     */
    public void pushTask(PackageInfo fileInfo, String fileName, String pengingQueue,String doingQueue) throws Exception {

        logger.info("新的PKG文件:={} 存入队列:={} 开始",fileName,pengingQueue);

        String level = "";
        try{
            String packName = fileName.split("\\.")[0];
            // 如果没有level级别，默认level级别3，向下兼容无level版本
            if (packName.indexOf("+") == -1) {
                level = String.valueOf( LevelEnum.LEVEL3.getValue() );
            } else{
                level = packName.substring(packName.length() - 1);
            }

            List<Object> allList  =  lGet(pengingQueue + "_" + level,0,-1);
            Map<Object, Object> map = hmget(doingQueue + "_" + level);

            boolean find = false;
            for(Object object:allList){
                Gson gson = new Gson();
                PackageInfo packageInfo = gson.fromJson((String)object, PackageInfo.class);
                if(fileName.equals(packageInfo.getFileName())){
                    find = true;
                    break;
                }
            }

            for(Object object:map.keySet()){
                String key = (String) object;
                String value = (String) map.get(key);
                Gson gson = new Gson();
                PackageInfo packageInfo = gson.fromJson(value, PackageInfo.class);
                if(fileName.equals(packageInfo.getFileName())){
                    find = true;
                    break;
                }
            }

            if(find){
                logger.info("新的PKG文件:={} 在队列:={} 存在，跳过",fileName,pengingQueue + "_" + level);
            }
            else{
                Gson gson = new Gson();
                String json = gson.toJson(fileInfo);
                lpush(pengingQueue + "_" + level,json);
                logger.info("新的PKG文件:={} 存入队列:={} 成功,json{}",fileName,pengingQueue + "_" + level,json);
            }
        }
        catch (Exception e){
            logger.error("新的PKG文件:={} 存入队列:={} 失败",fileName,pengingQueue + "_" + level,e);
            throw new Exception("存入task任务失败");
        }
    }

    /**
     *@描述 添加待打包上传任务
     *@参数  [outPath, fileName]
     *@返回值  void
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/6/23
     *@修改人和其它信息
     */
    public void pushUpTask(String outPath, String fileName) throws Exception {

        PackageInfo fileInfo =  PackageInfo.from(fileName,fileName,"", UploadTaskStatus.UP_DEP_PENGING.getName(),DateUtils.getDateTime(),outPath,null,null,null,null, outPath);

        pushTask(fileInfo,fileName,RedisQueueType.PENG_UP_PKG.getCode(),RedisQueueType.DOING_UP_PKG.getCode());
    }

    /**
     *@描述 添加待下载任务，放入redis队列
     *@参数  [fileName, curNodeId]
     *@返回值  void
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/6/23
     *@修改人和其它信息
     */
    public void pushDownTask(String filePath,String fileName, String curNodeId) throws Exception {

        PackageInfo fileInfo =  PackageInfo.from(fileName,fileName,curNodeId, DownloadTaskStatus.DOWN_DEP_DOING.getName(),DateUtils.getDateTime(),filePath,null,null,null,null,"");

        pushTask(fileInfo,fileName,RedisQueueType.PENG_DOWN_PKG.getCode(),RedisQueueType.DOING_DOWN_PKG.getCode());
    }

    public void pushAckTask( String fileName, String curNodeId) throws Exception {

        PackageInfo fileInfo =  PackageInfo.from(fileName,fileName,curNodeId, DownloadTaskStatus.DOWN_ACK_PENGING.getName(),DateUtils.getDateTime(),"",null,null,null,null,"");

        pushTask(fileInfo,fileName,RedisQueueType.PENG_DOWN_ACK.getCode(),RedisQueueType.DOING_DOWN_ACK.getCode());
    }

    public void pushAcCallBackTask(String packageId,String biz_sn) throws Exception {

        CaStepRecorders caStepRecorders =  new CaStepRecorders();
        caStepRecorders.setPackageId(packageId);
        caStepRecorders.setBizSn(biz_sn);
        pushAcCallBackTask(packageId,biz_sn, caStepRecorders ,RedisQueueType.PENG_CA.getCode(),RedisQueueType.DOING_DOWN_ACK.getCode());
    }

    private void pushAcCallBackTask(String packageId,String biz_sn,CaStepRecorders caStepRecorders, String pengingQueue,String doingQueue) throws Exception {

        logger.info("新的PKG文件:={} 存入队列:={} 开始",biz_sn,pengingQueue);

        String level = "";
        try{
            String packName = packageId.split("\\.")[0];
            // 如果没有level级别，默认level级别3，向下兼容无level版本
            if (packName.indexOf("+") == -1) {
                level = String.valueOf( LevelEnum.LEVEL3.getValue() );
            } else{
                level = packName.substring(packName.length() - 1);
            }

            List<Object> allList  =  lGet(pengingQueue + "_" + level,0,-1);
            Map<Object, Object> map = hmget(doingQueue + "_" + level);

            boolean find = false;
            for(Object object:allList){
                Gson gson = new Gson();
                CaStepRecorders packageInfo = gson.fromJson((String)object, CaStepRecorders.class);
                if(biz_sn.equals(packageInfo.getBizSn())){
                    find = true;
                    break;
                }
            }

            for(Object object:map.keySet()){
                String key = (String) object;
                String value = (String) map.get(key);
                Gson gson = new Gson();
                CaStepRecorders packageInfo = gson.fromJson(value, CaStepRecorders.class);
                if(biz_sn.equals(packageInfo.getBizSn())){
                    find = true;
                    break;
                }
            }

            if(find){
                logger.info("新的PKG文件:={} 在队列:={} 存在，跳过",biz_sn,pengingQueue + "_" + level);
            }
            else{
                Gson gson = new Gson();
                String json = gson.toJson(caStepRecorders);
                lpush(pengingQueue + "_" + level,json);
                logger.info("新的PKG文件:={} 存入队列:={} 成功,json{}",biz_sn,pengingQueue + "_" + level,json);
            }
        }
        catch (Exception e){
            logger.error("新的PKG文件:={} 存入队列:={} 失败",biz_sn,pengingQueue + "_" + level,e);
            throw new Exception("存入task任务失败");
        }
    }


    /**
     *@描述 添加ETL待入库任务
     *@参数  [packPath, fileName, jobName]
     *@返回值  void
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/6/23
     *@修改人和其它信息
     */
    public void pushEtlTask(String packPath, String packName, String jobName)  throws  Exception{

        PackageInfo packageInfo = PackageInfo.from(packName,packName,"", DownloadTaskStatus.DOWN_ETL_PENGING.getName(), DateUtils.getDateTime(),packPath,jobName,null,null,null,packPath);

        pushTask(packageInfo,packName, RedisQueueType.PENG_DOWN_ETL.getCode(), RedisQueueType.DOING_DOWN_ETL.getCode());

    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if ((key != null) && (key.length > 0)) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return (key == null) ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue()
                        .set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public Object getset(String key, String newValue) {
        try {
            return redisTemplate.opsForValue()
                    .getAndSet(key, newValue);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }

        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }

        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);

            if (time > 0) {
                expire(key, time);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);

            if (time > 0) {
                expire(key, time);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();

            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);

            if (time > 0) {
                expire(key, time);
            }

            return count;
        } catch (Exception e) {
            e.printStackTrace();

            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();

            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);

            return count;
        } catch (Exception e) {
            e.printStackTrace();

            return 0;
        }
    }

    // ===============================list=================================
    public void lpush(String key, String value) {
        try {
            long l = redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String lpop(String key) {
        try {
            Object o = redisTemplate.opsForList().leftPop(key);
            return (String) o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void rpush(String key, String value) {
        try {
            long l = redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String rpop(String key) {
        try {
            Object o = redisTemplate.opsForList().rightPop(key);
            return (String) o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();

            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);

            if (time > 0) {
                expire(key, time);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);

            if (time > 0) {
                expire(key, time);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);

            return remove;
        } catch (Exception e) {
            e.printStackTrace();

            return 0;
        }
    }

    public String doAcTask(String pararm) throws Exception {
        List<String> keys = new ArrayList<String>();
        List<String> args = new ArrayList<String>();
        args.add(pararm);

        String LUA =
                "local param = ARGV[1]\n" +
                        "local table = cjson.decode(param)\n" +
                        "local status_old = table[\"status_old\"]\n" +
                        "local status_new = table[\"status_new\"]\n" +
                        "local time = table[\"time\"]\n" +
                        "local pengingKey = table[\"pengingKey\"]\n" +
                        "local doingKey = table[\"doingKey\"]\n" +
                        "local level = table[\"level\"]\n" +
                        "for i=0,9,1 do\n" +
                        "     local len =  redis.call(\"llen\", pengingKey..'_'..i)\n" +
                        "     if len>0 then\n" +
                        "       local return_result = redis.call(\"rpop\", pengingKey..'_'..i)\n" +
                        "       return_result = (return_result and {return_result} or {''})[1]\n" +
                        "       if return_result=='' then\n" +
                        "              return ''\n" +
                        "       end\n" +
                        "       local data = cjson.decode(return_result)\n"  +
                        "       local bizSn = data[\"bizSn\"]\n"  +
                        "       local packageId = data[\"packageId\"]\n"  +
                        "       local len_doing = redis.call(\"hget\", doingKey..'_'..i,bizSn)\n" +
                        "       len_doing = (len_doing and {len_doing} or {''})[1]\n" +
                        "       if len_doing ~= '' then\n" +
                        "               return  ''\n" +
                        "       end\n" +
                        "       local retTable = {}\n" +
                        "       retTable[\"bizSn\"] = bizSn\n" +
                        "       retTable[\"packageId\"] = packageId\n" +
                        "       retTable[\"status\"] = status_new\n" +
                        "       retTable[\"time\"] = time\n" +
                        "       local jsonStr = cjson.encode(retTable)\n" +
                        "       redis.call(\"HSET\", doingKey..'_'..i, packageId, jsonStr)\n" +
                        "       return jsonStr\n" +
                        "     end\n" +
                        "end\n" +
                        "return ''\n";


        String result = null;
        try {
            result = (String) redisTemplate.execute(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    Object nativeConnection = connection.getNativeConnection();

                    // 集群模式和单点模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                    // 集群
                    if (nativeConnection instanceof JedisCluster) {
                        return (String) ((JedisCluster) nativeConnection).eval(LUA, keys, args);
                    }

                    // 单点
                    else if (nativeConnection instanceof Jedis) {
                        return (String) ((Jedis) nativeConnection).eval(LUA, keys, args);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("doTask keys：{}，\n pararm:{}, \n args:{}, \n error:{}",keys,pararm,args,e);
            throw new Exception(e);
        }
        return result;
    }


    public String doTask(String pararm) throws Exception {
        List<String> keys = new ArrayList<String>();
        List<String> args = new ArrayList<String>();
        args.add(pararm);

        String LUA =
                "local param = ARGV[1]\n" +
                        "local table = cjson.decode(param)\n" +
                        "local status_old = table[\"status_old\"]\n" +
                        "local status_new = table[\"status_new\"]\n" +
                        "local time = table[\"time\"]\n" +
                        "local pengingKey = table[\"pengingKey\"]\n" +
                        "local doingKey = table[\"doingKey\"]\n" +
                        "local level = table[\"level\"]\n" +
                        "for i=0,9,1 do\n" +
                        "     local len =  redis.call(\"llen\", pengingKey..'_'..i)\n" +
                        "     if len>0 then\n" +
                        "       local return_result = redis.call(\"rpop\", pengingKey..'_'..i)\n" +
                        "       return_result = (return_result and {return_result} or {''})[1]\n" +
                        "       if return_result=='' then\n" +
                        "              return ''\n" +
                        "       end\n" +
                        "       local data = cjson.decode(return_result)\n"  +
                        "       local packageId = data[\"packageId\"]\n"  +
                        "       local len_doing = redis.call(\"hget\", doingKey..'_'..i,packageId)\n" +
                        "       len_doing = (len_doing and {len_doing} or {''})[1]\n" +
                        "       if len_doing ~= '' then\n" +
                        "               return  ''\n" +
                        "       end\n" +
                        "       local retTable = {}\n" +
                        "       retTable[\"packDirPath\"] = data[\"packDirPath\"]\n" +
                        "       retTable[\"fileName\"] = data[\"fileName\"]\n" +
                        "       retTable[\"path\"] = data[\"path\"]\n" +
                        "       retTable[\"filePath\"] = data[\"filePath\"]\n" +
                        "       retTable[\"params\"] = data[\"params\"]\n" +
                        "       retTable[\"packageId\"] = data[\"packageId\"]\n" +
                        "       retTable[\"curNodeId\"] = data[\"curNodeId\"]\n" +
                        "       retTable[\"status\"] = status_new\n" +
                        "       retTable[\"time\"] = time\n" +
                        "       local jsonStr = cjson.encode(retTable)\n" +
                        "       redis.call(\"HSET\", doingKey..'_'..i, packageId, jsonStr)\n" +
                        "       return jsonStr\n" +
                        "     end\n" +
                        "end\n" +
                        "return ''\n";


        String result = null;
        try {
            result = (String) redisTemplate.execute(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    Object nativeConnection = connection.getNativeConnection();

                    // 集群模式和单点模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                    // 集群
                    if (nativeConnection instanceof JedisCluster) {
                        return (String) ((JedisCluster) nativeConnection).eval(LUA, keys, args);
                    }

                    // 单点
                    else if (nativeConnection instanceof Jedis) {
                        return (String) ((Jedis) nativeConnection).eval(LUA, keys, args);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("doTask keys：{}，\n pararm:{}, \n args:{}, \n error:{}",keys,pararm,args,e);
            throw new Exception(e);
        }
        return result;
    }

    public String rePengingTaskAc(String pengingList, String doingList, String packageId, String status_old, String status_new, String time_new) throws Exception {
        List<String> keys = new ArrayList<String>();
        keys.add(pengingList);
        keys.add(doingList);

        List<String> args = new ArrayList<String>();
        args.add(packageId);
        args.add(status_old);
        args.add(status_new);
        args.add(time_new);

        String LUA =
                "local key1 = KEYS[1]\n" +
                        "local key2 = KEYS[2]\n" +
                        "local packageId1 = ARGV[1]\n" +
                        "local status_old = ARGV[2]\n" +
                        "local status_new = ARGV[3]\n" +
                        "local time_new = ARGV[4]\n" +
                        "local return_result = redis.call(\"hget\", key2,packageId1)\n" +
                        "return_result = (return_result and {return_result} or {''})[1]" +
                        "if return_result=='' then\n" +
                        "   return '1'\n" +
                        "end\n" +
                        "local map = cjson.decode(return_result)\n" +
                        "local packageId = map[\"packageId\"]\n" +
                        "local status = map[\"status\"]\n" +
                        "local bizSn = map[\"bizSn\"]\n" +

                        "if status~= status_old then\n" +
                        "   return '2'\n" +
                        "end\n" +

                        "local packageIdLock = redis.call(\"hget\",key2, packageId1)\n" +
                        "local packageIdLock = (packageIdLock and {packageIdLock} or {''})[1]" +
                        "if packageIdLock =='' then\n" +
                        "   return '3'\n" +
                        "end\n" +

                        "local retTable = {}\n" +
                        "retTable[\"packageId\"] = packageId\n" +
                        "retTable[\"status\"] = status_new\n" +
                        "retTable[\"time\"] = time_new\n" +
                        "retTable[\"bizSn\"] = bizSn\n" +
                        "local jsonStr = cjson.encode(retTable)" +
                        "redis.call(\"lpush\",key1, jsonStr)\n" +
                        "redis.call(\"hdel\", key2,packageId1 )\n" +
                        "return jsonStr\n";


        String result = null;
        try {
            result = (String) redisTemplate.execute(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {

                    //                logger.info("rePengingTask lua --->>> \n {},\n args:{} ",LUA,args);

                    Object nativeConnection = connection.getNativeConnection();
                    // 集群模式和单点模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                    // 集群
                    if (nativeConnection instanceof JedisCluster) {
                        return (String) ((JedisCluster) nativeConnection).eval(LUA, keys, args);
                    }

                    // 单点
                    else if (nativeConnection instanceof Jedis) {
                        return (String) ((Jedis) nativeConnection).eval(LUA, keys, args);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("rePengingTask keys：{}，\n args:{}, error {}",keys,args,e);
            throw new Exception(e);
        }
        return result;
    }

    public String rePengingTask(String pengingList, String doingList, String packageId, String status_old, String status_new, String time_new) throws Exception {
        List<String> keys = new ArrayList<String>();
        keys.add(pengingList);
        keys.add(doingList);

        List<String> args = new ArrayList<String>();
        args.add(packageId);
        args.add(status_old);
        args.add(status_new);
        args.add(time_new);

        String LUA =
                "local key1 = KEYS[1]\n" +
                        "local key2 = KEYS[2]\n" +
                        "local packageId1 = ARGV[1]\n" +
                        "local status_old = ARGV[2]\n" +
                        "local status_new = ARGV[3]\n" +
                        "local time_new = ARGV[4]\n" +
                        "local return_result = redis.call(\"hget\", key2,packageId1)\n" +
                        "return_result = (return_result and {return_result} or {''})[1]" +
                        "if return_result=='' then\n" +
                        "   return '1'\n" +
                        "end\n" +
                        "local map = cjson.decode(return_result)\n" +
                        "local packageId = map[\"packageId\"]\n" +
                        "local status = map[\"status\"]\n" +
                        "local curNodeId = map[\"curNodeId\"]\n" +
                        "local packDirPath = map[\"packDirPath\"]\n" +
                        "local path = map[\"path\"]\n" +
                        "local filePath = map[\"filePath\"]\n" +
                        "local fileName = map[\"fileName\"]\n" +

                        "if status~= status_old then\n" +
                        "   return '2'\n" +
                        "end\n" +

                        "local packageIdLock = redis.call(\"hget\",key2, packageId1)\n" +
                        "local packageIdLock = (packageIdLock and {packageIdLock} or {''})[1]" +
                        "if packageIdLock =='' then\n" +
                        "   return '3'\n" +
                        "end\n" +

                        "local retTable = {}\n" +
                        "retTable[\"packageId\"] = packageId\n" +
                        "retTable[\"curNodeId\"] = curNodeId\n" +
                        "retTable[\"status\"] = status_new\n" +
                        "retTable[\"time\"] = time_new\n" +
                        "retTable[\"packDirPath\"] = packDirPath\n" +
                        "retTable[\"path\"] = path\n" +
                        "retTable[\"filePath\"] = filePath\n" +
                        "retTable[\"fileName\"] = fileName\n" +
                        "local jsonStr = cjson.encode(retTable)" +
                        "redis.call(\"lpush\",key1, jsonStr)\n" +
                        "redis.call(\"hdel\", key2,packageId1 )\n" +
                        "return jsonStr\n";


        String result = null;
        try {
            result = (String) redisTemplate.execute(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {

                    //                logger.info("rePengingTask lua --->>> \n {},\n args:{} ",LUA,args);

                    Object nativeConnection = connection.getNativeConnection();
                    // 集群模式和单点模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                    // 集群
                    if (nativeConnection instanceof JedisCluster) {
                        return (String) ((JedisCluster) nativeConnection).eval(LUA, keys, args);
                    }

                    // 单点
                    else if (nativeConnection instanceof Jedis) {
                        return (String) ((Jedis) nativeConnection).eval(LUA, keys, args);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("rePengingTask keys：{}，\n args:{}, error {}",keys,args,e);
            throw new Exception(e);
        }
        return result;
    }


    public void delTask(String doingList, String packageId) throws Exception {
        List<String> keys = new ArrayList<String>();
        keys.add(doingList);

        List<String> args = new ArrayList<String>();
        args.add(packageId);

        String LUA =
                "local key1 = KEYS[1]\n" +
                        "local packageId1 = ARGV[1]\n" +
                        "redis.call(\"hdel\", key1,packageId1 )\n";


        try {
            redisTemplate.execute(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {

                    //                logger.info("rePengingTask lua --->>> \n {},\n args:{} ",LUA,args);

                    Object nativeConnection = connection.getNativeConnection();
                    // 集群模式和单点模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                    // 集群
                    if (nativeConnection instanceof JedisCluster) {
                        return (String) ((JedisCluster) nativeConnection).eval(LUA, keys, args);
                    }

                    // 单点
                    else if (nativeConnection instanceof Jedis) {
                        return (String) ((Jedis) nativeConnection).eval(LUA, keys, args);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("delTask keys：{}，\n args:{}, error {}",keys,args,e);
            throw new Exception(e);
        }
    }

    public void updateTask(String packageId, String doingList)  throws Exception{
        List<String> keys = new ArrayList<String>();
        keys.add(doingList);

        List<String> args = new ArrayList<String>();
        args.add(packageId);

        String LUA =
                "local key1 = KEYS[1]\n" +
                        "local packageId1 = ARGV[1]\n" +
                        "redis.call(\"hdel\", key1,packageId1 )\n";


        try {
            redisTemplate.execute(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {

                    //                logger.info("rePengingTask lua --->>> \n {},\n args:{} ",LUA,args);

                    Object nativeConnection = connection.getNativeConnection();
                    // 集群模式和单点模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                    // 集群
                    if (nativeConnection instanceof JedisCluster) {
                        return (String) ((JedisCluster) nativeConnection).eval(LUA, keys, args);
                    }

                    // 单点
                    else if (nativeConnection instanceof Jedis) {
                        return (String) ((Jedis) nativeConnection).eval(LUA, keys, args);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("delTask keys：{}，\n args:{}, error {}",keys,args,e);
            throw new Exception(e);
        }
    }
}

