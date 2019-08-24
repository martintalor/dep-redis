package com.iflytek.dep.admin.model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description: DirectoryVo
 * @date 2019/3/6--14:38
 */
public class DirectoryVo implements Serializable {
    private String id;
    private String label;
    private int sort;
    private String dir;
    private List<DirectoryVo> children = new ArrayList<>();

    public DirectoryVo() {
    }

    public DirectoryVo(String id, String label, int sort, String dir) {
        this.id = id;
        this.label = label;
        this.sort = sort;
        this.dir = dir;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public List<DirectoryVo> getChildren() {
        return children;
    }

    public void setChildren(List<DirectoryVo> children) {
        this.children = children;
    }
}