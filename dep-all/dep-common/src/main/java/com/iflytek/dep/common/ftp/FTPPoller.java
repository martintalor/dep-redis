package com.iflytek.dep.common.ftp;

import com.github.drapostolos.rdp4j.DirectoryPoller;
import com.github.drapostolos.rdp4j.Rdp4jListener;
import com.github.drapostolos.rdp4j.spi.PolledDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * FTP文件监控类
 * <p>
 * Created by admin on 2019/2/22.
 */
public class FTPPoller {

    private static Logger logger = LoggerFactory.getLogger(FTPPoller.class);

    public <T extends Rdp4jListener> void start(String workingDirectory, T listener, long pollingInterval, TimeUnit timeUnit) {
        //构建远程监听目录
        PolledDirectory polledDirectory = new FTPDirectory(workingDirectory);
        // 启动监听任务
        DirectoryPoller dp = DirectoryPoller.newBuilder()
                .addPolledDirectory(polledDirectory)
                .addListener(listener)
                .enableFileAddedEventsForInitialContent()
                .setPollingInterval(pollingInterval, TimeUnit.MINUTES)
                .start();
    }
}
