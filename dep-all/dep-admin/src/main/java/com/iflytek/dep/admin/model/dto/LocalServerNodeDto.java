package com.iflytek.dep.admin.model.dto;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.dto
 * @Description: 逻辑节点dto
 * @date 2019/2/25--15:45
 */
public class LocalServerNodeDto {


    private String serverNodeId;

    private String serverNodeName;

    private int currentPageNo;

    private int pageSize;

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId;
    }

    public String getServerNodeName() {
        return serverNodeName;
    }

    public void setServerNodeName(String serverNodeName) {
        this.serverNodeName = serverNodeName;
    }

    public int getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(int currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}