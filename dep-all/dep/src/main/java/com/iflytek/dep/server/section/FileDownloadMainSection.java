package com.iflytek.dep.server.section;


import com.iflytek.dep.server.constants.PkgStatus;
import com.iflytek.dep.server.service.dataPack.SendPackService;
import com.iflytek.dep.server.utils.SectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Description: 数据包下载-中心节点
 * @date 2019/2/22
 */
@Service
public class FileDownloadMainSection  implements Section, Status {
	private static Logger logger = LoggerFactory.getLogger(FileDownloadMainSection.class);
	@Autowired
	SendPackService sendPackService;
	@Autowired
	SectionUtils sectionUtils;

	@Override
	public void doAct(final String packageId, String jobId, SectionNode sectionNode, BigDecimal totalSectionNumber, ConcurrentHashMap<String, Object> map) throws Exception {
		logger.info("[{}] job has [{}],package id [{}] in method ,jobId is [{}]",this.getClass(),Thread.currentThread().getName(),packageId,jobId);
		sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));

		sendPackService.downPackMain(map);

//					return SectionResult(true,map);
		sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
		SectionNode nextSectionNode = sectionNode.getNext();
		if(nextSectionNode!= null){
			nextSectionNode.getCurrent().doAct(packageId,jobId,nextSectionNode,totalSectionNumber,map);
		}
	}

	@Override
	public void update(String pkgId, PkgStatus status) {
		// update pkg status here
		
	}

}
