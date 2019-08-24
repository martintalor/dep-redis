package com.iflytek.dep.admin.model;

import java.util.Date;

/**
 * JOB配置信息
 */
public class DicDepEtlJob {

    private String jobId;

    private String jobName;

    private Date createTime;

    private String scheduled;//是否定时任务(0-不是定时；1-定时)

    private String scheduledCron;

    private long  fixDelay;

    private String scheduledParam;

    public long getFixDelay() {
        return fixDelay;
    }

    public void setFixDelay(long fixDelay) {
        this.fixDelay = fixDelay;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getScheduled() {
        return scheduled;
    }

    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }

    public String getScheduledCron() {
        return scheduledCron;
    }

    public void setScheduledCron(String scheduledCron) {
        this.scheduledCron = scheduledCron;
    }

    public String getScheduledParam() {
        return scheduledParam;
    }

    public void setScheduledParam(String scheduledParam) {
        this.scheduledParam = scheduledParam;
    }
}