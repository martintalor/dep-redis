package com.iflytek.dep.server.utils;

import com.iflytek.dep.server.section.SectionNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogSectionInfo {
	private static Logger logger = LoggerFactory.getLogger(LogSectionInfo.class);
	public static void loggerSectionChain(SectionNode sectionNode){

		SectionNode temp = sectionNode;
		while(temp!=null){
			logger.info(" -> chain {}",temp.getCurrent().getClass());
			temp = temp.getNext();
		}

	}
}
