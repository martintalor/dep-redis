package com.iflytek.dep.admin.model;

import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model
 * @Description: job字典表
 * @date 2019/7/22--11:29
 */
public class JobType {
    private String id;
    private String name;
    private List list;

    public JobType() {
    }

    public JobType(String id, String name, List list) {
        this.id = id;
        this.name = name;
        this.list = list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}