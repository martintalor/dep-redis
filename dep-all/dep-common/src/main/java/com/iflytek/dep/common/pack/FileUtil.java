package com.iflytek.dep.common.pack;


import com.iflytek.dep.common.security.*;
import com.iflytek.dep.common.utils.CommonConstants;
import com.iflytek.dep.common.utils.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.file.pack
 * @Description:
 * @date 2019/2/19--15:52
 */
public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * @描述
     * @参数 [commonPath 文件目录 fromTo appid编号]
     * @返回值 java.util.Map flag为返回状态 path为返回文件存放目录路径 fileName为无后缀的压缩文件名称
     * @创建人 朱一帆
     * @创建时间 2019/2/20
     * @修改人和其它信息
     */
    public static Map<String, Object> makeDir(String commonPath, String fromTo) {

        Map result = new HashMap();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        //生成格式化年/月/日的文件路径
        String format = sf.format(date);
        //生成打包后打包文件的无后缀名称
        String fileName = makeZipFileName(fromTo);
        //完整的文件存放目录地址
        String path = commonPath + CommonConstants.FILE_INFO.FILESPLIT + format + CommonConstants.FILE_INFO.FILESPLIT + fileName;
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
     * @描述 生成打包后文件无后缀名称
     * @参数 [fromTO appid的代码]
     * @返回值 java.lang.String 文件名称
     * @创建人 朱一帆
     * @创建时间 2019/2/20
     * @修改人和其它信息
     */
    public static String makeZipFileName(String fromTO) {

        //返回生成压缩包的名称 命名规则为from_xx_to_xx_时间戳_打包目录的uuid(去掉中间横岗！).zip
        return fromTO + CommonConstants.FILE_INFO.SPILT + System.currentTimeMillis() + CommonConstants.FILE_INFO.SPILT + UUIDGenerator.createUUID();
    }

    /**
     * @描述 生成压缩包存放路径
     * @参数 [outDir 压缩包存放主目录, fileName 压缩包文件无后缀名称]
     * @返回值 java.util.Map
     * @创建人 朱一帆
     * @创建时间 2019/2/22
     * @修改人和其它信息
     */
    public static Map<String, Object> makeZipOut(String outDir, String fileName) {

        Map result = new HashMap();
        //生成压缩文件存放路径
        File zip = new File(outDir + CommonConstants.FILE_INFO.FILESPLIT + fileName);
        if (!zip.exists()) {
            boolean mkdirs = zip.mkdirs();
            if (mkdirs) {
                result.put("flag", true);
                result.put("path", outDir + CommonConstants.FILE_INFO.FILESPLIT + fileName);
                return result;
            } else {
                result.put("flag", false);
                result.put("path", "创建文件夹失败");
                return result;
            }
        } else {
            result.put("flag", true);
            result.put("path", outDir + CommonConstants.FILE_INFO.FILESPLIT + fileName);
            return result;
        }
    }


    /**
     * @描述
     * @参数 [filePath]
     * @返回值 byte[]
     * @创建人 朱一帆
     * @创建时间 2019/2/22
     * @修改人和其它信息
     */
    public static byte[] InputStream2ByteArray(String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);
        byte[] data = toByteArray(in);
        in.close();

        return data;
    }

    /**
     * @描述
     * @参数 [in]
     * @返回值 byte[]
     * @创建人 朱一帆
     * @创建时间 2019/2/22
     * @修改人和其它信息
     */
    private static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    /**
     * @描述 获取公钥
     * @参数 [publicKeyPath]
     * @返回值 java.security.PublicKey
     * @创建人 朱一帆
     * @创建时间 2019/2/22
     * @修改人和其它信息
     */
    //获取公钥
    private static PublicKey getPublicKey(String publicKeyPath) throws Exception {

        byte[] bytes = Files.readAllBytes(Paths.get(publicKeyPath));
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pub = kf.generatePublic(ks);
        return pub;
    }

    /**
     * @描述 获取私钥
     * @参数 [privateKeyPath]
     * @返回值 java.security.PrivateKey
     * @创建人 朱一帆
     * @创建时间 2019/2/22
     * @修改人和其它信息
     */
    //获取私钥
    private static PrivateKey getPrivateKey(String privateKeyPath) throws Exception {
        byte[] bytes1 = Files.readAllBytes(Paths.get(privateKeyPath));
        PKCS8EncodedKeySpec ks1 = new PKCS8EncodedKeySpec(bytes1);
        KeyFactory kf1 = KeyFactory.getInstance("RSA");
        PrivateKey pvt = kf1.generatePrivate(ks1);
        return pvt;
    }

    /**
     * @描述 源文件加密(单个文件)
     * @参数 [file 需要压缩的单个文件, publicKey 公钥的路径 ,appTo 目标id]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/2/22
     * @修改人和其它信息
     */
    //源文件加密(单个文件)
    public static void getEncryptFileByFile(File file, String publicKey,String appTo) throws EncryptException {
        try {
            long startTime = System.currentTimeMillis();
            System.out.println("开始加密单个文件");
            String parent = file.getParent();
            FileEncrypt fileEncrypt = new FileEncryptImpl(file.getAbsolutePath(), parent+CommonConstants.FILE_INFO.FILESPLIT+appTo, publicKey);
            //加密
            fileEncrypt.encrypt();
            System.out.println("单个文件加密完成耗时:" + (System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception e) {
            logger.error("getEncryptFileByFile file:{},publicKey:{},appTo:{}, error[{}]",file,publicKey,appTo,e);
            throw e;
        }
    }

    /**
     * @描述 源文件加密（根据分卷主文件路径加密所有分卷）
     * @参数 [pathStr 分卷压缩文件所在文件夹目录, publicKey 加密公钥地址,appTo 目标id]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/2/22
     * @修改人和其它信息
     */
    //源文件加密
    public static void getEncryptFileByFileList(String pathStr, String publicKey,String appTo ) throws EncryptException {
        long startTime = System.currentTimeMillis();
        System.out.println("开始加密文件及其分卷");
        File newFile = new File(pathStr);
        File[] files = newFile.listFiles();
        for (File file : files) {
            getEncryptFileByFile(file, publicKey,appTo);
        }

        System.out.println("全部文件加密完成耗时:" + (System.currentTimeMillis() - startTime) + "ms");
    }

    /**
     * @描述 文件解密(单个文件)
     * @参数 [file 待解密单个文件, privateKey 解密密钥地址]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/2/22
     * @修改人和其它信息
     */
    //文件解密(单个文件)
    public static void decryptFile(File file, String privateKey) throws DecryptException {
        long startTime = System.currentTimeMillis();
        System.out.println("单个文件解密开始");
        String parent = file.getParent();
        FileDecrypt fileDecrypt = new FileDecryptImpl(file.getAbsolutePath(), parent, privateKey);
        //解密
        fileDecrypt.decrypt();

        System.out.println("文件解密耗时:" + (System.currentTimeMillis() - startTime) + "ms");
    }

    /**
     *@描述  decryptFile
     *@参数  [file, privateKey]
     *@返回值  void
     *@创建人  朱一帆
     *@创建时间  2019/3/27
     *@修改人和其它信息
     */
    //文件解密叶子节点(单个文件)
    public static void decryptLeafFile(File file, String privateKey) throws DecryptException {
        long startTime = System.currentTimeMillis();
        System.out.println("单个文件解密开始");
        String parent = file.getParent();
        FileDecrypt fileDecrypt = new FileDecryptImpl(file.getAbsolutePath(), parent+CommonConstants.FILE_INFO.FILESPLIT+"unpack", privateKey);
        //解密
        fileDecrypt.decrypt();

        System.out.println("文件解密耗时:" + (System.currentTimeMillis() - startTime) + "ms");
    }
    /**
     * @描述 文件解密（解密全部分卷文件）
     * @参数 [pathStr 分卷压缩加密后文件的主文件全路径（带名称） , privateKey 解密密钥地址]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/2/22
     * @修改人和其它信息
     */
    public static void decryptFileList(String pathStr, String privateKey) throws DecryptException {
        long startTime = System.currentTimeMillis();
        System.out.println("开始解密文件及分卷");
        File oldFile = new File(pathStr);
        String parent = oldFile.getParent();
        File newFile = new File(parent);
        File[] files = newFile.listFiles();
        for (File file : files) {
            decryptFile(file, privateKey);
        }
        System.out.println("文件及分卷解密耗时:" + (System.currentTimeMillis() - startTime) + "ms");
    }

    /**
     * @描述 delFolder
     * @参数 [folderPath]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/2/26
     * @修改人和其它信息
     */
    //删除文件夹
    //param folderPath 文件夹完整绝对路径
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @描述 delAllFile
     * @参数 [path]
     * @返回值 boolean
     * @创建人 朱一帆
     * @创建时间 2019/2/26
     * @修改人和其它信息
     */
    //删除指定文件夹下所有文件
    //param path 文件夹完整绝对路径
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

}
