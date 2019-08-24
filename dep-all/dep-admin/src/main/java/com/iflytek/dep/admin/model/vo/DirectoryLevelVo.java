package com.iflytek.dep.admin.model.vo;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description: DirectoryLevelVo
 * @date 2019/3/6--16:53
 */
public class DirectoryLevelVo {
    private String id;

    private String directoryName;

    private String parentId;

    private int sort;

    private String dir;

    public DirectoryLevelVo() {
    }

    public DirectoryLevelVo(String id, String directoryName, String parentId, int sort, String dir) {
        this.id = id;
        this.directoryName = directoryName;
        this.parentId = parentId;
        this.sort = sort;
        this.dir = dir;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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
}