package com.iflytek.dep.server.ftp.core;

import com.github.drapostolos.rdp4j.spi.FileElement;
import com.iflytek.dep.server.ftp.DEPFile;
import com.iflytek.dep.server.ftp.config.FtpClientConfig;
import com.iflytek.dep.server.model.FTPConfig;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.server.ftp.core
 * @Description: FtpClientTemplate
 * @date 2019/3/4--14:39
 */
public class FtpClientTemplate {

    private static final Logger log = LoggerFactory.getLogger(FtpClientTemplate.class);

    // 缓存ftp配置信息
    public static Map<String, FTPConfig> FTP_CONFIG;
    // 缓存FtpClientTemplate
    public static Map<String, FtpClientTemplate> FTP_CLIENT_TEMPLATE;

    static {
        FTP_CONFIG = new ConcurrentHashMap<String, FTPConfig>();
        FTP_CLIENT_TEMPLATE = new ConcurrentHashMap<String, FtpClientTemplate>();
    }

    public FtpClientTemplate(String nodeID) {
        initFTPConfig(nodeID);
        this.ftpClientFactory = new FtpClientFactory(ftpClientConfig);
//        this.ftpClientPool = new GenericObjectPool<>(ftpClientFactory, genericObjectPoolConfig);
    }


//    private final GenericObjectPool<FTPClient> ftpClientPool;

    /**
     * ftp连接池配置
     */
    private GenericObjectPoolConfig genericObjectPoolConfig;

    /**
     * ftp客户端配置
     */
    private FtpClientConfig ftpClientConfig;

    /**
     * ftp客户端工厂
     */
    private final FtpClientFactory ftpClientFactory;


    /***
     * ftp配置文件初始化
     *
     * @param nodeID 节点信息
     * @return
     */
    private void initFTPConfig(String nodeID) {
        // 配置ftp连接信息
        FTPConfig config = FtpClientTemplate.FTP_CONFIG.get(nodeID);
        this.ftpClientConfig = new FtpClientConfig();
        ftpClientConfig.setHost(config.getFtpIp());
        ftpClientConfig.setPort(config.getFtpPort());
        ftpClientConfig.setUsername(config.getUsername());
        ftpClientConfig.setPassword(config.getPassword());
        ftpClientConfig.setClientTimeout(config.getTimeout()*1000*60);
        ftpClientConfig.setMaxTotal(config.getConnectMax());
        ftpClientConfig.setNodeId(nodeID);
        // 配置ftp-pool信息
//        this.genericObjectPoolConfig = new GenericObjectPoolConfig();
//        genericObjectPoolConfig.setMaxTotal(ftpClientConfig.getMaxTotal());
//        genericObjectPoolConfig.setMaxIdle(ftpClientConfig.getMaxIdle());
//        genericObjectPoolConfig.setMinIdle(ftpClientConfig.getMinIdle());
//        genericObjectPoolConfig.setMaxWaitMillis(ftpClientConfig.getMaxWait());
//        genericObjectPoolConfig.setBlockWhenExhausted(ftpClientConfig.isBlockWhenExhausted());
//        genericObjectPoolConfig.setTestOnBorrow(ftpClientConfig.isTestOnBorrow());
//        genericObjectPoolConfig.setTestOnReturn(ftpClientConfig.isTestOnReturn());
//        genericObjectPoolConfig.setTestOnCreate(ftpClientConfig.isTestOnCreate());
//        genericObjectPoolConfig.setTestWhileIdle(ftpClientConfig.isTestWhileIdle());
    }

//    public GenericObjectPool<FTPClient> getFtpClientPool() {
//        return this.ftpClientPool;
//    }

    public FtpClientConfig getFtpClientConfig() {
        return this.ftpClientConfig;
    }


    public FTPClient getFtpClient() {
        FTPClient ftpClient = null;
        try {
            ftpClient =  this.ftpClientFactory.create();
        } catch (Exception e) {
            log.error("FTP create error：{}",e.getMessage());
        }
        return ftpClient;
    }

    public void destroyFtp(FTPClient ftpClient)  {
        this.ftpClientFactory.destroyFtp(ftpClient);
    }

