package com.iflytek.dep.admin.aop;


import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.iflytek.dep.common.exception.BusinessErrorException;

@Configuration
@Aspect
public class APIProxy {

	private final static Logger logger = LoggerFactory.getLogger(APIProxy.class);

	// 切面应用范围是在com.iflytek.dep.controller下面所有类的所有方法
	@Around("execution(* com.iflytek.dep.*.controller..*.*(..))")
	public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

		Signature signature = proceedingJoinPoint.getSignature();
		String args = Arrays.toString(proceedingJoinPoint.getArgs());

		long start = System.currentTimeMillis();
		try {
			return proceedingJoinPoint.proceed();
		} catch (Exception e) {
			if (e instanceof BusinessErrorException) {
				logger.warn(e.getMessage());
			}
			if(logger.isDebugEnabled()){
				e.printStackTrace();
			}
			logger.error(String.format("method:%s call failed  parameter input:%s", signature, args), e);
			throw e;
		} finally {
			long last = System.currentTimeMillis();
			if ((last - start) / 1000 > 5) {
				logger.error(String.format("method:%s  parameter input:%s carry_out_time:%s ms is to long", signature, args,
						System.currentTimeMillis() - start));
			} else {
				logger.info(String.format("method:%s  parameter input:%s carry_out_time:%s ms", signature, args,
						System.currentTimeMillis() - start));
			}

		}
	}
}
