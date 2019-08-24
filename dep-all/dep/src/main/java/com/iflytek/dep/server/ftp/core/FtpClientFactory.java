package com.iflytek.dep.server.ftp.core;

import com.iflytek.dep.server.ftp.config.FtpClientConfig;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.IOException;
import java.net.SocketException;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.server.ftp.core
 * @Description: FtpClientFactory
 * @date 2019/3/4--14:39
 */
public class FtpClientFactory extends BasePooledObjectFactory<FTPClient> {

    private static final Logger log = LoggerFactory.getLogger(FtpClientFactory.class);

    private FtpClientConfig config;

    public FtpClientFactory(FtpClientConfig config) {
        this.config = config;
    }

    /**
     * 创建FtpClient对象
     */
    @Override
//    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
    public FTPClient create() throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding(config.getEncoding());
        ftpClient.setConnectTimeout(config.getClientTimeout());
        ftpClient.setDataTimeout(config.getClientTimeout());
        ftpClient.setControlKeepAliveTimeout(config.getKeepAliveTimeout());
        try {
            log.debug("连接ftp服务器:"  + config.getHost() + ":" + config.getPort() + ",ftp_node_id：" + config.getNodeId());
            ftpClient.connect(config.getHost(), config.getPort());
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                ftpClient.disconnect();
                log.error("FTPServer 拒绝连接");
                return null;
            }
            if (!ftpClient.login(config.getUsername(), config.getPassword())) {
                throw new Exception("ftpClient登录失败! userName:" + config.getUsername() + ", password:"
                        + config.getPassword());
            }
            ftpClient.setBufferSize(config.getBufferSize());
            ftpClient.setFileType(config.getTransferFileType());
            if (config.isPassiveMode()) {
                ftpClient.enterLocalPassiveMode();
            }
        } catch (IOException e) {
            log.error("FTP连接失败：", e);
        }
        return ftpClient;
    }

    /**
     * 用PooledObject封装对象放入池中
     */
    @Override
    public PooledObject<FTPClient> wrap(FTPClient ftpClient) {
        return new DefaultPooledObject<>(ftpClient);
    }

    /**
     * 销毁FtpClient对象
     */
    @Override
    public void destroyObject(PooledObject<FTPClient> ftpPooled) {
        if (ftpPooled == null) {
            return;
        }
        FTPClient ftpClient = ftpPooled.getObject();
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
            } else {
                log.info("ftpClient is null");
            }

        } catch (FTPConnectionClosedException e) {
        } catch (SocketException e) {
        } catch (IOException io) {
            log.error("ftpClient退出失败 {}", io);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            } catch (IOException io) {
                log.error("ftpClient关闭失败 {}", io);
            }
        }
    }

    /**
     * 验证FtpClient对象
     */
    @Override
    public boolean validateObject(PooledObject<FTPClient> ftpPooled) {
        try {
            FTPClient ftpClient = ftpPooled.getObject();
            return ftpClient.sendNoOp();
        } catch (FTPConnectionClosedException e) {

        } catch (IOException e) {
            log.error("failed to validate client: {}", e);
        }
        return false;
    }


    /**
     * 销毁FTP
     * @param ftpClient
     */
    public void destroyFtp(FTPClient ftpClient) {
        try {
            if (ftpClient != null ) {
                ftpClient.logout();
            } else {
                log.error("[destroyFtp] ftpClient logout error, ftpClient is null");
            }
        } catch (FTPConnectionClosedException e) {
            log.error("ftpClient退出失败1", e);
        } catch (SocketException e) {
            log.error("ftpClient退出失败2", e);
        } catch (IOException io) {
            log.error("ftpClient退出失败3", io);
        } finally {

            try {
                if (ftpClient != null && ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            } catch (IOException io) {
                log.error("ftpClient关闭失败", io);
            }
        }
    }
}
