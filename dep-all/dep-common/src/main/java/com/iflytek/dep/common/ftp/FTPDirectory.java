package com.iflytek.dep.common.ftp;

import com.github.drapostolos.rdp4j.spi.FileElement;
import com.github.drapostolos.rdp4j.spi.PolledDirectory;

import java.io.IOException;
import java.util.Set;

/**
 * 监听目录类
 * <p>
 * Created by admin on 2019/2/19.
 */
public class FTPDirectory implements PolledDirectory {

    /**
     * 工作目录
     */
    private String workingDirectory;

    public FTPDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public Set<FileElement> listFiles() throws IOException {
        return FTPUtils.listRemoteFile(workingDirectory);
    }
}
