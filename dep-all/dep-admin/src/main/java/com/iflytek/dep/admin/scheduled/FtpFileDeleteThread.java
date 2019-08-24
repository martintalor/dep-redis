package com.iflytek.dep.admin.scheduled;

import com.iflytek.dep.admin.model.FTPConfig;
import com.iflytek.dep.admin.utils.CommonConstants;
import com.iflytek.dep.admin.utils.FTPUtils;
import com.iflytek.dep.common.utils.PropertiesUtils;
import com.iflytek.dep.common.utils.SpringUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Ftp文件删除定时任务线程
 *
 * @author dzr
 */
public class FtpFileDeleteThread implements Runnable {
    private Logger logger = LoggerFactory.getLogger(FtpFileDeleteThread.class);

    FTPConfig ftpConfig;
    private static Integer ftpMinutes = null;


    public FtpFileDeleteThread(FTPConfig ftpConfig) {
        this.ftpConfig = ftpConfig;
    }

    @Override
    public void run() {
        FTPClient ftpClient = null;

        try {
            String ftpId = ftpConfig.getFtpId();
            String nodeId = ftpConfig.getNodeId();
            String ftpIp = ftpConfig.getFtpIp();
            String userName = ftpConfig.getUsername();
            String password = ftpConfig.getPassword();
            int ftpPort = ftpConfig.getFtpPort();

            ftpClient = FTPUtils.connect(ftpIp, ftpPort, userName, password);
            if (ftpClient != null) {
                PropertiesUtils propertiesUtils = SpringUtil.getBean(PropertiesUtils.class);
                deleteFiles(ftpClient, propertiesUtils.getPropertiesValue("pkg.back.path"));
                deleteFiles(ftpClient, propertiesUtils.getPropertiesValue("ack.back.path"));
                deleteFiles(ftpClient, propertiesUtils.getPropertiesValue("pkg.delete.path"));
                deleteFiles(ftpClient, propertiesUtils.getPropertiesValue("ack.delete.path"));
            }
            logger.info("\nFtpFileDeleteThread 执行成功, ftpIp=" + ftpConfig.getFtpIp());
        } catch (Exception e) {
            logger.error("\nFtpFileDeleteThread 执行失败, ftpIp=" + ftpConfig.getFtpIp() + ":" + e.getLocalizedMessage());
        } finally {
            try {
                ftpClient.disconnect();
            } catch (Exception e) {
                logger.error("FtpFileDeleteThread ftpClient.disconnect()执行失败, ftpIp=" + ftpConfig.getFtpIp(), e);
            }
        }
    }

    private void deleteFiles(FTPClient ftpClient, String dir) throws IOException {
        ftpClient.changeWorkingDirectory(dir);
        FTPFile[] ftpFile = ftpClient.listFiles(dir);
        if (ftpFile != null) {
            for (FTPFile file : ftpFile) {
                if (file.isDirectory()) {
                    String path = null;
                    if (dir.endsWith(CommonConstants.NAME.FILESPLIT)) {
                        path = dir + file.getName();
                    } else {
                        path = dir + CommonConstants.NAME.FILESPLIT + file.getName();
                    }
                    deleteFiles(ftpClient, path);
                } else {
                    // doDelete(file, ftpClient);
                    if (file.getName().contains(CommonConstants.NAME.FROM) || file.getName().contains(CommonConstants.NAME.ACK)) {
                        doDelete(file, ftpClient);
                    }
                }
            }
        }
    }

    private void deleteFiles(FTPClient ftpClient) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles();
        for (FTPFile ftpFile : ftpFiles) {
            // 判断是否dep生成的文件
            if (ftpFile.getName().contains(CommonConstants.NAME.FROM) || ftpFile.getName().contains(CommonConstants.NAME.ACK)) {
                doDelete(ftpFile, ftpClient);
            }
        }
    }

    private void doDelete(FTPFile ftpFile, FTPClient ftpClient) {
        if (ftpFile.isFile()) {
            //根据文件名称规则，获取文件创建时间，然后删除符合条件的文件
            Date timeFile = ftpFile.getTimestamp().getTime();
            long minutes = (new Date().getTime() - timeFile.getTime()) / (60 * 1000);
            if (minutes >= getMinutesInt()) {
                try {
                    ftpClient.deleteFile(ftpFile.getName());
                    logger.info("删除ftp文件成功，ftpFile.getName()=" + ftpFile.getName());
                } catch (IOException e) {
                    logger.error("删除ftp文件失败，ftpFile.getName()=" + ftpFile.getName());
                } finally {
                }
            }
        }
    }


    /**
     * 查询文件删除时间间隔
     */
    private int getMinutesInt() {
        int minutesInt = 30 * 24 * 60;//默认30天

        if (this.ftpMinutes == null) {
            PropertiesUtils propertiesUtils = SpringUtil.getBean(PropertiesUtils.class);
            String minutes = propertiesUtils.getPropertiesValue("fileDelete.minutes.ftp");

            try {
                minutesInt = Integer.valueOf(minutes);
            } catch (NumberFormatException e) {
                logger.error("FTP文件删除时间间隔天数解析失败，默认为30天");
            }

            this.ftpMinutes = minutesInt;
        }

        return this.ftpMinutes;
    }
}