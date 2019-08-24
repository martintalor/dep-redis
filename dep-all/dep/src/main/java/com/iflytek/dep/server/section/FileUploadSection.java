package com.iflytek.dep.server.section;


import com.iflytek.dep.common.exception.BusinessErrorException;
import com.iflytek.dep.server.constants.ExceptionState;
import com.iflytek.dep.server.constants.PkgStatus;
import com.iflytek.dep.server.service.dataPack.SendPackService;
import com.iflytek.dep.server.utils.SectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Description: 数据包上传
 * @date 2019/2/22
 */
@Service
public class FileUploadSection  implements Section, Status {
	private static Logger logger = LoggerFactory.getLogger(FileUploadSection.class);
	private static ExecutorService fixedUpStreamThreadPool;
	public static AtomicInteger threadJobSize = new AtomicInteger(0);
	@Autowired
	SectionUtils sectionUtils;
	@Autowired
	SendPackService sendPackService;


	@Override
	public void doAct(final String pkgId, String jobId, SectionNode sectionNode, BigDecimal totalSectionNumber, ConcurrentHashMap<String, Object> map) throws Exception {
		logger.info("[{}] job has [{}],pkgId id [{}] in method ,jobId is [{}]",this.getClass(),Thread.currentThread().getName(),pkgId,jobId);
		sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));

		update("pkgid", PkgStatus.COMPRSS_BEFORE);
		// up pkg here

		sendPackService.upPackList(map);

		String UP_ALL_FLAG = (String) map.get("UP_ALL_FLAG");

		boolean isValid = true;

		// 如果有一个包上传失败了
		if ("FALSE".equals(UP_ALL_FLAG) ) {
			isValid = false;
			System.out.println("UP_ALL_FLAG:" + UP_ALL_FLAG);
		}

		update("pkgid", PkgStatus.COMPRESS_AFTER);
//					return SectionResult(isValid,map);
		if(isValid){
			sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
			SectionNode nextSectionNode = sectionNode.getNext();
			if(nextSectionNode!= null){
				nextSectionNode.getCurrent().doAct(pkgId,jobId,nextSectionNode,totalSectionNumber,map);
			}
		}
		else{
			throw new BusinessErrorException(ExceptionState.UP.getCode(),ExceptionState.UP.getName() + "上传失败！pkgId:"+ pkgId);
		}
	}

	@Override
	public void update(String pkgId, PkgStatus status) {
		// update pkg status here
		
	}

}
