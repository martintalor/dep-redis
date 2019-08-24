package com.iflytek.dep.admin.model.vo;


import java.util.List;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description: 分页查询返回
 * @date 2019/2/25--16:27
 */
public class PageVo<T> {

    private int currentPageNo;

    private int pageSize;

    private int total;

    private List<T> list;

    public PageVo() {
    }

    public PageVo(int currentPageNo, int pageSize, int total, List<T> list) {
        this.currentPageNo = currentPageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}