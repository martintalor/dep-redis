package com.iflytek.dep.admin.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description: FileVo
 * @date 2019/3/6--14:47
 */
public class FileVo {

    private int id;
    private String name;
    private long size;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModified;

    public FileVo() {
      
    }

    public FileVo(int id, String name, long size, Date lastModified) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}