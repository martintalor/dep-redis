package com.iflytek.dep.common.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * FTP工厂
 * <p>
 * Created by admin on 2019/2/19.
 */
public class FTPClientFactory extends BasePooledObjectFactory<FTPClient> {

    private static Logger logger = LoggerFactory.getLogger(FTPClientFactory.class);

    public FtpPoolConfig ftpPoolConfig;

    public FTPClientFactory(FtpPoolConfig ftpPoolConfig) {
        this.ftpPoolConfig = ftpPoolConfig;
    }

    /**
     * 新建FTP对象
     */
    @Override
    public FTPClient create() throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(ftpPoolConfig.getConnectTimeOut());
        try {
            logger.info("连接ftp服务器:" + ftpPoolConfig.getHost() + ":" + ftpPoolConfig.getPort());
            ftpClient.connect(ftpPoolConfig.getHost(), ftpPoolConfig.getPort());
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                logger.error("FTPServer 拒绝连接");
                return null;
            }
            boolean result = ftpClient.login(ftpPoolConfig.getUsername(), ftpPoolConfig.getPassword());
            if (!result) {
                throw new Exception("ftpClient登录失败! userName:" + ftpPoolConfig.getUsername() + ", password:"
                        + ftpPoolConfig.getPassword());
            }
            ftpClient.setControlEncoding(ftpPoolConfig.getControlEncoding());
            ftpClient.setBufferSize(ftpPoolConfig.getBufferSize());
            ftpClient.setFileType(ftpPoolConfig.getFileType());
            ftpClient.setDataTimeout(ftpPoolConfig.getDataTimeout());
            ftpClient.setUseEPSVwithIPv4(ftpPoolConfig.isUseEPSVwithIPv4());
            if (ftpPoolConfig.isPassiveMode()) {
                logger.info("进入ftp被动模式");
                ftpClient.enterLocalPassiveMode();//进入被动模式
            }
        } catch (IOException e) {
            logger.error("FTP连接失败：", e);
        }
        return ftpClient;
    }

    @Override
    public PooledObject<FTPClient> wrap(FTPClient ftpClient) {
        return new DefaultPooledObject<FTPClient>(ftpClient);
    }

    /**
     * 销毁FTP对象
     */
    @Override
    public void destroyObject(PooledObject<FTPClient> p) {
        if (null == p) {
            return;
        }
        FTPClient ftpClient = p.getObject();
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } catch (IOException io) {
            logger.error("ftpClient退出失败", io);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException io) {
                logger.error("ftpClient关闭失败", io);
            }
        }

    }

    /**
     * 验证FTP对象
     */
    @Override
    public boolean validateObject(PooledObject<FTPClient> p) {
        FTPClient ftpClient = p.getObject();
        boolean connect = false;
        try {
            connect = ftpClient.sendNoOp();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connect;
    }


    /**
     * No-op.
     *
     * @param p ignored
     */
    @Override
    public void activateObject(PooledObject<FTPClient> p) throws Exception {
        // The default implementation is a no-op.
    }

    /**
     * No-op.
     *
     * @param p ignored
     */
    @Override
    public void passivateObject(PooledObject<FTPClient> p)
            throws Exception {
        // The default implementation is a no-op.
    }
}
