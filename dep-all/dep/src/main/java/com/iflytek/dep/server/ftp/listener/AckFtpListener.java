package com.iflytek.dep.server.ftp.listener;

import com.github.drapostolos.rdp4j.*;
import com.github.drapostolos.rdp4j.spi.FileElement;
import com.google.gson.Gson;
import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.server.config.web.ApplicationContextRegister;
import com.iflytek.dep.server.constants.LevelEnum;
import com.iflytek.dep.server.ftp.FtpDirectory;
import com.iflytek.dep.server.ftp.FtpListener;
import com.iflytek.dep.server.ftp.core.FtpClientTemplate;
import com.iflytek.dep.server.redis.DownloadTaskStatus;
import com.iflytek.dep.server.redis.PackageInfo;
import com.iflytek.dep.server.service.dataPack.ParseAckService;
import com.iflytek.dep.server.service.dataPack.SendAckService;
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
public class AckFtpListener extends FtpListener {

    private static Logger logger = LoggerFactory.getLogger(AckFtpListener.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ParseAckService parseAckService;

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
        logger.info( Thread.currentThread().getName()+ " fileAdded AckFtpListener INIT fileName="  +  event.getFileElement().getName() );

        FileElement element = event.getFileElement();
//
        SendAckService sendAckService = (SendAckService)ApplicationContextRegister.getApplicationContext().getBean(SendAckService.class);

        String fileName = element.getName();

        //  不监控后缀tmp的文件
        if ( fileName.toUpperCase().endsWith("TMP") ) {
            logger.debug( "endsWith tmp="  +fileName );
            return;
        }

        //  不监控包含tmp的文件
        if ( fileName.toUpperCase().contains("TMP") ) {
            logger.debug( "contains tmp="  +fileName );
            return;
        }

        if (!fileName.startsWith("ACK")) {
            return;
        }

        FtpDirectory directory = (FtpDirectory) event.getPolledDirectory();
        FtpClientTemplate ftpClientTemplate = directory.getFtpClientTemplate();
        String curNodeId = ftpClientTemplate.getFtpClientConfig().getNodeId();


        /**
        FtpDirectory directory = (FtpDirectory) event.getPolledDirectory();
        FtpClientTemplate ftpClientTemplate = directory.getFtpClientTemplate();
        String curNodeId = ftpClientTemplate.getFtpClientConfig().getNodeId();

        // 传递参数
        ConcurrentHashMap<String,Object> paramMap = new  ConcurrentHashMap<String,Object>();
        paramMap.put("PACKAGE_ID",fileName);// 数据包名
        paramMap.put("NODE_ID",curNodeId);// 当前FTP节点

        try {
            parseAckService.parseAck(paramMap);
        } catch (Exception e) {
            throw new InterruptedException(e.getMessage());
        }
        **/
        // 写入ACK 待下载任务

        try {
            redisUtil.pushAckTask(fileName,curNodeId);
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
