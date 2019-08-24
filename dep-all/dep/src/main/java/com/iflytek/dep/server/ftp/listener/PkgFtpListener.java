package com.iflytek.dep.server.ftp.listener;

import com.github.drapostolos.rdp4j.*;
import com.github.drapostolos.rdp4j.spi.FileElement;
import com.google.gson.Gson;
import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.server.constants.LevelEnum;
import com.iflytek.dep.server.constants.RedisQueueType;
import com.iflytek.dep.server.ftp.FtpDirectory;
import com.iflytek.dep.server.ftp.FtpListener;
import com.iflytek.dep.server.ftp.core.FtpClientTemplate;
import com.iflytek.dep.server.redis.DownloadTaskStatus;
import com.iflytek.dep.server.redis.PackageInfo;
import com.iflytek.dep.server.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PkgFtpListener extends FtpListener {

    private static Logger logger = LoggerFactory.getLogger(PkgFtpListener.class);

    @Autowired
    private RedisUtil redisUtil;

	String pengingDownList = RedisQueueType.PENG_DOWN_PKG.getCode();
	String doingList = "downloadTaskList-pkg-map";

    @Override
	public void initialContent(InitialContentEvent arg0) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ioErrorCeased(IoErrorCeasedEvent arg0) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ioErrorRaised(IoErrorRaisedEvent arg0) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fileAdded(FileAddedEvent event) throws InterruptedException {
//		System.out.println(DateUtils.sysdate() + " Added: " + event.getFileElement());
        logger.info( Thread.currentThread().getName()+ " fileAdded PkgFtpListener INIT fileName="  +  event.getFileElement().getName() );

        FileElement element = event.getFileElement();
//
        // 传递参数
        ConcurrentHashMap<String,Object> paramMap = new  ConcurrentHashMap<String,Object>();

        String fileName = element.getName();

        //  不监控包含tmp的文件
        if ( fileName.toUpperCase().contains("TMP") ) {
            logger.debug( "contains tmp="  +fileName );
            return;
        }

        if (!fileName.startsWith("PKG")) {
            return;
        }

		FtpDirectory directory = (FtpDirectory) event.getPolledDirectory();
		FtpClientTemplate ftpClientTemplate = directory.getFtpClientTemplate();
		String curNodeId = ftpClientTemplate.getFtpClientConfig().getNodeId();

		// PKG待下载任务,写入redis
		try {
			redisUtil.pushDownTask(null,fileName,curNodeId);
		} catch (Exception e) {
			throw new InterruptedException("保存redis 失败:=" + fileName);
		}

	}

	@Override
	public void fileModified(FileModifiedEvent event) throws InterruptedException {
		System.out.println("Modified: " + event.getFileElement());
		
	}

	@Override
	public void fileRemoved(FileRemovedEvent arg0) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}


}
