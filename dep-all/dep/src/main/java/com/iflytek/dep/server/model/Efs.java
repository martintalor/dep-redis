package com.iflytek.dep.server.model;

public class Efs {

    private String biz_sn;
    private String file_url;
    private String efs_url;
    private String callback_url;
    private String mode;


    public String getBiz_sn() {
        return biz_sn;
    }

    public void setBiz_sn(String biz_sn) {
        this.biz_sn = biz_sn;
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

    public String getCallback_url() {
        return callback_url;
    }

    public void setCallback_url(String callback_url) {
        this.callback_url = callback_url;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
