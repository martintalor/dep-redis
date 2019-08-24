package com.iflytek.dep.server.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

/**
 * 文件加密服务
 *
 * @author ddcai
 * @date 2019年3月8日上午8:40:49
 */
@Scope("prototype")
@Service
public class FileEncryptService {
	private final static Logger logger = LoggerFactory.getLogger(FileEncryptService.class);


	private ExecutorService threadPoolForWord;
	private CountDownLatch countDownLatch;

	class FileEncryptThreadFactory implements ThreadFactory {

		private int counter;
		private String name;

		public FileEncryptThreadFactory(String name) {
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

	public void encryptFile(final List<String> filePath) {
		long totalStart = System.currentTimeMillis();
		countDownLatch = new CountDownLatch(filePath.size());
		int threads = Runtime.getRuntime().availableProcessors();
		this.initThreadPool(threads);
		for (String filePathItem : filePath) {
			threadPoolForWord.execute(() -> {
				long start = System.currentTimeMillis();
				try {
					//Do Encrypt file


				} catch (Exception e) {
					e.printStackTrace();
					logger.error("encryptFile ,file path:{} happend error,{}", filePathItem, e);
				} finally {
					if (countDownLatch != null) {
						countDownLatch.countDown();
					}
				}
			});

		}

		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
//            logger.error(e,"");
		} finally {
			shutdown();
			long totalEnd = System.currentTimeMillis();

		}

	}


	/**
	 * 初始化线程池
	 */
	private void initThreadPool(int threads) {
		threadPoolForWord = new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
	}

	/**
	 * 关闭线程池
	 */
	private void shutdown() {
		if (!threadPoolForWord.isShutdown()) {
			threadPoolForWord.shutdown();
		}
	}
}