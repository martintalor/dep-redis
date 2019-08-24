package com.iflytek.dep.server.file;

import net.lingala.zip4j.exception.ZipException;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.service
 * @Description:
 * @date 2019/2/22--20:36
 */
public interface ZipService {
    String toZip(String outStr, String packDirPath, String fileName) throws ZipException;
    void unZip(String packedPath) throws ZipException;
}
