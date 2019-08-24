package com.iflytek.dep.server.redis;

import com.google.gson.Gson;
import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.server.ca.AcTaskStatus;
import com.iflytek.dep.server.constants.RedisQueueType;
import com.iflytek.dep.server.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Service
public class RePengAc {

    private Logger logger = LoggerFactory.getLogger(RePengAc.class);

    @Autowired
    private RedisUtil redisUtil;


    @Value("${maxExecuteTimeByMinute}")
    //单位：分钟
    private long maxExecuteTimeByMinute;

    public void rePendingScheduled( )
    {
        rePendingScheduled( RedisQueueType.PENG_CA.getCode(),RedisQueueType.DOING_CA.getCode(), AcTaskStatus.CA_DOING.getName(),AcTaskStatus.CA_PENGING.getName());

    }

    private  void rePendingScheduled(String pengingList,String doingList,String  current_status,String new_status) {
        List<String> list = new ArrayList<>();
        list.add(pengingList);
        list.add(doingList);
        list.add(current_status);
        list.add(new_status);

        try{
            rePendingScheduled(list);
        } catch (Exception e){
            logger.error("[{}],error:{}",this.getClass().getName(), e);
        }
    }

    private void rePendingScheduled(List<String> list )
    {
        String pengingKey = list.get(0);
        String doingKey = list.get(1);
        String status_old = list.get(2);
        String status_new = list.get(3);
        logger.debug("{}进入rePendingScheduled方法",Thread.currentThread().getName());

        for(int level = 0; level < 10; level++){
            Map<Object, Object> map = redisUtil.hmget(doingKey + "_" + level);
            for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
//                String key = (String) entry.getKey();
                String json = (String) entry.getValue();
                Gson gson = new Gson();
                PackageInfo fileInfo = gson.fromJson(json, PackageInfo.class);
                String packageId = fileInfo.getPackageId();
//                String nodeId = fileInfo.getCurNodeId();
                String status = fileInfo.getStatus();
                long currentTime = System.currentTimeMillis();
                String time = fileInfo.getTime();
                long oldTimeSte = currentTime;

                try {
                    oldTimeSte = DateUtils.dateParse(time,DateUtils.DATE_TIME_PATTERN).getTime();
                } catch (ParseException e) {
                    logger.error("[{}],error:{}",this.getClass().getName(), e);
                }

                long dTime = currentTime - oldTimeSte;

                //任务开始后半小时还没完成
                if(status.equals(status_old) && ((dTime/1000) > maxExecuteTimeByMinute)){
                    try {
                        String dateTimeStr = DateUtils.dateFormat(new Date(currentTime),DateUtils.DATE_TIME_PATTERN);
                        if(redisUtil.hasKey(packageId)==false)
                        {
                            String ret = redisUtil.rePengingTaskAc(pengingKey + "_" +  level ,doingKey + "_" +  level,packageId ,status_old,status_new,dateTimeStr);
                            logger.info("[{}]--执行rePengingTask返回 ret:{},json:{}",Thread.currentThread().getName(),ret,json);
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("将超时的任务重新放入doing队列 失败",e);
                    }
                }
            }
        }
    }
}