    /***
     * 上传Ftp文件
     *
     * @param localFile 当地文件
     * @param remotePath 上传服务器路径 - 应该以/结束
     * @return true or false
     */
    public boolean uploadFile(File localFile, String remotePath) {
        FTPClient ftpClient = null;
        BufferedInputStream inStream = null;
        boolean resultBool = false;
        try {
            //从池中获取对象
//            ftpClient = ftpClientPool.borrowObject();
            ftpClient = ftpClientFactory.create();
            // 验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.warn("ftpServer refused connection, replyCode:{}", replyCode);
                return false;
            }
            // 开始上传的文件，先加后缀.tmp，防止被add事件监听
            String remoteFile = localFile.getName() ;
            String remoteTmp = remoteFile + ".tmp";

            // 改变工作路径
            ftpClient.changeWorkingDirectory(remotePath);
            inStream = new BufferedInputStream(new FileInputStream(localFile));
            log.info("文件【" + remoteFile + "】开始上传");
            final int retryTimes = 3;

            for (int j = 0; j <= retryTimes; j++) {
                resultBool = ftpClient.storeFile(remoteTmp, inStream);

                // 文件上传成功，改回原文件名
                if (resultBool) {
                    resultBool = ftpClient.rename(remoteTmp, remoteFile);
                    log.info("文件【" + remoteFile + "】上传结束");
                    return resultBool;
                }
                log.warn("upload file failure! try uploading again... {} times", j);
            }

        } catch (FileNotFoundException e) {
            log.error("file not found!{}", localFile);
        } catch (Exception e) {
            log.error("upload file failure!", e);
        } finally {
            //IOUtils.closeQuietly(inStream);
            //将对象放回池中
            //ftpClientPool.returnObject(ftpClient);
            try{
                IOUtils.closeQuietly(inStream);
                ftpClientFactory.destroyFtp(ftpClient);
//                ftpClientPool.returnObject(ftpClient);
            }
            catch (Exception e) {

            }
        }
        return false;
    }

    /**
     *@描述 比较本地与FTP文件大小
     *@参数  [ftpClient, ftpFileName, localFile]
     *@返回值  boolean
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/3/25
     *@修改人和其它信息
     */
    public boolean compareFile(FTPClient ftpClient ,String ftpFileName,File localFile) throws IOException {
        boolean result = false;
        long localFileSize = 0L; // 本地文件大小

        if (localFile.exists() ) {
            // 本地文件不存在
            localFileSize = localFile.length();
        }

        FTPFile[] ftpFiles = ftpClient.listFiles(ftpFileName);
        if ( null != ftpFiles && ftpFiles.length > 0 ) {

            // 判断文件大小是否一致
            result = ftpFiles[0].getSize() == localFileSize;
        }

        return result;
    }

    /**
     *@描述
     *@参数  [ftpClient, ftpFileName, localFile]
     *@返回值  boolean
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/3/25
     *@修改人和其它信息
     */
    public boolean compareUpFile(FTPClient ftpClient ,String ftpFileName,File localFile) throws IOException {

        // 校验是否一致
        Boolean result = compareFile(ftpClient, ftpFileName, localFile);

        // 上传的文件不一致，删除ftp文件，重传
        if (!result) {
            ftpClient.deleteFile(ftpFileName);
        }

        return result;
    }


    /**
     *@描述 比较下载的文件是否跟FTP上一致，如果不一致，删除下载的文件
     *@参数  [ftpClient, ftpFileName, localFile]
     *@返回值  boolean
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/3/25
     *@修改人和其它信息
     */
    public boolean compareDownFile(FTPClient ftpClient ,String ftpFileName,File localFile) throws IOException {

        // 校验是否一致
        Boolean result = compareFile(ftpClient, ftpFileName, localFile);

        // 下载的文件不一致，删除下载的文件，重下
        if (!result) {
            // 文件如果存在，则删除
            if (localFile.exists()) {
                localFile.delete();
            }
        }

        return result;
    }


    /***
     * 上传Ftp文件（支持断点续传）
     *
     * @param localFilePath 本地文件路径
     * @param fileName 上传后文件名
     * @param remoteFolder 上传文件夹
     * @return true or false
     */
    public boolean uploadFileReset( String localFilePath, String fileName, String remoteFolder)   {
        FTPClient ftpClient = null;
        boolean result = false;
        OutputStream outputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            //从池中获取对象
//            ftpClient = ftpClientPool.borrowObject();
            ftpClient = ftpClientFactory.create();
            ftpClient.setBufferSize(5*1024*1024);
            // 验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.warn("ftpServer refused connection, replyCode:{}", replyCode);
                return false;
            }

            log.debug( "uploadFileReset FTP连接成功：nodeId=" + ftpClientConfig.getNodeId() + ",FTP=" + ftpClientConfig.getHost() + ":" + ftpClientConfig.getPort() );


