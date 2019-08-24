package com.iflytek.dep.admin.model.dto.monitor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iflytek.dep.admin.model.dto.BaseDto;

import java.util.Date;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.dto.monitor
 * @Description:
 * @date 2019/7/22--18:48
 */
public class MonitorEtlDto extends BaseDto {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    private Date endTime;

    private String scheduled;

    private String jobId;

    public MonitorEtlDto(){}

    public MonitorEtlDto(Date startTime, Date endTime, String scheduled, String jobId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.scheduled = scheduled;
        this.jobId = jobId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getScheduled() {
        return scheduled;
    }

    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}