package com.iflytek.dep.admin.model.dto;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.dto
 * @Description:
 * @date 2019/2/27--19:19
 */
public class DEPServerDto {

    private String depServerRemark;

    private String depServerIp;

    private int currentPageNo;

    private int pageSize;

    public String getDepServerRemark() {
        return depServerRemark;
    }

    public void setDepServerRemark(String depServerRemark) {
        this.depServerRemark = depServerRemark;
    }

    public String getDepServerIp() {
        return depServerIp;
    }

    public void setDepServerIp(String depServerIp) {
        this.depServerIp = depServerIp;
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