//            String remoteFilePath = remoteFolder + fileName;

            String fileTmp = fileName + ".tmp";// 先变成mp文件


            //设置被动模式(FTP客户端上传容易失败)
            ftpClient.enterLocalPassiveMode();
//            fileTmp = "PKG_APP-S1_TO_APP-J1_1552914704998_847c5adc-b45c-46c1-aeb8-ff0337eefc66.zip";

            // 改变工作目录
            ftpClient.changeWorkingDirectory(new String(remoteFolder.getBytes("GBK"), FTP.DEFAULT_CONTROL_ENCODING));
//            FTPFile ftpFile = ftpClient.mlistFile(fileTmp);// 此方法获取113FTP文件失败

            File newFile = new File(localFilePath);

            // 如果文件已经存在了，并且大小一致,返回true
            if ( compareUpFile(ftpClient,fileName, newFile ) ) {
                return true;
            }

            // 如果源文件不存在，则探测是否有其tmp文件，获取tmp文件大小
            // 探测ftp文件是否存在，获取其大小
            FTPFile[] ftpFiles = ftpClient.listFiles(fileTmp);
            FTPFile ftpFile = null;

            if ( null != ftpFiles && ftpFiles.length > 0 ) {
                ftpFile = ftpFiles[0];
            }

            // FTP文件如果不存在，则上传
            if (ftpFile == null) {
                InputStream ins = new FileInputStream(localFilePath);
                log.debug("start storeFile ,localFilePath [{}]",localFilePath);
                result = ftpClient.storeFile(fileTmp, ins);
                if (ins != null) {
                    ins.close();
                }
                log.debug(" storeFile end ,localFilePath [{}]",localFilePath);
            } else {
                log.debug("断点续传开始 ,localFilePath [{}]",localFilePath);
                long remoteFileSize = ftpFile.getSize();
                ftpClient.setRestartOffset(remoteFileSize);
                randomAccessFile = new RandomAccessFile(localFilePath, "r");
                randomAccessFile.seek(remoteFileSize);
                outputStream = ftpClient.appendFileStream(new String(fileTmp.getBytes("GBK"), "iso-8859-1"));
                byte[] buff = new byte[1024*1024];
                int readedSize = 0;
                while ((readedSize = randomAccessFile.read(buff)) > 0) {
                    outputStream.write(buff, 0, readedSize);
                }
                outputStream.flush();
                if (outputStream != null) {
                    outputStream.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                ftpClient.completePendingCommand();
                result = true;
                log.debug("断点续传结束,localFilePath [{}]",localFilePath);
            }

//            Thread.sleep(1000);// 等待1秒后，重命名

            // 去除.tmp后缀,重命名,可能第一次重命名不成功
            int delay = 3;// 重试次数

            for (int i = 0; i < delay; i++) {
                log.debug("修改文件名称 ,localFilePath old [{}],new [{}]",fileTmp,fileName);
                result = ftpClient.rename(fileTmp, fileName);
                if (result) {
                    break;
                }
            }
            if(!result)
            {
                log.info("修改文件名称 ,localFilePath old [{}],new [{}], 结果[{}]",fileTmp,fileName,result);
            }

            // 验证改名有没有成功
            if (result) {
                //校验文件是否存在，并且大小一致
                result =  compareFile(ftpClient,fileName, newFile);
            }
            if (!result) {
                log.info("compareFile ,localFilePath old [{}],new [{}], 结果[{}]",fileTmp,fileName,result);
            }
        } catch (FileNotFoundException e) {
            log.error("文件未发现:{}" ,localFilePath);
            result = false;
//            throw new IOException(e);
        } catch (Exception e) {
            log.error("FTP文件上传失败：{}", e);
            result = false;
//            throw new IOException(e);
        } finally {
            //将对象放回池中
            try {


                if (outputStream != null) {
                    outputStream.flush();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }

                ftpClientFactory.destroyFtp(ftpClient);
//                ftpClientPool.returnObject(ftpClient);
            }  catch (Exception e) {
                log.error("ftpClient 销毁失败：{}", e);
            }
        }

        return result;
    }


    /***
     * 下载Ftp文件（支持断点续传）
     * 重写downloadFile方法，
     *
     * @param remoteDir 远程文件路径
     * @param fileName 下载文件名称
     * @param localDir 本地文件夹路径
     * @return true or false
     */
    public boolean downloadFileReset(String remoteDir, String fileName, String localDir)  {
        FTPClient ftpClient = null;
        OutputStream fos = null;
        boolean result = false;
        String localFilePath = localDir + fileName ;
        String fileTmp = localFilePath + ".tmp" ;
        try {

            //从池中获取对象
//            ftpClient = ftpClientPool.borrowObject();

            log.info("-------------------1");
            ftpClient = ftpClientFactory.create();
            // 验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.warn("ftpServer refused connection, replyCode:{}", replyCode);
                return false;
            }

            log.info( "downloadFileReset FTP连接成功：nodeId=" + ftpClientConfig.getNodeId() + ",FTP=" + ftpClientConfig.getHost() + ":" + ftpClientConfig.getPort() );

//            ftpClient.enterLocalPassiveMode();
            log.info("-------------------2");
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(new String(remoteDir.getBytes("GBK"), FTP.DEFAULT_CONTROL_ENCODING));
            ftpClient.setBufferSize(5*1024*1024);
            // 查看文件是否已存在
            log.info("-------------------3");
            File newFile = new File(localFilePath);
//            if (newFile.exists()) {
//                return true;
//            }

            // 如果文件已经存在了，并且大小一致,返回true
            log.info("-------------------4");
            if ( compareDownFile(ftpClient,fileName, newFile) ) {
                return true;
            }

            log.info("-------------------5 retrieveFile");
            // 查看tmp文件是否存在，下载时先将文件变成tmp，下载成功后重命名正常文件
            File localFile = new File(fileTmp);
            if (localFile.exists()) {
                log.info("-------------------6.1 retrieveFile");
                fos = new FileOutputStream(localFile, true);
                ftpClient.setRestartOffset(localFile.length());
                result = ftpClient.retrieveFile(fileName, fos);
                log.info("-------------------6.1 retrieveFile result:" + result);
            } else {
                log.info("-------------------6.2 retrieveFile");
                localFile.getParentFile().mkdirs();
                localFile.createNewFile();
                log.info("-----------------6.2 retrieveFile temp文件是否生成"+String.valueOf(localFile.exists()));
                fos = new FileOutputStream(localFile);
                result = ftpClient.retrieveFile(fileName, fos);
                log.info("-----------------6.2 retrieveFile 流是否全部注入："+String.valueOf(result));
            }

            // 需要先清空OutputStream，否则renameTo不成功
            log.info("-------------------7 fos.flush");
            fos.flush();
            if (fos != null) {
                fos.close();
            }
            log.info("-------------------8 fos.flush fos：" + fos);

//            Thread.sleep(1000);// 等待1秒后，重命名

            // 下载成功，重命名
            log.info("-------------------8 delay");
            int delay = 3;
            for (int i = 0; i < delay; i++) {
                log.info("-------------------rename start,localFile.renameTo9,localFile:" + localFile + ",newFile:" +newFile);
                result = localFile.renameTo( newFile );
                if (result) {
                    log.info("-------------------rename true,localFile.renameTo9,localFile:" + localFile + ",newFile:" +newFile);
                    break;
                }
            }
            log.info("-------------------8delay result：" + result);

            // 验证改名有没有成功
            log.info("-------------------9 compareFile");
            if (result) {
                //校验文件是否存在，并且大小一致
                result =  compareFile(ftpClient,fileName, newFile);
            }

            log.info("-------------------9 compareFile result：" + result);

            // 重命名方法有缺陷，所以使用这种方法
//
//            // copy一份
//            FileUtils.copyFile(localFile, newFile);
//
//            // 删除
//            localFile.delete();

        } catch (UnsupportedEncodingException e) {
            log.error("FTP文件下载失败 UnsupportedEncodingException：" + localFilePath + ",MSG: " + e.getMessage());
            result = false;
        } catch (FileNotFoundException e) {
            log.error("FTP文件下载失败 FileNotFoundException：" + localFilePath + ",MSG: " + e.getMessage());
            result = false;
        } catch (IOException e) {
            log.error("FTP文件下载失败 IOException：" + localFilePath + ",MSG: " + e.getMessage());
            result = false;
        } catch (Exception e) {
            log.error("FTP文件下载失败 Exception：" + localFilePath + ",MSG: " + e.getMessage());
            result = false;
//            throw new IOException(e);
        }  finally {
            //将对象放回池中
            //ftpClientPool.returnObject(ftpClient);
            try{

                if (fos != null) {
                    fos.flush();
                }
                if (fos != null) {
                    fos.close();
                }

                ftpClientFactory.destroyFtp(ftpClient);

//                ftpClientPool.returnObject(ftpClient);
            } catch (Exception e) {
                log.error("FTP线程销毁失败：{}", e);
            }
        }
        return result;
    }

    /**
     * 删除文件
     *
     * @param remotePath FTP服务器保存目录
     * @param fileName   要删除的文件名称
     * @return true or false
     */
    public boolean deleteFile(String remotePath, String fileName) {
        FTPClient ftpClient = null;
        try {
//            ftpClient = ftpClientPool.borrowObject();
            ftpClient = ftpClientFactory.create();
            // 验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.warn("ftpServer refused connection, replyCode:{}", replyCode);
                return false;
            }
            // 切换FTP目录
            ftpClient.changeWorkingDirectory(remotePath);
            int delCode = ftpClient.dele(fileName);
            log.info("delete file reply code:{}", delCode);
            return true;
        } catch (Exception e) {
            log.error("delete file failure! {}", e);
        } finally {
            //ftpClientPool.returnObject(ftpClient);
            try{
                ftpClientFactory.destroyFtp(ftpClient);
//                ftpClientPool.returnObject(ftpClient);
            }  catch (Exception e) {
                log.error("ftpClient 销毁失败：{}", e);
            }
        }
        return false;
    }

    /***
     * 获取指定目录下文件清单
     *
     * @param remotePath ftp路径
     * @return 文件清单
     */
    public Set<FileElement> listFiles(String remotePath) throws IOException {

        log.info("ftp listFiles path :{}" ,remotePath);
        Set<FileElement> result = new LinkedHashSet<FileElement>();
        FTPFile[] files = null;
        FTPClient ftpClient = null;
        try {
            //从池中获取对象
//            ftpClient = ftpClientPool.borrowObject();
            ftpClient = ftpClientFactory.create();
            log.info("listFiles ftpClient is:{}", ftpClient);
//            ftpClient = ftpClientFactory.create();
            // 验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.warn("ftpServer refused connection, replyCode:{}", replyCode);
            } else {
                ftpClient.enterLocalPassiveMode();
                // 改变工作路径
                ftpClient.changeWorkingDirectory("/");
                files = ftpClient.listFiles(remotePath);
                log.info("listFiles files is:{}", files.length);
                if (files != null) {
//                    log.info("listFiles files-len is:{}", files.length);
                    for (FTPFile file : files) {
                        result.add(new DEPFile(file));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            log.error("file not found!{}", remotePath);
            throw new IOException(e);
        } catch (Exception e) {
            log.error("list files failure! {}", e);
            throw new IOException(e);
        } finally {
            //将对象放回池中
            //ftpClientPool.returnObject(ftpClient);
//        	ftpClient.disconnect();
//        	ftpClient.quit();
            try{
                // 直接销毁FTP
                ftpClientFactory.destroyFtp(ftpClient);
//                ftpClientPool.returnObject(ftpClient);
            }  catch (Exception e) {
                log.error("ftpClient 销毁失败：{}", e);
            }
        }
        return result;
    }


    /**
     * 在服务器上创建一个文件夹
     *
     * @param dir
     *            文件夹名称，不能含有特殊字符，如 \ 、/ 、: 、* 、?、 "、 <、>...
     */
    public boolean makeDirectory(String dir) {
        FTPClient ftpClient = null;
        boolean flag = true;
        try {
            // System.out.println("dir=======" dir);
            //从池中获取对象
//            ftpClient = ftpClientPool.borrowObject();
            ftpClient = ftpClientFactory.create();
            makeDirectory(ftpClient, dir);
        } catch (Exception e) {
            log.warn("make Directory " +dir+ " error：{}" + e);
        } finally {
            //将对象放回池中
            //ftpClientPool.returnObject(ftpClient);
            //        	ftpClient.disconnect();
            //        	ftpClient.quit();
            try{
                ftpClientFactory.destroyFtp(ftpClient);
//                ftpClientPool.returnObject(ftpClient);
            }  catch (Exception e) {
                log.error("ftpClient 销毁失败：{}", e);
            }
        }
        return flag;
    }


    /**
     *@描述 创建多级文件夹
     *@参数  [ftpClient, dir] ftpClient ftp对象，dir 文件夹
     *@返回值  boolean
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/3/21
     *@修改人和其它信息
     */
    public boolean makeDirectory(FTPClient ftpClient, String dir) {

        boolean flag = true;

        // 创建多层文件夹
        StringTokenizer s = new StringTokenizer(dir, "/");

        s.countTokens();

        String pathName = "";

        while (s.hasMoreElements()) {

            pathName = pathName + "/" + (String) s.nextElement();

            try {

                ftpClient.mkd(pathName);
//                log.info("make Directory " +pathName +" succeed");

            } catch (Exception e) {

                log.warn("make Directory " +pathName+ " false" + e.getMessage());
            }

        }
        return flag;
    }

    /**
     *@描述 移动文件
     *@参数  [oldPath, newDir, newName]
     * @param oldPath 被移除文件地址
     * @param newDir 新文件夹
     * @param newName 新的文件名称
     *@返回值  boolean
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/3/21
     *@修改人和其它信息
     */
    public boolean moveFile(String oldPath, String newDir, String newName) {
//        log.info("move ftp file from [{}] to [{}]",oldPath,newDir+newName);

        FTPClient ftpClient = null;
        boolean flag = true;
        try {
            // System.out.println("dir=======" dir);
            //从池中获取对象
//            ftpClient = ftpClientPool.borrowObject();
            ftpClient = ftpClientFactory.create();
            // 先创建新的文件夹
            makeDirectory(ftpClient,newDir);

            // 再将文件移除
            ftpClient.rename(oldPath,newDir + "/" + newName);

//            log.info("move file " + oldPath +  " success");
        } catch (Exception e) {
            log.warn("move file " + oldPath+ " error：" + e);
        } finally {
            try{
                ftpClientFactory.destroyFtp(ftpClient);
//                ftpClientPool.returnObject(ftpClient);
            }  catch (Exception e) {
                log.error("ftpClient 销毁失败：{}", e);
            }
        }
        return flag;
    }


    public boolean moveFile(FTPClient ftpClient, String oldPath, String newDir, String newName) {
//        log.info("move ftp file from [{}] to [{}]",oldPath,newDir+newName);

        boolean flag = true;
        try {
            // System.out.println("dir=======" dir);
            //从池中获取对象
//            ftpClient = ftpClientPool.borrowObject();
            // 先创建新的文件夹
            makeDirectory(ftpClient,newDir);

            // 再将文件移除
            ftpClient.rename(oldPath,newDir + "/" + newName);

//            log.info("move file " + oldPath +  " success");
        } catch (Exception e) {
            log.warn("move file " + oldPath+ " error：" + e);
        } finally {
            try{
                ftpClientFactory.destroyFtp(ftpClient);
//                ftpClientPool.returnObject(ftpClient);
            }  catch (Exception e) {
                log.error("ftpClient 销毁失败：{}", e);
            }
        }
        return flag;
    }

}
