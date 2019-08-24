package com.iflytek.dep.common.exception;

/**
 * 自定义异常类
 * 
 * @author ddcai
 *
 * @version 1.0
 */
public class BusinessErrorException extends Exception {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 异常码
     */
    private String code;

    /**
     * 异常信息
     */
    private String message;

    /**
     * 构造方法
     */
    public BusinessErrorException() {
        super();
    }

    /**
     * 构造方法
     * 
     * @param code
     * @param message
     */
    public BusinessErrorException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * @param code
     */
    public BusinessErrorException(String code) {
        super(code);
        this.code = code;
    }

    /**
     * @description
     * @author wangt
     * @create 2016年11月9日下午4:32:12
     * @version 1.0
     * @return String
     */
    public String getCode() {
        return code;
    }

    /**
     * @description
     * @author wangt
     * @create 2016年11月9日下午4:32:13
     * @version 1.0
     * @param code
     *            String
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @description
     * @author wangt
     * @create 2016年11月9日下午4:32:12
     * @version 1.0
     * @return String
     */
    public String getMessage() {
        return message;
    }

    /**
     * @description
     * @author wangt
     * @create 2016年11月9日下午4:32:18
     * @version 1.0
     * @param message
     *            String
     */
    public void setMessage(String message) {
        this.message = message;
    }

}