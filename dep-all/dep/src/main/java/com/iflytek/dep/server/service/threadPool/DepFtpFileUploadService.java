package com.iflytek.dep.server.service.threadPool;

import com.iflytek.dep.common.exception.BusinessErrorException;
import com.iflytek.dep.server.constants.ExceptionState;
import com.iflytek.dep.server.constants.RecvSendStateEnum;
import com.iflytek.dep.server.file.FileServiceImpl;
import com.iflytek.dep.server.file.ZipServiceImpl;
import com.iflytek.dep.server.ftp.core.FtpClientTemplate;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.mapper.NodeLinkBeanMapper;
import com.iflytek.dep.server.model.FTPConfig;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.model.NodeLinkBean;
import com.iflytek.dep.server.service.dataPack.SendAckService;
import com.iflytek.dep.server.service.dataPack.UpStatusService;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.ConcurrentCache;
import com.iflytek.dep.server.utils.FileConfigUtil;
import com.iflytek.dep.server.utils.PackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.*;

@Service
public class DepFtpFileUploadService {
	@Autowired
	UpStatusService upStatusService;

	@Autowired
	NodeAppBeanMapper nodeAppBeanMapper;

	@Autowired
	NodeLinkBeanMapper linkBeanMapper;

	@Autowired
	FileServiceImpl fileService;

	@Autowired
	ZipServiceImpl zipService;

	@Autowired
	SendAckService sendAckService;

	private final static Logger logger = LoggerFactory.getLogger(DepFtpFileUploadService.class);


	class DepThreadFactory implements ThreadFactory {

		private int counter;
		private String name;

		public DepThreadFactory(String name) {
			counter = 0;
			this.name = name;
		}

		@Override
		public Thread newThread(Runnable run) {
			Thread t = new Thread(run, name + "-Thread-" + counter);
			counter++;
			return t;
		}
	}

	ExecutorService fixedFtpFileUpLoadThreadPool = Executors.newFixedThreadPool(10,
			new DepFtpFileUploadService.DepThreadFactory("ftp file upload thread"));

	public void upPackThread(ConcurrentHashMap<String, Object> mapTemp, CountDownLatch countDownLatch, ConcurrentHashMap<String, Object> resultMap) throws Exception {

		final Future<?> fu = fixedFtpFileUpLoadThreadPool.submit(new Runnable() {

			public void run() {
				String packageId = "";
				String toNodeId = "";
				String curNodeId = "";

				try {
					packageId = (String) mapTemp.get("PACKAGE_ID");// 数据包名
					toNodeId = (String) mapTemp.get("TO_NODE_ID");// 目标节点
					curNodeId = (String) mapTemp.get("NODE_ID");// 当前节点

					// 调用上传方法
					upPack(mapTemp);

					logger.info("线程启用，packageId：" + packageId);

				} catch (Exception e) {
					resultMap.put("UP_ALL_FLAG","FALSE");
					resultMap.put("UP_ERROR_MSG",e.getMessage());
					resultMap.put("UP_PACKAGE_ID_" + toNodeId,packageId);
					logger.error("上传失败-->TO_NODE_ID:" + toNodeId + ",失败原因：" + e );
					logger.error("上传失败-->CUR_NODE_ID：" + curNodeId + ",PACKAGE_ID：" + packageId );

					// 上传异常，更新状态
//					updateStatus(FileConfigUtil.ISCENTER,
//							packageId,
//							curNodeId,
//							toNodeId,
//							RecvSendStateEnum.FAIL.getStateCode());

				} finally {
					countDownLatch.countDown();
				}
			}
		});

	}


