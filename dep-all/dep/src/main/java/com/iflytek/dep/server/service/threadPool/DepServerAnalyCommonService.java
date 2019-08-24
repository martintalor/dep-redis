package com.iflytek.dep.server.service.threadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批处理附件信息
 * 
 * @author qif
 * @date 2017年5月2日上午11:20:49
 */
@Service
public class DepServerAnalyCommonService {
	private final static Logger logger = LoggerFactory.getLogger(DepServerAnalyCommonService.class);

	private CountDownLatch countDownLatch;

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

	ExecutorService fixedUpStreamThreadPool = Executors.newFixedThreadPool(40,
			new DepServerAnalyCommonService.DepThreadFactory("dep up stream thread"));

	ExecutorService fixedDownStreamThreadPool = Executors.newFixedThreadPool(40,
			new DepServerAnalyCommonService.DepThreadFactory("dep down stream thread"));
	ExecutorService futureThreadPool = Executors.newFixedThreadPool(100);



	//当前上载任务数量,(相对于服务来说)
	private static AtomicInteger upJobNumber = new AtomicInteger(0);
	//当前下载任务数量,(相对于服务来说)
	private static AtomicInteger downJobNumber = new AtomicInteger(0);


	/**
	 * 做数据压缩、加密、上传FTP
	 * @param jobId
	 */
	public void downloadFilePackage(final String jobId ,final String folderName,final String packageName,
									final String securytimark

	) {


		final Future<?> fu = fixedDownStreamThreadPool.submit(new Runnable() {

			public void run() {


			}
		});



	}


	/**
	 * 数据包下载、数据包解密、数据包合并、数据包、加密、上传FTP
	 * @param jobId
	 */
	public void uploadFilePackage(final String jobId ) {


		final Future<?> fu = fixedUpStreamThreadPool.submit(new Runnable() {

			public void run() {


			}
		});



	}
}