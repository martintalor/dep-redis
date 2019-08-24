package com.iflytek.dep.server.up;

import com.iflytek.dep.server.constants.ActionType;
import com.iflytek.dep.server.constants.ExchangeNodeType;
import com.iflytek.dep.server.constants.PkgType;
import com.iflytek.dep.server.ftp.core.FtpClientTemplate;
import com.iflytek.dep.server.section.SectionNodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 构建SectionNode 启动线程任务
 *
 * @author Kevin
 */
@Service
public class PkgUploaderManager {
	@Autowired
	PkgUploader pkgUploader;

	/**
	 * 上传数据包接口
	 *
	 * @param packageId
	 * @param packageFileList
	 * @param targetFtp       数据包需要上传的目标ftp
	 */
	public void uploadPackage(ExchangeNodeType exchangeNodeType,String packageId, List<String> packageFileList, List<FtpClientTemplate> targetFtp,	ConcurrentHashMap<String, Object> param ) throws Exception {
		SectionNodeBuilder sectionNodeBuilder = new SectionNodeBuilder(PkgType.DATA, exchangeNodeType, ActionType.UP, packageId);

		final String thisPackageId = new String(packageId);
		pkgUploader.upload(thisPackageId, sectionNodeBuilder.build(), param);
	}


	/**
	 * 中心上传数据包接口
	 *
	 * @param packageId
	 * @param packageFileList
	 * @param targetFtp       数据包需要上传的目标ftp
	 */
	public void mainUploadPackage(ExchangeNodeType exchangeNodeType,String packageId, List<String> packageFileList, List<FtpClientTemplate> targetFtp,		ConcurrentHashMap<String, Object> param ) throws Exception {
		SectionNodeBuilder sectionNodeBuilder = new SectionNodeBuilder(PkgType.MAINDATA, exchangeNodeType, ActionType.UP, packageId);

		final String thisPackageId = new String(packageId);
		pkgUploader.upload(thisPackageId, sectionNodeBuilder.build(), param);
	}

	/**
	 * 上传Ack接口
	 *
	 * @param packageId
	 * @param packageFileList
	 * @param targetFtp       数据包需要上传的目标ftp
	 */
	public void uploadAck(ExchangeNodeType exchangeNodeType, String packageId, List<String> packageFileList, List<FtpClientTemplate> targetFtp,ConcurrentHashMap<String, Object> param) throws Exception {

		SectionNodeBuilder sectionNodeBuilder = new SectionNodeBuilder(PkgType.ACK, exchangeNodeType, ActionType.UP, packageId);

	 	final String thisPackageId = new String(packageId);
		pkgUploader.upload(thisPackageId, sectionNodeBuilder.build(), param);

	}

}
