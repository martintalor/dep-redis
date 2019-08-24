package com.iflytek.dep.server.model;

import io.swagger.models.auth.In;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ca加密解密当前状态记录表
 *
 * @author wcyong
 *
 * @date 2019-06-14
 */
public class CaStepRecorders {
    /**
     * 创建时间
     */
    private String bizSn;

    /**
     * 回调后的异步执行状态
     */
    private BigDecimal executeStatus;

    /**
     * 创建时间
     */
    private String packageId;

    /**
     * 创建时间
     */
    private String fileUrl;

    /**
     * 创建时间
     */
    private String efsUrl;

    /**
     * 创建时间
     */
    private String callbackUrl;

    /**
     * 创建时间
     */
    private String mode;

    /**
     * 创建时间
     */
    private BigDecimal callStatus;

    /**
     * 创建时间
     */
    private String backStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    private String containerName;

    private String receiver;

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getBizSn() {
        return bizSn;
    }

    public void setBizSn(String bizSn) {
        this.bizSn = bizSn;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getEfsUrl() {
        return efsUrl;
    }

    public void setEfsUrl(String efsUrl) {
        this.efsUrl = efsUrl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public BigDecimal getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(BigDecimal callStatus) {
        this.callStatus = callStatus;
    }

    public String getBackStatus() {
        return backStatus;
    }

    public void setBackStatus(String backStatus) {
        this.backStatus = backStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(BigDecimal executeStatus) {
        this.executeStatus = executeStatus;
    }


}