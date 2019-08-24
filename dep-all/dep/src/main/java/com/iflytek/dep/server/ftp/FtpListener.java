package com.iflytek.dep.server.ftp;

import com.github.drapostolos.rdp4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FtpListener implements DirectoryListener, IoErrorListener, InitialContentListener {

    private static Logger logger = LoggerFactory.getLogger(FtpListener.class);

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
        // 继承此类，在PkgFtpListener重写此方法
        logger.info( "Ftplistener ADD: " + event.getFileElement() );


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
