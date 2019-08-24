package com.iflytek.dep.common.scheduled;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;

/**
 * @author dzr
 * @version V1.0
 * @Package com.iflytek.dep.common.ftp
 * @Description:
 * @date 2019/2/7--10:00
 */
public class QuartzTrigger implements Trigger {

    private String cron;

    public QuartzTrigger(String cron) {
        super();
        this.cron = cron;
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        if (StringUtils.isBlank(cron)) {
            return null;
        }
        // 定时任务触发，可修改定时任务的执行周期
        CronTrigger trigger = new CronTrigger(cron);
        Date nextExecDate = trigger.nextExecutionTime(triggerContext);
        return nextExecDate;
    }

}