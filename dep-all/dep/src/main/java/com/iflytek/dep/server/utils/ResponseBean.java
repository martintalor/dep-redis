package com.iflytek.dep.server.utils;

import com.iflytek.dep.common.utils.ToStringModel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.utils
 * @Description:
 * @date 2019/4/9--20:51
 */
public class ResponseBean<T> extends ToStringModel {

    private static final long serialVersionUID = -6351210629803310701L;

    /**
     * 1 成功 0 失败
     */
    private String code;

    /**
     * 错误信息
     */
    private String message;

    /***/
    private int total;
    /**
     * 业务数据
     */
    private T rows;

    public T getRows() {
        return rows;
    }

    public void setRows(T rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ResponseBean() {
        super();
        this.code = "1";
    }

    public ResponseBean(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public ResponseBean(T data) {
        super();
        this.rows = data;
        this.code = "1";
    }

    public ResponseBean(String message) {
        super();
        this.message = message;
        this.code = "0";
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     *            the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
