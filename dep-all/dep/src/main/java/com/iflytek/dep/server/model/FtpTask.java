package com.iflytek.dep.server.model;


import java.util.Date;

/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.model
 * @Description: FtpTask
 * @date 2019/2/28--19:57
 */
public class FtpTask {

    /**
     * 定时任务ID
     */
    private Integer taskId;

    /**
     * 定时任务名称
     */
    private String quartzName;
    /**
     * cron
     */
    private String cron;
    /**
     * 状态("1":有效 "0":无效)
     */
    private Integer status;
    /**
     * 定时任务类
     */
    private String schedulerClass;
    /**
     * 时间戳
     */
    private Date ts;


    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public String getSchedulerClass() {
        return schedulerClass;
    }

    public void setSchedulerClass(String schedulerClass) {
        this.schedulerClass = schedulerClass;
    }

    public String getQuartzName() {
        return quartzName;
    }

    public void setQuartzName(String quartzName) {
        this.quartzName = quartzName;
    }

    public Integer getTaskId() { return taskId; }

    public void setTaskId(Integer taskId) { this.taskId = taskId; }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}