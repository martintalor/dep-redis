package com.iflytek.dep.admin.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.iflytek.dep.admin.utils.CommonConstants;
import com.iflytek.dep.common.exception.BusinessErrorException;
import com.iflytek.dep.common.utils.ResponseBean;

/**
 * 全局异常统一处理类
 *
 * @author lli
 *
 * @version 1.0
 */
@ControllerAdvice(basePackages = {"com.iflytek.dep.admin.controller"})
public class GlobalExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessErrorException.class)
//    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseBean<String> handleBusinessError(BusinessErrorException ex) {
    	logger.error("业务异常:",ex);
        return new ResponseBean<String>(ex.getCode(), ex.getMessage());
    }

    /**
     * @description 参数校验异常处理
     * @author zyzhang15
     * @create 2017年8月23日上午9:33:11
     * @version 1.0
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseBean<Void> MyMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return new ResponseBean<Void>(CommonConstants.RESPONSE_INFO.FAILURE, bindingResult.getFieldError().getDefaultMessage());

    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseBean<String> handleError(Exception ex) {
    	logger.error("内部错误",ex);
        return new ResponseBean<String>(CommonConstants.RESPONSE_INFO.FAILURE, CommonConstants.RESPONSE_INFO.ERROR_MESSAGE);
    }
}
