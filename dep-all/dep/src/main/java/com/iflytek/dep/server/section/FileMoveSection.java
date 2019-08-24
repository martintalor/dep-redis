package com.iflytek.dep.server.section;

import com.google.gson.Gson;
import com.iflytek.dep.server.constants.PkgStatus;
import com.iflytek.dep.server.ftp.core.FtpClientTemplate;
import com.iflytek.dep.server.mapper.FTPConfigMapper;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.mapper.SectionStepRecordersMapper;
import com.iflytek.dep.server.model.FTPConfig;
import com.iflytek.dep.server.service.threadPool.DepServerFtpFileBackService;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.FileUtil;
import com.iflytek.dep.server.utils.SectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FileMoveSection implements Section, Status {
	private static Logger logger = LoggerFactory.getLogger(FileMoveSection.class);
	@Autowired
	private Environment environment;
	@Autowired
	FTPConfigMapper fTPConfigMapper;

	@Autowired
	DepServerFtpFileBackService depServerFtpFileBackService;

	@Autowired
	SectionStepRecordersMapper sectionStepRecordersMapper;

	@Autowired
	NodeAppBeanMapper nodeAppBeanMapper;

	@Autowired
	RestTemplate restTemplate;
	@Autowired
	SectionUtils sectionUtils;

	@Override
	public void doAct(final String pkgId, String jobId, SectionNode sectionNode, BigDecimal totalSectionNumber, ConcurrentHashMap<String, Object> map) throws Exception {
		logger.info("[{}] job has [{}],pkgId id [{}] in method ,jobId is [{}]",this.getClass(),Thread.currentThread().getName(),pkgId,jobId);
		sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));

		SectionResult result = new SectionResult(true);
		result.setMap(map);

		String downFtpNodeId = (String) map.get("DOWN_FTP_NODE_ID");

		FTPConfig fTPConfig = fTPConfigMapper.selectByNodeId(downFtpNodeId);

//			String curNodeId = (String) map.get("CUR_NODE_ID");
		List<ConcurrentHashMap<String, Object>> param = (List<ConcurrentHashMap<String, Object>>) map.get("PARAM");
		FtpClientTemplate fromFtpClientTemplate = FtpClientTemplate.FTP_CLIENT_TEMPLATE.get(downFtpNodeId);
		Gson gson = new Gson();
		for (Object item : param) {
			ConcurrentHashMap<String, Object> s = gson.fromJson(gson.toJson(item), ConcurrentHashMap.class);

			String packageId = (String) s.get("PACKAGE_ID");

			try {
				//截取掉包名后的zip或者z01等后缀
				packageId= packageId.split(CommonConstants.NAME.PACKAGE_FIX)[0];
				if (packageId.startsWith("PKG")) {
					fromFtpClientTemplate.moveFile(fTPConfig.getDataPackageFolderDown() + packageId+CommonConstants.NAME.ZIP, environment.getProperty("pkg.back.path") + environment.getProperty("node.id") + "/" + FileUtil.getTodayString() + "/", "BACK_" + packageId);
				} else {
//					fromFtpClientTemplate.moveFile(fTPConfig.getDataPackageFolderDown() +packageId,environment.getProperty("ack.back.path")+FileUtil.getTodayString(),"BACK_"+packageId);
				}

				if(!Boolean.valueOf( environment.getProperty("is.center"))){
					List<String> subPackageList = sectionStepRecordersMapper.listPackageSub(packageId+CommonConstants.NAME.ZIP);
					for (String subItem : subPackageList) {
						fromFtpClientTemplate.moveFile(fTPConfig.getDataPackageFolderDown() + subItem, environment.getProperty("pkg.back.path") + environment.getProperty("node.id") + "/" + FileUtil.getTodayString() + "/", "BACK_" + subItem);
					}
				}
			} catch (Exception e) {
				logger.error("[{}] happend error [{}] ",this,getClass(),e);
			}
		}

		sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
		SectionNode nextSectionNode = sectionNode.getNext();
		if(nextSectionNode!= null){
			nextSectionNode.getCurrent().doAct(pkgId,jobId,nextSectionNode,totalSectionNumber,map);
		}
	}


	@Override
	public void update(String pkgId, PkgStatus status) {

	}
}
