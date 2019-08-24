package com.iflytek.dep.server.redis;

import java.util.List;

public class DoTaskParam{
    String pengingKey;
    String doingKey;
    String level;
    String status_old;
    String status_new;
    String time;

    private DoTaskParam (){

    }

    public String getPengingKey() { return pengingKey; }

    public void setPengingKey(String pengingKey) { this.pengingKey = pengingKey; }

    public String getDoingKey() { return doingKey; }

    public void setDoingKey(String doingKey) { this.doingKey = doingKey; }

    public String getLevel() { return level; }

    public void setLevel(String level) { this.level = level; }

    public String getStatus_old() { return status_old; }

    public void setStatus_old(String status_old) { this.status_old = status_old; }

    public String getStatus_new() { return status_new; }

    public void setStatus_new(String status_new) { this.status_new = status_new; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public static  DoTaskParam from(String pengingKey,String doingKey,String level,String status_old,String status_new,String time){
        DoTaskParam doTaskParam = new DoTaskParam();
        doTaskParam.setPengingKey(pengingKey);
        doTaskParam.setDoingKey(doingKey);
        doTaskParam.setLevel(level);
        doTaskParam.setStatus_old(status_old);
        doTaskParam.setStatus_new(status_new);
        doTaskParam.setTime(time);
        return doTaskParam;
    }
}