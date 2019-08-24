package com.iflytek.dep.common.utils;


import com.iflytek.dep.common.scheduled.QuartzScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dzr
 * @version V1.0
 * @Package com.iflytek.dep.common.utils
 * @Description: 定时任务工厂类
 * @date 2019/2/7--10:00
 */
public class ScheduledFutureFactory {

    private static Map<String, QuartzScheduler> map = new HashMap<>(0);

    /**
     * 获取定时任务实例
     *
     * @param ftpId
     * @param runnable
     * @param cron
     * @param threadPoolTaskScheduler
     * @return
     */
    public static QuartzScheduler createQuartzScheduler(String ftpId, Runnable runnable, String cron,
                                                        ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        QuartzScheduler quartzScheduler = new QuartzScheduler(runnable, cron, threadPoolTaskScheduler);
        map.put(ftpId, quartzScheduler);
        return quartzScheduler;
    }

    /**
     * 根据key获取定时任务实例
     *
     * @param ftpId
     * @return
     */
    public static QuartzScheduler getQuartzScheduler(String ftpId) {
        return map.get(ftpId);
    }


}