package com.iflytek.dep.admin.scheduled;

import com.iflytek.dep.admin.model.FTPConfig;
import com.iflytek.dep.admin.utils.CommonConstants;
import com.iflytek.dep.admin.utils.FTPUtils;
import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.common.utils.PropertiesUtils;
import com.iflytek.dep.common.utils.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.scheduled
 * @Description:
 * @date 2019/4/13--16:25
 */
public class FtpFileMoveThread implements Runnable{

    private Logger logger = LoggerFactory.getLogger(FtpFileDeleteThread.class);

    FTPConfig ftpConfig;
    private static Integer ftpMinutes = null;


    public FtpFileMoveThread(FTPConfig ftpConfig) {
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
            PropertiesUtils propertiesUtils = SpringUtil.getBean(PropertiesUtils.class);
            if (ftpClient != null) {
                if (StringUtils.isNotBlank(ftpConfig.getDataPackageFolderUp())) {
                    if (ftpClient.changeWorkingDirectory(ftpConfig.getDataPackageFolderUp())) {
                        // 移动data包
                        // 测试 ftpClient.changeWorkingDirectory("/yftao/");
                        moveFiles(ftpClient, propertiesUtils.getPropertiesValue("pkg.delete.path"));
                    }
                }
                if (StringUtils.isNotBlank(ftpConfig.getAckPackageFolderUp())) {
                    if (ftpClient.changeWorkingDirectory(ftpConfig.getAckPackageFolderUp())) {
                        // 移动ack包
                        // 测试 ftpClient.changeWorkingDirectory("/yftao1/");
                        moveFiles(ftpClient, propertiesUtils.getPropertiesValue("ack.delete.path"));
                    }
                }
            }
            logger.info("\nFtpFileMoveThread 执行成功, ftpIp=" + ftpConfig.getFtpIp());
        } catch (Exception e) {
            logger.error("\nFtpFileMoveThread 执行失败, ftpIp=" + ftpConfig.getFtpIp() + ":" + e.getLocalizedMessage());
        } finally {
            try {
                ftpClient.disconnect();
            } catch (Exception e) {
                logger.error("FtpFileMoveThread ftpClient.disconnect()执行失败, ftpIp=" + ftpConfig.getFtpIp(), e);
            }
        }
    }

    private void moveFiles(FTPClient ftpClient, String dir) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles();
        for (FTPFile ftpFile : ftpFiles) {
            // 判断是否dep生成的文件
            if (ftpFile.getName().contains(CommonConstants.NAME.FROM) || ftpFile.getName().contains(CommonConstants.NAME.ACK)) {
                doMove(ftpFile, ftpClient, dir);
            }
        }
    }

    private void doMove(FTPFile ftpFile, FTPClient ftpClient, String dir) {
        //根据文件名称规则，获取文件创建时间，然后移动符合条件的文件
        Date timeFile = ftpFile.getTimestamp().getTime();
        long minutes = (new Date().getTime() - timeFile.getTime()) / (60 * 1000);
        if (minutes >= getMinutesInt()) {
            try {
                String toDir = dir + DateUtils.getTodaySN();
                // 创建目录
                makeDirectory(ftpClient, toDir);
                // 移动到新路径下
                ftpClient.rename(ftpFile.getName(), toDir + CommonConstants.NAME.FILESPLIT + ftpFile.getName());
                logger.error("移动ftp文件成功，ftpFile.getName()=" + ftpFile.getName());
            } catch (IOException e) {
                logger.error("移动ftp文件失败，ftpFile.getName()=" + ftpFile.getName());
            } finally {
            }
        }
    }

    public void makeDirectory(FTPClient ftpClient, String dir) {
        // 创建目录
        StringTokenizer s = new StringTokenizer(dir, CommonConstants.NAME.FILESPLIT);
        s.countTokens();
        String pathName = "";
        while (s.hasMoreElements()) {
            pathName = pathName + CommonConstants.NAME.FILESPLIT + s.nextElement();
            try {
                ftpClient.mkd(pathName);
            } catch (Exception e) {
                logger.warn("make Directory " + pathName + " false" + e.getMessage());
            }

        }
    }

    /**
     * 查询文件删除时间间隔
     */
    private int getMinutesInt() {
        int minutesInt = 7 * 24 * 60;//默认7天
        if (this.ftpMinutes == null) {
            PropertiesUtils propertiesUtils = SpringUtil.getBean(PropertiesUtils.class);
            String minutes = propertiesUtils.getPropertiesValue("fileMove.minutes.ftp");
            try {
                minutesInt = Integer.valueOf(minutes);
            } catch (NumberFormatException e) {
                logger.error("FTP文件移走时间间隔天数解析失败，默认为7天");
            }
            this.ftpMinutes = minutesInt;
        }
        return this.ftpMinutes;
    }
}