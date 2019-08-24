package com.iflytek.dep.admin.model;

import java.math.BigDecimal;
import java.util.Date;

public class DataPackageSub {
    private String subPackageId;

    private String packageId;

    private BigDecimal packageSize;

    private Date createTime;

    public String getSubPackageId() {
        return subPackageId;
    }

    public void setSubPackageId(String subPackageId) {
        this.subPackageId = subPackageId == null ? null : subPackageId.trim();
    }

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}