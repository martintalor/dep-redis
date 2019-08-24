package com.iflytek.dep.common.ftp;

import com.github.drapostolos.rdp4j.*;


/**
 * FTP监听器实现demo
 * <p>
 * Created by admin on 2019/2/19.
 */
public class FTPPollerListener implements DirectoryListener, IoErrorListener, InitialContentListener {

    @Override
    public void fileAdded(FileAddedEvent event) {
        System.out.println("Added: " + event.getFileElement());
    }

    @Override
    public void fileRemoved(FileRemovedEvent event) {
        System.out.println("Removed: " + event.getFileElement());
    }

    @Override
    public void fileModified(FileModifiedEvent event) {
        System.out.println("Modified: " + event.getFileElement());
    }

    @Override
    public void ioErrorCeased(IoErrorCeasedEvent event) {
        System.out.println("I/O error ceased.");
    }

    @Override
    public void ioErrorRaised(IoErrorRaisedEvent event) {
        System.out.println("I/O error raised!");
        event.getIoException().printStackTrace();
    }

    @Override
    public void initialContent(InitialContentEvent event) {
        System.out.println("initial Content: ^");
    }
}
