package com.iflytek.dep.server.section;

import com.iflytek.dep.server.down.PkgGetter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseSectionThreadPool  {
	public int threadNumber = 2;

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


}
