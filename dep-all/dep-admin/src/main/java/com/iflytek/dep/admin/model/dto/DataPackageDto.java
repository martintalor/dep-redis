package com.iflytek.dep.admin.model.dto;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.dto
 * @Description: DataPackageDto
 * @date 2019/2/28--20:21
 */
public class DataPackageDto extends BaseDto {

    private String packageId;

    private String createTimeStart;

    private String createTimeEnd;

    private String globalStateDm;

    private String appIdTo;

    public String getAppIdTo() {
        return appIdTo;
    }

    public void setAppIdTo(String appIdTo) {
        this.appIdTo = appIdTo;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getGlobalStateDm() {
        return globalStateDm;
    }

    public void setGlobalStateDm(String globalStateDm) {
        this.globalStateDm = globalStateDm;
    }
}