package com.iflytek.dep.server.file;


import com.iflytek.dep.common.security.DecryptException;
import com.iflytek.dep.common.security.EncryptException;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.service
 * @Description:
 * @date 2019/2/22--20:06
 */
public interface FileService {
    Map<String, Object> getFileDir(String appId);

    void encryptZips(String pathStr, String publicKey,String appTo) throws EncryptException;

    void encryptZip(File file, String publicKey, String appTo) throws EncryptException;

    void decryptZips(String pathStr) throws DecryptException;

    void decryptZip(File file) throws DecryptException;

    void decryptLeafZip(File file) throws DecryptException;

    String makeOutFileDir(String destZipFile, String fileNames);

    void createKeys(String serverNode) throws IOException, NoSuchAlgorithmException;

    String makeDirByPackageId(String packageId);
}
