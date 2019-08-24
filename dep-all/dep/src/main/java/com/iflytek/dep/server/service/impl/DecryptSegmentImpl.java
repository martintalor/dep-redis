package com.iflytek.dep.server.service.impl;

import com.iflytek.dep.server.service.SegmentService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 解密模块
 */
@Service
@Scope("prototype")
public class DecryptSegmentImpl implements SegmentService {

	@Override
	public void doJob(ConcurrentHashMap<String, Object> map) throws Exception {

	}

	@Override
	public void next(ConcurrentHashMap<String, Object> map) throws Exception {

	}
}
