package com.iflytek.dep.server.ftp;

import com.github.drapostolos.rdp4j.spi.FileElement;
import com.github.drapostolos.rdp4j.spi.PolledDirectory;
import com.iflytek.dep.server.ftp.core.FtpClientTemplate;

import java.io.IOException;
import java.util.Set;

public class FtpDirectory implements PolledDirectory {

    private String workingDirectory;

    private FtpClientTemplate ftpClientTemplate;

    public FtpClientTemplate getFtpClientTemplate() {
        return ftpClientTemplate;
    }

    public FtpDirectory(String dir, FtpClientTemplate ftpClientTemplate) {
        this.workingDirectory = dir;
        this.ftpClientTemplate = ftpClientTemplate;
    }

    public Set<FileElement> listFiles() throws IOException {
        return ftpClientTemplate.listFiles(workingDirectory);
    }
}
