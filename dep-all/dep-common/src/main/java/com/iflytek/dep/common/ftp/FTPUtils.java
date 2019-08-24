package com.iflytek.dep.common.ftp;

import com.github.drapostolos.rdp4j.spi.FileElement;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * FTP工具类
 * <p>
 * Created by admin on 2019/2/19.
 */
public class FTPUtils {

    private static Logger logger = LoggerFactory.getLogger(FTPUtils.class);

    private static FTPClientPool ftpClientPool;

    static {
        ftpClientPool = new FTPClientPool();
    }


    /**
     * FTP文件上传
     *
     * @param localFile      本地文件
     * @param remoteDir      远程上传目录
     * @param remoteFileName 上传文件名称
     * @return
     */
    public static boolean uploadFile(File localFile, String remoteDir, String remoteFileName) {
        logger.info("资源【" + localFile.getName() + "】开始上传");
        FTPClient ftpClient = null;
        boolean result = false;
        try {
            FileInputStream in = new FileInputStream(localFile);
            ftpClient = ftpClientPool.borrowObject();
            // 目录操作
            createRemoteDir(ftpClient, remoteDir);
            result = ftpClient.storeFile(remoteFileName, in);
            in.close();
            //ftpClient.disconnect();
        } catch (Exception e) {
            logger.error("FTP文件上传失败：", e);
        } finally {
            try {
                ftpClientPool.returnObject(ftpClient);
            } catch (Exception e) {
                logger.error("归还FTP连接对象失败：", e);
            }
        }
        logger.info("资源【" + localFile.getName() + "】上传结束");
        return result;
    }

    /**
     * FTP文件下载
     *
     * @param remoteFile 远程文件
     * @param localDir   本地下载目录
     * @return
     */
    public static boolean downFile(File remoteFile, String localDir) {
        logger.info("资源【" + remoteFile.getName() + "】开始下载");
        FTPClient ftpClient = null;
        boolean result = false;
        try {
            ftpClient = ftpClientPool.borrowObject();
            ftpClient.changeWorkingDirectory(remoteFile.getParent());
            String localPath = localDir + File.separator + remoteFile.getName();
            OutputStream os = new FileOutputStream(localPath);
            result = ftpClient.retrieveFile(remoteFile.getName(), os);
            //ftpClient.logout();
            //ftpClient.disconnect();
            os.close();
        } catch (Exception e) {
            logger.error("FTP文件下载失败：", e);
        } finally {
            try {
                ftpClientPool.returnObject(ftpClient);
            } catch (Exception e) {
                logger.error("归还FTP连接对象失败：", e);
            }
        }
        logger.info("资源【" + remoteFile.getName() + "】下载完成");
        return result;
    }


    /**
     * FTP连接检测
     *
     * @return
     */
    public static boolean validateConnect() {
        FTPClient ftpClient = null;
        boolean result = false;
        try {
            ftpClient = ftpClientPool.borrowObject();
            if (ftpClient.sendNoOp()) {
                result = true;
            }
        } catch (Exception e) {
            logger.error("FTP连接检测失败：", e);
        } finally {
            try {
                ftpClientPool.returnObject(ftpClient);
            } catch (Exception e) {
                logger.error("归还FTP连接对象失败：", e);
            }
            return result;
        }
    }


    /**
     * FTP目录权限检测
     *
     * @param remoteDir
     * @return
     */
    public static boolean validatePermission(String remoteDir) {
        FTPClient ftpClient = null;
        // 初始默认有权限
        boolean result = true;
        InputStream in = null;
        String test = "permission test";
        String remoteFile = "test.txt";
        try {
            ftpClient = ftpClientPool.borrowObject();
            ftpClient.makeDirectory(remoteDir);
            in = new ByteArrayInputStream(test.getBytes());
            // 写测试，写入失败直接返回
            result = ftpClient.storeFile(remoteFile, in);
            in.close();
            if (!result) {
                return result;
            }
            // 读测试，读入失败直接返回
            InputStream input = ftpClient.retrieveFileStream(remoteFile);
            if (null == input) {
                input.close();
                return result;
            }
            // 删除测试,删除失败直接返回
            result = ftpClient.deleteFile(remoteFile);
            if (!result) {
                return result;
            }
            result = true;
        } catch (Exception e) {
            logger.error("目录权限检测失败：", e);
        } finally {
            try {
                logger.info("11111111111111111111111");
                ftpClientPool.returnObject(ftpClient);
            } catch (Exception e) {
                logger.error("归还FTP连接对象失败：", e);
            }
        }
        return result;
    }

    /**
     * 创建远程目录
     *
     * @param ftpClient
     * @param remoteDir
     * @throws IOException
     */
    public static void createRemoteDir(FTPClient ftpClient, String remoteDir) throws IOException {
        //根据路径逐层判断目录是否存在，如果不存在则创建
        //1.首先进入ftp的根目录
        ftpClient.changeWorkingDirectory("\\");
        String[] dirs = remoteDir.split("\\\\");
        for (String dir : dirs) {
            //2.创建并进入不存在的目录
            if (!ftpClient.changeWorkingDirectory(dir)) {
                ftpClient.mkd(dir);
                ftpClient.changeWorkingDirectory(dir);

            }
        }
    }

    /**
     * 获取FTP文件列表
     *
     * @param remoteDir
     * @return
     */
    public static Set<FileElement> listRemoteFile(String remoteDir) {
        FTPClient ftpClient = null;
        Set<FileElement> result = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            result = new LinkedHashSet<FileElement>();
            for (org.apache.commons.net.ftp.FTPFile file : ftpClient.listFiles(remoteDir)) {
                result.add(new FTPFile(file));
            }
            return result;
        } catch (Exception e) {
            logger.error("获取FTP文件列表失败：", e);
        } finally {
            try {
                logger.info("11111111111111111111111");
                ftpClientPool.returnObject(ftpClient);
            } catch (Exception e) {
                logger.error("归还FTP连接对象失败：", e);
            }
        }
        return result;
    }

}
