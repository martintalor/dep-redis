package com.iflytek.dep.admin.utils;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.utils
 * @Description: HttpUtil
 * @date 2019/2/27--19:20
 */
public class FTPUtils {

    private static Logger logger = LoggerFactory.getLogger(FTPUtils.class);

    /**
     * FTP连接检测
     *
     * @return
     */
    public static boolean validateConnect(String host, int port, String user, String password) {
        FTPClient ftpClient = connect(host, port, user, password);
        boolean result = false;
        try {
            if (ftpClient.sendNoOp()) {
                result = true;
            }
        } catch (Exception e) {
            logger.error("FTP连接检测失败：", e);
        } finally {
            try {
                disconnect(ftpClient);
            } catch (Exception e) {

            }
            return result;
        }
    }

    public static FTPClient connect(String host, int port, String user, String password) {
        FTPClient ftp = new FTPClient();
        // Connect to server.
        try {
            ftp.connect(host, port);
        } catch (Exception ex) {
            logger.error("\nCan't find FTP server '" + host + "'");
            return null;
        }
        try {
            // Check rsponse after connection attempt.
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                disconnect(ftp);
            }
        } catch (Exception ex) {
            logger.error("\nCan't connect to server '" + host + "'");
            return null;
        }
        try {
            // Login.
            if (!ftp.login(user, password)) {
                disconnect(ftp);
            }
            // Set data transfer mode.
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            return ftp;
        } catch (Exception ex) {
            logger.error("\nCan't login to server '" + host + "'");
            return null;
        }
    }

    /**
     * Disconnect from the FTP server
     *
     * @throws IOException on I/O errors
     */
    private static void disconnect(FTPClient ftp) throws IOException {
        if (ftp.isConnected()) {
            try {
                ftp.logout();
                ftp.disconnect();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * FTP目录权限检测
     *
     * @param remoteDir
     * @return
     */
    public static boolean validatePermission(String ftpIp, int ftpPort, String userName, String password, String remoteDir) {
        FTPClient ftpClient = connect(ftpIp, ftpPort, userName, password);
        // 初始默认有权限
        boolean result = true;
        InputStream in = null;
        String test = "permission test";
        String remoteFile = "test.txt";
        try {
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
                disconnect(ftpClient);
            } catch (Exception e) {
            }
        }
        return result;
    }
}
