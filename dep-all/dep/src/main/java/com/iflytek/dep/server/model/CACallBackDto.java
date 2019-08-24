package com.iflytek.dep.server.model;

import com.iflytek.dep.server.redis.PackageInfo;

import java.util.Map;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.model
 * @Description:
 * @date 2019/6/14--14:13
 */
public class CACallBackDto {
    private String biz_sn;
    private String status;
    private String mode;

    private String file_url;
    private String efs_url;

    private CACallBackDto()
    {

    }
    public String getBiz_sn() {
        return biz_sn;
    }

    public void setBiz_sn(String biz_sn) {
        this.biz_sn = biz_sn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getEfs_url() {
        return efs_url;
    }

    public void setEfs_url(String efs_url) {
        this.efs_url = efs_url;
    }


}
