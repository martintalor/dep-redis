package com.iflytek.dep.server.model;


public class CACallDto {

    private String api_key;
    private String api_secret;
    private String api_sign;

    private Efs efs;

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getApi_secret() {
        return api_secret;
    }

    public void setApi_secret(String api_secret) {
        this.api_secret = api_secret;
    }

    public String getApi_sign() {
        return api_sign;
    }

    public void setApi_sign(String api_sign) {
        this.api_sign = api_sign;
    }

    public Efs getEfs() {
        return efs;
    }

    public void setEfs(Efs efs) {
        this.efs = efs;
    }
}
