package com.iflytek.dep.server.redis;

import com.google.gson.Gson;
import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.server.ca.AcTaskStatus;
import com.iflytek.dep.server.ca.DoCallBackServiceImpl;
import com.iflytek.dep.server.config.redis.DepThreadFactory;
import com.iflytek.dep.server.constants.RedisQueueType;
import com.iflytek.dep.server.file.CAService;
import com.iflytek.dep.server.mapper.CaStepRecordersMapper;
import com.iflytek.dep.server.model.CaStepRecorders;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.FileConfigUtil;
import com.iflytek.dep.server.utils.FileUtil;
import com.iflytek.dep.server.utils.RedisUtil;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.*;


@Component
@EnableScheduling
public class UpAndDownTaskCA extends UpAndDownTask {

    private Logger logger = LoggerFactory.getLogger(UpAndDownTaskCA.class);


    String pengingList_ac = RedisQueueType.PENG_CA.getCode();
    String doingList_ac = RedisQueueType.DOING_CA.getCode();

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonService redissonService;

    @Autowired
    CaStepRecordersMapper caStepRecordersMapper;
    @Autowired
    DoCallBackServiceImpl doCallBackServiceImpl;

    public UpAndDownTaskCA() {
        if (threadPool == null) {
            BlockingQueue<Runnable> bq = new ArrayBlockingQueue<Runnable>(1);
            threadPool = new ThreadPoolExecutor(1, threadNumber, 50, TimeUnit.MILLISECONDS, bq,new DepThreadFactory("redis-ac-task") {});
        }
    }

    @Scheduled(cron = "0/1 * * * * *")
    public void caCallBackTaskListScheduled() {
        try {
            taskScheduled("CA回调", pengingList_ac, doingList_ac, 10, AcTaskStatus.CA_PENGING.getName(), AcTaskStatus.CA_DOING.getName(), threadPool);
        } catch (Exception e) {
            //logger.error("caCallBackTaskListScheduled----error >>>[{}]", e);
        }
    }


    public void taskScheduled(String taskType, String pengingList, String doingList, int maxLevel, String status_old, String status_new, ThreadPoolExecutor threadPool) throws Exception {
        int queueSize = threadPool.getQueue().size();
        int activeCount = threadPool.getActiveCount();
        if ((queueSize + activeCount) > threadNumber) {
            logger.debug("超过 {} 设置的线程数，退出", threadPool.getClass().getName());
            return;
        }
        Future<Boolean> result = threadPool.submit(new Worker(pengingList, doingList, maxLevel, status_old, status_new));
    }

    class Worker implements Callable<Boolean> {
        private String pengingList;
        private String doingList;
        private String status_old;
        private String status_new;
        private int maxLevel;


        public Worker(String pengingList, String doingList, int maxLevel, String status_old, String status_new) {
            this.pengingList = pengingList;
            this.doingList = doingList;
            this.maxLevel = maxLevel;
            this.status_old = status_old;
            this.status_new = status_new;

        }

