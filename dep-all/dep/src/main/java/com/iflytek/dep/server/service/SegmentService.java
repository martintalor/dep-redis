package com.iflytek.dep.server.service;


import java.util.concurrent.ConcurrentHashMap;

/**
 *各模块对外统一接口
 */
public interface SegmentService {
	void doJob(ConcurrentHashMap<String,Object> map) throws Exception;
	void next(ConcurrentHashMap<String,Object> map) throws Exception;
}
