package com.iflytek.dep.server.redis;

import com.google.gson.Gson;
import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.server.config.redis.DepThreadFactory;
import com.iflytek.dep.server.constants.RedisQueueType;
import com.iflytek.dep.server.service.dataPack.ParseAckService;
import com.iflytek.dep.server.utils.RedisUtil;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;


@Component
@EnableScheduling
public class UpAndDownTaskACK extends UpAndDownTask {

    private Logger logger = LoggerFactory.getLogger(UpAndDownTaskACK.class);

    final String pengingList_download_ack = RedisQueueType.PENG_DOWN_ACK.getCode();
    final String doingList_download_ack = RedisQueueType.DOING_DOWN_ACK.getCode();

    @Autowired
    ParseAckService parseAckService ;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonService redissonService;

    public UpAndDownTaskACK() {
        if (threadPool == null) {
            BlockingQueue<Runnable> bq = new ArrayBlockingQueue<Runnable>(1);
            threadPool = new ThreadPoolExecutor(1, threadNumber, 50, TimeUnit.MILLISECONDS, bq,new DepThreadFactory("redis-ack-task") {});
        }
    }

    @Scheduled(fixedRate = 10)
    public void ackTaskListScheduled()
    {
        try {
            logger.debug("ackTaskListScheduled----do task >>>[{}]",Thread.currentThread().getName());
            //ACK
            taskScheduled("下载ACK",pengingList_download_ack,doingList_download_ack,10, DownloadTaskStatus.DOWN_DEP_PENGING.getName(), DownloadTaskStatus.DOWN_DEP_DOING.getName(),threadPool);

        } catch (Exception e) {
            //logger.error("ackTaskListScheduled----error >>>[{}]",e);
        }
    }

    public void taskScheduled(String taskType,String pengingList,String doingList,int maxLevel,String status_old,String status_new,ThreadPoolExecutor threadPool) throws Exception {
        int queueSize = threadPool.getQueue().size();
        int activeCount = threadPool.getActiveCount();
        if((queueSize + activeCount ) >=threadNumber){
            logger.debug("超过 {} 设置的线程数，退出",threadPool.getClass().getName());
            return;
        }

        Future<Boolean> result = threadPool.submit(new Worker(taskType, pengingList, doingList,maxLevel,status_old,status_new));

    }


    class Worker implements Callable<Boolean> {
        private String taskType;
        private String  pengingList;
        private String doingList;
        private int maxLevel;
        private String status_old;
        private String status_new;


        public Worker(String taskType,String pengingList,String doingList,int maxLevel, String status_old,String status_new) {
            this.taskType = taskType;
            this.pengingList = pengingList;
            this.doingList = doingList;
            this.maxLevel = maxLevel;
            this.status_old = status_old;
            this.status_new = status_new;

        }

        public String getTask(String pengingListKey)
        {
            for(int level =0;level<maxLevel;level ++ )
            {
                List<Object> list =  redisUtil.lGet(pengingListKey + "_" + level,0,-1) ;
                if(list!=null && list.isEmpty())
                {
                    int size = list.size();
                    String json = (String) list.get(size-1);
                    return  json;
                }
            }
            return null;
        }

        @Override
        public Boolean call() throws Exception {
            String   currentTimeStr = DateUtils.getDateTime();
            DoTaskParam doTaskParam =  DoTaskParam.from(pengingList, doingList, maxLevel +"", status_old, status_new, currentTimeStr);
            Gson gson = new Gson();
            String paramMapJson = gson.toJson(doTaskParam);

            // ----------------------------------获取任务，锁住任务----------------------------------------------
            String json = redisUtil.doTask( paramMapJson);
            if(json == null || json.equals("")) {
//            logger.info("队列 {} 为空，未获得新任务",pengingList);
                return null;
            }

            PackageInfo fileInfo = gson.fromJson(json, PackageInfo.class);

            ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<>();
            paramMap.put("PACKAGE_ID", fileInfo.getPackageId());// 数据包名
            paramMap.put("NODE_ID", fileInfo.getCurNodeId());// 当前FTP节点
            paramMap.put("PACK_DIR_PATH", fileInfo.getPackDirPath());
            paramMap.put("FILE_NAME", fileInfo.getFileName() );

            String packName = fileInfo.getPackageId().split("\\.")[0];
            String packageId = fileInfo.getPackageId();
            int beginIndex = packageId.indexOf("PKG");
            String subStr = packageId.substring(beginIndex);
            int endIndex = subStr.indexOf("#");
            packageId = subStr.substring(0,endIndex).split("\\.")[0];
            String lockKey = "ACK_" + packageId;
            RReadWriteLock readWriteLock = redissonService.getRWLock(lockKey);
            RLock lock  = readWriteLock.writeLock();
            Boolean locked = false;
            try {
                locked = lock.tryLock(1,TimeUnit.SECONDS);
                if (locked) {
                    gson = new Gson();
                    paramMapJson = gson.toJson(paramMap);
                    logger.info("{}任务开始 packageId:={},paramMap:={} ",taskType,packageId, paramMapJson);
                    if(taskType.equals("下载ACK")) {
                        parseAckService.parseAck(paramMap);
                    }
                    String level = packName.substring(packName.length() - 1);
                    redisUtil.delTask( doingList + "_" + level ,packageId);
                }
                else{
                    //logger.info(" 获得lockKey:={} 写锁 失败", lockKey);
                    //String dateTimeStr = DateUtils.dateFormat(new Date(),DateUtils.DATE_TIME_PATTERN);
                    //String level = packName.substring(packName.length() - 1);
                    //redisUtil.rePengingTask(pengingList + "_" + level ,doingList + "_" + level ,packageId  ,status_new,status_old,dateTimeStr);
                }
            } catch (Exception e) {
                logger.error("执行 packageId:={} 失败", packageId,e);
                //String dateTimeStr = DateUtils.dateFormat(new Date(),DateUtils.DATE_TIME_PATTERN);
                //String level = packName.substring(packName.length() - 1);
                //redisUtil.rePengingTask(pengingList + "_" + level ,doingList + "_" + level ,packageId  ,status_new,status_old,dateTimeStr);
            }
            finally {
                if (locked) {
                    lock.unlock();
                }
            }
            return true;
        }
    }
}