        @Override
        public Boolean call() throws Exception {

            String currentTimeStr = DateUtils.getDateTime();
            DoTaskParam doTaskParam = DoTaskParam.from(pengingList, doingList, maxLevel + "", status_old, status_new, currentTimeStr);
            Gson gson = new Gson();
            String paramMapJson = gson.toJson(doTaskParam);

            // ----------------------------------获取任务，锁住任务----------------------------------------------
            String json = redisUtil.doAcTask(paramMapJson);
            if (json == null || json.equals("")) {
                return null;
            }
            gson = new Gson();
            CaStepRecorders item = gson.fromJson(json, CaStepRecorders.class);
            String biz_sn = item.getBizSn();
            CaStepRecorders caStepRecorders = caStepRecordersMapper.selectByPrimaryKey(biz_sn);

            BigDecimal callStatus = caStepRecorders.getCallStatus();
            String backStatus = caStepRecorders.getBackStatus();
            BigDecimal executeStatus = caStepRecorders.getExecuteStatus();

            String packageId1 = item.getPackageId().split("\\.")[0];

            //执行回调处理
            String mode = item.getMode();
            String packageId = item.getPackageId();

            String packName = packageId.split("\\.")[0];
            String level = packName.substring(packName.length() - 1);

            RReadWriteLock readWriteLock = redissonService.getRWLock(packageId);
            RLock lock = readWriteLock.writeLock();
            Boolean locked = false;
            try {
                locked = lock.tryLock(1, TimeUnit.SECONDS);
                if (locked) {
                    logger.info("{}任务开始 packageId:={},biz_sn:={} ", packageId, biz_sn);

                    CaStepRecorders caStepRecorders2 = caStepRecordersMapper.selectByPrimaryKey(biz_sn);
                    BigDecimal executeStatus2 = caStepRecorders2.getExecuteStatus();

                    if (executeStatus2 != null && executeStatus2.intValue() == 0) {
                        doCallBackServiceImpl.doCallBackTask(biz_sn);
                        redisUtil.delTask(doingList + "_" + level, packageId);
                    }
                    else{
                        redisUtil.delTask(doingList + "_" + level, packageId);
                    }
                }
                //else{
                //    logger.info(" 获得packageId:={} 写锁 失败", packageId);
                //    String dateTimeStr = DateUtils.dateFormat(new Date(),DateUtils.DATE_TIME_PATTERN);
                //    redisUtil.rePengingTaskAc(pengingList + "_" + level ,doingList + "_" + level ,packageId  ,status_new,status_old,dateTimeStr);
                //}
            } catch (Exception e) {
                logger.error(" 获得packageId:={} 写锁 失败", packageId, e);
                //String dateTimeStr = DateUtils.dateFormat(new Date(),DateUtils.DATE_TIME_PATTERN);
                //redisUtil.rePengingTaskAc(pengingList + "_" + level ,doingList + "_" + level ,packageId  ,status_new,status_old,dateTimeStr);
            } finally {
                if (locked) {
                    lock.unlock();
                }
            }
            return true;
        }

        private void retryCallCa(String packageId, String biz_sn, String source, String dest) {

            //生成receiver
            String receiver = FileUtil.parsingReceiver(packageId);

            String appcode = FileConfigUtil.APPCODE;
            String apppwd = FileConfigUtil.APPPWD;
            //ca接口地址
            String url = FileConfigUtil.CAURL;
            //ca回调接口地址
            String callBackUrl = FileConfigUtil.CACALLBACKURL;

            try {
                String callStatus2 = CAService.decrypt(appcode, apppwd, url, source, dest, callBackUrl, biz_sn, FileConfigUtil.CONTAINERNAME, receiver);
                if ("0".equals(callStatus2)) {
                    CaStepRecorders caStepRecorders = caStepRecordersMapper.selectByPrimaryKey(biz_sn);
                    caStepRecorders.setBizSn(biz_sn);
                    caStepRecorders.setCallStatus(CommonConstants.CA.CALL_FAIL);
                    caStepRecordersMapper.updateByPrimaryKeySelective(caStepRecorders);
                }
                if ("1".equals(callStatus2)) {
                    CaStepRecorders caStepRecorders = caStepRecordersMapper.selectByPrimaryKey(biz_sn);
                    caStepRecorders.setBizSn(biz_sn);
                    caStepRecorders.setCallStatus(CommonConstants.CA.CALL_SUCCESS);
                    caStepRecordersMapper.updateByPrimaryKeySelective(caStepRecorders);
                }
            } catch (Exception e) {
                logger.error("", e);
                CaStepRecorders caStepRecorders = caStepRecordersMapper.selectByPrimaryKey(biz_sn);
                caStepRecorders.setBizSn(biz_sn);
                caStepRecorders.setCallStatus(CommonConstants.CA.CALL_FAIL);
                caStepRecordersMapper.updateByPrimaryKeySelective(caStepRecorders);
            }
        }

    }


}