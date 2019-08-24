package com.iflytek.dep.server.ftp;

import com.github.drapostolos.rdp4j.DirectoryPoller;
import com.github.drapostolos.rdp4j.DirectoryPollerFuture;
import com.github.drapostolos.rdp4j.spi.PolledDirectory;
import com.iflytek.dep.server.ftp.core.FtpClientTemplate;

import java.util.concurrent.TimeUnit;

public class FTPMonitor {
	
	private String ftpDir;
	private int pollingInterval = 10;
	private FtpClientTemplate ftpClientTemplate;

	public FTPMonitor(String ftpDir, int pollingInterval, FtpClientTemplate ftpClientTemplate) {
		this.ftpDir = ftpDir;
		this.pollingInterval = pollingInterval;
		this.ftpClientTemplate = ftpClientTemplate;
	}
	
	public DirectoryPollerFuture execute(FtpListener listener) {
		PolledDirectory polledDirectory = new FtpDirectory(this.ftpDir, this.ftpClientTemplate);
		DirectoryPollerFuture directoryPollerFuture = DirectoryPoller.newBuilder()
                .addPolledDirectory(polledDirectory)
                .addListener( listener )
                .enableFileAddedEventsForInitialContent()
                .setPollingInterval(this.pollingInterval, TimeUnit.SECONDS)
                .startAsync();
		return directoryPollerFuture;
	}

}