	/**
	 * @描述 当前节点是文档服务器，下一节点是待上传的FTP
	 * 上传，同时更新本节点、下一节点状态
	 * @参数 [map] PACKAGE_ID 数据包id
	 * @返回值 void
	 * @创建人 姚伟-weiyao2
	 * @创建时间 2019/2/27
	 * @修改人和其它信息
	 */
	//@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
	public void upPack(ConcurrentHashMap<String, Object> map) throws Exception {
		// 开始时间
		BigDecimal start = new BigDecimal(System.currentTimeMillis());

		String packageId = (String) map.get("PACKAGE_ID");// 数据包名
		String curNodeId = (String) map.get("NODE_ID");// 当前节点
		String toNodeId = (String) map.get("TO_NODE_ID");// 目标节点
		String localPath = (String) map.get("FILE_PATH");//源文件地址
		Boolean isCenter = FileConfigUtil.ISCENTER;// 是否中心节点

		// 0、获取待上传的FTP节点
		// nextNodeId为待上传的FTP_ID，根据packageId、toNodeId、curNodeId从nodelink表中查询出来

		// ********* 重点>>>>> 永远只取主包,只有主包有nodeLink <<<<<<<*************
		String mainPackageId = packageId.split("\\.")[0] + CommonConstants.NAME.ZIP;

		NodeLinkBean linkBean = new NodeLinkBean();
		linkBean.setToNodeId(toNodeId);
		linkBean.setPackageId(mainPackageId);
		linkBean.setLeftNodeId(curNodeId);
		linkBean = linkBeanMapper.getLinkByCurNode(linkBean);
		if (linkBean == null) {
			logger.error("找不到上传FTP！file path[{}] ,packageId[{}]", localPath, packageId);
			updateStatus(isCenter,
					packageId,
					curNodeId,
					toNodeId,
					RecvSendStateEnum.FAIL.getStateCode(),null);
			throw new BusinessErrorException(ExceptionState.UP.getCode(),ExceptionState.UP.getName() + "找不到上传FTP！file path:" + localPath + " ,packageId:" + packageId);
		}
		String ftpNodeId = linkBean.getRightNodeId();

		// 1、上传之前要记录下

		// 发送中状态
		String sendState = RecvSendStateEnum.SENDING.getStateCode();

		// 更新状态，生成ack
		updateStatus(isCenter,
				packageId,
				curNodeId,
				toNodeId,
				sendState,null);

		// FTP接收中
		updateStatus(isCenter,
				packageId,
				ftpNodeId,
				toNodeId,
				RecvSendStateEnum.RECVING.getStateCode(),null);

		// 2、根据nextNodeId获取FTP连接池，获取上传路径，上传文件
		FtpClientTemplate ftpClientTemplate = FtpClientTemplate.FTP_CLIENT_TEMPLATE.get(ftpNodeId);
		FTPConfig config = FtpClientTemplate.FTP_CONFIG.get(ftpNodeId);
		String host = ftpClientTemplate.getFtpClientConfig().getHost();

		logger.info("开始上传==>FTP_IP=" + host + "==>packageId:" + packageId);

		String upPath = config.getDataPackageFolderUp();// /up/data

		Boolean bool = ftpClientTemplate.uploadFileReset(localPath, packageId, upPath);
//        Boolean bool = ftpClientTemplate.uploadFile(new File(localPath), upPath);

		// 3、上传之后要更新下状态
		String ftpState = "";// FTP更新状态
		if (bool) {
			sendState = RecvSendStateEnum.SENDSUCC.getStateCode();
			ftpState = RecvSendStateEnum.RECVED.getStateCode();
			logger.info("上传成功==>FTP_IP=" + host + ",curNodeId:" + curNodeId + ",packageId:" + packageId);
		} else {
			sendState = RecvSendStateEnum.FAIL.getStateCode();
			ftpState = RecvSendStateEnum.FAIL.getStateCode();
			logger.error("上传异常==>FTP_IP=" + host + ",curNodeId:" + curNodeId + ",packageId:" + packageId);
		}

		//操作结束时间
		BigDecimal end = new BigDecimal(System.currentTimeMillis());
		//计算耗时
		BigDecimal spendTime = end.subtract(start).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP);


		// 更新状态，生成ack
		updateStatus(isCenter, packageId, curNodeId, toNodeId, sendState, spendTime);

		// FTP接收完成
		updateStatus(isCenter,
				packageId,
				ftpNodeId,
				toNodeId,
				ftpState,
				spendTime);

		if (RecvSendStateEnum.FAIL.getStateCode().equals(sendState)) {
			throw new BusinessErrorException(ExceptionState.UP.getCode(),ExceptionState.UP.getName() + "curNodeId:" + curNodeId + ",packageId:" + packageId);
		}
	}

	/**
	 *@描述 判断是否需要根据toNodeId循环更新
	 *@参数  [flag, packageId, curNodeId, toNodeId, sendStateDm]
	 * @param flag  [true/false]ture：叶子节点下载、中心节点上传，false：中心节点下载、叶子节点上传
	 *@返回值  void
	 *@创建人  姚伟-weiyao2
	 *@创建时间  2019/3/14
	 *@修改人和其它信息
	 */
	public void updateStatus(boolean flag,
							 String packageId,
							 String curNodeId,
							 String toNodeId,
							 String sendStateDm,
							 BigDecimal spendTime) throws BusinessErrorException {


		ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<String, Object>();

		if ( flag ) {
			// 叶子节点下载、中心节点上传，更新状态

			paramMap.put("PACKAGE_ID", packageId);
			paramMap.put("NODE_ID", curNodeId);
			paramMap.put("TO_NODE_ID", toNodeId);
			paramMap.put("OPERATE_STATE_DM", sendStateDm);
			paramMap.put("SEND_STATE_DM", sendStateDm);
			paramMap.put("SPEND_TIME",spendTime);
			upStatusService.updateCurState(paramMap);
//            createUpAck(paramMap);
//            sendAckService.createUpAck(paramMap);
		} else {

			// 中心节点下载、叶子节点上传，要循环toNodeId，更新状态，
			String[] appIdTos = PackUtil.splitAppTo(packageId);
			for (String appIdTo : appIdTos) {

				// 获取appId对应的toNodeId 后续这些数据都可以作为缓存表读入server中
				String toId = (String) ConcurrentCache.getFieldValue("TO_NODE_ID_" + appIdTo);
				if (toId == null) {
					NodeAppBean nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(appIdTo);
					toId =  nodeAppBean.getNodeId();
					ConcurrentCache.setFieldValue("TO_NODE_ID_" + appIdTo,  toId);
				}
				toId = (String) ConcurrentCache.getFieldValue("TO_NODE_ID_" + appIdTo);

				paramMap = new ConcurrentHashMap<String, Object>();
				paramMap.put("PACKAGE_ID", packageId);
				paramMap.put("NODE_ID", curNodeId);
				paramMap.put("TO_NODE_ID", toId);
				paramMap.put("OPERATE_STATE_DM", sendStateDm);
				paramMap.put("SEND_STATE_DM", sendStateDm);
				paramMap.put("SPEND_TIME",spendTime);

				// 3 修改节点状态
				upStatusService.updateCurState(paramMap);

				// 4、每次发送都有ack生成
//                createUpAck(paramMap);
//                sendAckService.createUpAck(paramMap);
			}// for end

		}// else end

	}

}
