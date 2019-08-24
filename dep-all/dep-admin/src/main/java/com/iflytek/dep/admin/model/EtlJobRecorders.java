package com.iflytek.dep.admin.model;

import java.util.Date;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model
 * @Description:
 * @String 2019/7/23--11:49
 */
public class EtlJobRecorders {
    private String id;

    private String jobId;

    private String jobName;

    private String jobType;

    private Date startTime;

    private Date endTime;

    private String jobStatus;

    private String preJobKey;

    private String currJobKey;

    private String isEmptyElement;

    private String packageId;

    private String jobParam;

    private String jobResultInfo;

    private Date destEndTime;

    private String destCallbackInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
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

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getPreJobKey() {
        return preJobKey;
    }

    public void setPreJobKey(String preJobKey) {
        this.preJobKey = preJobKey;
    }

    public String getCurrJobKey() {
        return currJobKey;
    }

    public void setCurrJobKey(String currJobKey) {
        this.currJobKey = currJobKey;
    }

    public String getIsEmptyElement() {
        return isEmptyElement;
    }

    public void setIsEmptyElement(String isEmptyElement) {
        this.isEmptyElement = isEmptyElement;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getJobParam() {
        return jobParam;
    }

    public void setJobParam(String jobParam) {
        this.jobParam = jobParam;
    }

    public String getJobResultInfo() {
        return jobResultInfo;
    }

    public void setJobResultInfo(String jobResultInfo) {
        this.jobResultInfo = jobResultInfo;
    }

    public Date getDestEndTime() {
        return destEndTime;
    }

    public void setDestEndTime(Date destEndTime) {
        this.destEndTime = destEndTime;
    }

    public String getDestCallbackInfo() {
        return destCallbackInfo;
    }

    public void setDestCallbackInfo(String destCallbackInfo) {
        this.destCallbackInfo = destCallbackInfo;
    }
}