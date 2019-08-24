package com.iflytek.dep.server.service;

import com.iflytek.dep.server.constants.ExchangeNodeType;
import com.iflytek.dep.server.mapper.SectionStepRecordersMapper;
import com.iflytek.dep.server.model.SectionStepRecorderInfo;
import com.iflytek.dep.server.up.PkgUploaderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统启动调用类
 */
@Component
public class ContextStartListener implements ApplicationListener<ApplicationReadyEvent> {
	@Autowired
	SectionStepRecordersMapper sectionStepRecordersMapper;

	@Autowired
	PkgUploaderManager pkgUploaderManager;

	private static Logger logger = LoggerFactory.getLogger(ContextStartListener.class);

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		logger.info("ApplicationReadyEvent applicationReadyEvent success");

		//todo redis上的任务是持久化的，关闭redis不会导致里面的任务丢失，所以不需要依赖数据库的记录

//		List<SectionStepRecorderInfo> list = sectionStepRecordersMapper.getAllUnSuccessPakcage();
//
//		for (SectionStepRecorderInfo item:list
//			 ) {
//			logger.info("packageid [{}]chain unsuccess ,will retry...",item.getPackageId());
//
//			pkgUploaderManager.uploadPackage(ExchangeNodeType.LEAF, item.getPackageId(), null, null, new ConcurrentHashMap<>());
//
//		}

	}
}
