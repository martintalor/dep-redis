package com.iflytek.dep.server.file;

import com.iflytek.dep.common.pack.FileUtil;
import com.iflytek.dep.common.security.DecryptException;
import com.iflytek.dep.common.security.EncryptException;
import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.common.utils.UUIDGenerator;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.FileConfigUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.service.impl
 * @Description:
 * @date 2019/2/22--20:07
 */
@Service
public class FileServiceImpl implements FileService {

    /**
     * @描述 getFileDir
     * @参数 [appId]
     * @返回值 java.util.Map<java.lang.String   ,   java.lang.Object>
     * @创建人 朱一帆
     * @创建时间 2019/2/25
     * @修改人和其它信息
     */
    @Override
    public Map<String, Object> getFileDir(String appId) {
        //获取配置文件中的存放打包文件的目录
        String packDir = FileConfigUtil.PACKDIR;
        Map<String, Object> dir = FileUtil.makeDir(packDir, appId);
        return dir;
    }

    public Map<String, Object> getFileDir(String appId,String level) {
        //获取配置文件中的存放打包文件的目录
        String packDir = FileConfigUtil.PACKDIR;
        Map<String, Object> dir = makeDir(packDir, appId, level);
        return dir;
    }

    public static Map<String, Object> makeDir(String commonPath, String fromTo, String level) {

        Map result = new HashMap();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        //生成格式化年/月/日的文件路径
        String format = sf.format(date);
        //生成打包后打包文件的无后缀名称
        String fileName = fromTo + com.iflytek.dep.common.utils.CommonConstants.FILE_INFO.SPILT + System.currentTimeMillis() + com.iflytek.dep.common.utils.CommonConstants.FILE_INFO.SPILT + UUIDGenerator.createUUID()
                + "+" + level;// 添加级别
        //完整的文件存放目录地址
        String path = commonPath + com.iflytek.dep.common.utils.CommonConstants.FILE_INFO.FILESPLIT + format + com.iflytek.dep.common.utils.CommonConstants.FILE_INFO.FILESPLIT + fileName ;
        File dir = new File(path);
        //如果不存在生成文件夹
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            if (mkdirs) {
                result.put("flag", true);
                result.put("path", path);
                result.put("fileName", fileName);
                return result;
            } else {
                result.put("flag", false);
                result.put("path", "创建文件夹失败");
                result.put("fileName", fileName);
                return result;
            }
        } else {
            result.put("flag", true);
            result.put("path", path);
            result.put("fileName", fileName);
            return result;
        }


    }

    /**
     * @描述 encryptZips
     * @参数 [pathStr ,publicKey,appTo]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/2/25
     * @修改人和其它信息
     */
    @Override
    public void encryptZips(String pathStr, String publicKey,String appTo) throws EncryptException {
        FileUtil.getEncryptFileByFileList(pathStr, publicKey,appTo);

    }

    /**
     *@描述  encryptZip
     *@参数  [file, publicKey, appTo]
     *@返回值  void
     *@创建人  朱一帆
     *@创建时间  2019/3/6
     *@修改人和其它信息
     */
    @Override
    public void encryptZip(File file, String publicKey, String appTo) throws EncryptException {
        FileUtil.getEncryptFileByFile(file,publicKey,appTo);
    }

    /**
     * @描述 decryptZips
     * @参数 [pathStr]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/2/25
     * @修改人和其它信息
     */
    @Override
    public void decryptZips(String pathStr) throws DecryptException {
        //解密密钥地址
        String privateKey = FileConfigUtil.PRIVATEKEY;
        FileUtil.decryptFileList(pathStr, privateKey);
    }

    /**
     *@描述  decryptZip
     *@参数  [file]
     *@返回值  void
     *@创建人  朱一帆
     *@创建时间  2019/3/6
     *@修改人和其它信息
     */
    @Override
    public void decryptZip(File file) throws DecryptException {
        //解密密钥地址
        String privateKey = FileConfigUtil.PRIVATEKEY;
        FileUtil.decryptFile(file, privateKey);
    }

    /**
     *@描述  decryptLeafZip
     *@参数  [file]
     *@返回值  void
     *@创建人  朱一帆
     *@创建时间  2019/3/27
     *@修改人和其它信息
     */
    @Override
    public void decryptLeafZip(File file) throws DecryptException {
        //解密密钥地址
        String privateKey = FileConfigUtil.PRIVATEKEY;
        FileUtil.decryptLeafFile(file, privateKey);
    }

    /**
     * @描述 makeOutFileDir
     * @参数 [destZipFile, fileNames]
     * @返回值 java.lang.String
     * @创建人 朱一帆
     * @创建时间 2019/2/26
     * @修改人和其它信息
     */
    @Override
    public String makeOutFileDir(String destZipFile, String fileNames) {
        //生成压缩后文件存放路径
        Map outResult = FileUtil.makeZipOut(destZipFile, fileNames);
        String outStr = null;
        if ((boolean) outResult.get("flag")) {
            outStr = String.valueOf(outResult.get("path"));
            return outStr;
        } else {
            return null;
        }
    }



    /**
     * @描述 createKeys
     * @参数 [serverNode]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/2/26
     * @修改人和其它信息
     */
    @Override
    public void createKeys(String serverNode) throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        Key pub = kp.getPublic();
        Key pvt = kp.getPrivate();
        FileOutputStream out = new FileOutputStream(FileConfigUtil.CREATEKEY + CommonConstants.NAME.FILESPLIT + serverNode + CommonConstants.NAME.PUB);
        out.write(pub.getEncoded());
        out.flush();
        out.close();

        out = new FileOutputStream(FileConfigUtil.CREATEKEY + CommonConstants.NAME.FILESPLIT + serverNode + CommonConstants.NAME.PRI);
        out.write(pvt.getEncoded());
        out.flush();
        out.close();
    }

    @Override
    public String makeDirByPackageId(String packageId) {
        //压缩文件存放主目录
        String packedDir = FileConfigUtil.PACKEDDIR;
        String fileName= packageId.split("\\.")[0];
        String makeDirByPackageId = makeOutFileDir(packedDir, fileName);
        return makeDirByPackageId;
    }


    public String makeAckDirByPackageId(String packageId) {
        //压缩文件存放主目录
        String ackDir = FileConfigUtil.ACKDIR + File.separatorChar + DateUtils.getTodaySN() + File.separatorChar;
        String fileName= packageId.split("\\.")[0];
        String makeDirByPackageId = makeOutFileDir(ackDir, fileName);
        return makeDirByPackageId;
    }
}
