package com.iflytek.dep.admin.model.dto;

/**
 * Created by xiliu5 on 2019/2/27.
 */
public class BaseDto {
    private int currentPageNo = 1;
    private int pageSize = 10;

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
