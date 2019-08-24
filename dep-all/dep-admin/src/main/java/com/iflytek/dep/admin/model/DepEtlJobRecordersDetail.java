package com.iflytek.dep.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.admin.model
 * @Description:
 * @date 2019/6/12--16:49
 */
public class DepEtlJobRecordersDetail {
    private String id;
    private String packageId;
    private BigDecimal jobStatus;
    private String jobLog;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public BigDecimal getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(BigDecimal jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobLog() {
        return jobLog;
    }

    public void setJobLog(String jobLog) {
        this.jobLog = jobLog;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
