package com.iflytek.dep.common.ftp;

import com.github.drapostolos.rdp4j.spi.FileElement;

import java.io.IOException;

/**
 * FTP文件基本信息类
 * <p>
 * Created by admin on 2019/2/19.
 */
public class FTPFile implements FileElement {
    private final org.apache.commons.net.ftp.FTPFile file;
    private final String name;
    private final boolean isDirectory;

    public FTPFile(org.apache.commons.net.ftp.FTPFile file) {
        this.file = file;
        this.name = file.getName();
        this.isDirectory = file.isDirectory();
    }

    @Override
    public long lastModified() throws IOException {
        return file.getTimestamp().getTimeInMillis();
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return file.toString();
    }
}
