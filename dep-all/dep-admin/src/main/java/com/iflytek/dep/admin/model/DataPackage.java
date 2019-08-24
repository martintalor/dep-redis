package com.iflytek.dep.admin.model;

import java.math.BigDecimal;
import java.util.Date;

public class DataPackage {
    private String packageId;

    private BigDecimal packageSize;

    private String appIdFrom;

    private String appIdTo;

    private String folderPath;

    private String packagePath;

    private String sendLevel;

    private Date createTime;

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId == null ? null : packageId.trim();
    }

    public BigDecimal getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(BigDecimal packageSize) {
        this.packageSize = packageSize;
    }

    public String getAppIdFrom() {
        return appIdFrom;
    }

    public void setAppIdFrom(String appIdFrom) {
        this.appIdFrom = appIdFrom == null ? null : appIdFrom.trim();
    }

    public String getAppIdTo() {
        return appIdTo;
    }

    public void setAppIdTo(String appIdTo) {
        this.appIdTo = appIdTo == null ? null : appIdTo.trim();
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath == null ? null : folderPath.trim();
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath == null ? null : packagePath.trim();
    }

    public String getSendLevel() {
        return sendLevel;
    }

    public void setSendLevel(String sendLevel) {
        this.sendLevel = sendLevel == null ? null : sendLevel.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}