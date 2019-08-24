package com.iflytek.dep.common.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by admin on 2019/2/19.
 */
public class FTPClientPool {

    private static Logger logger = LoggerFactory.getLogger(FTPClientPool.class);

    /**
     * ftp客户端连接池
     */
    private GenericObjectPool<FTPClient> pool;

    private FtpPoolConfig ftpPoolConfig;


    private FTPClientFactory ftpClientFactory;

    /**
     * 构造函数中 注入一个bean
     */
    public FTPClientPool() {
        ftpPoolConfig = new FtpPoolConfig();
        ftpClientFactory = new FTPClientFactory(ftpPoolConfig);
        pool = new GenericObjectPool<FTPClient>(ftpClientFactory, ftpPoolConfig);
    }


    /**
     * 获取一个连接对象
     *
     * @return
     * @throws Exception
     */
    public FTPClient borrowObject() throws Exception {
        FTPClient client = pool.borrowObject();
//    	if(!client.sendNoOp()){
//    		//使池中的对象无效
//    		 client.logout();
//    		 client.disconnect();
//    		 pool.invalidateObject(client);
//    		 client =clientFactory.create();
//    		 pool.addObject();
//    	 }
        return client;
    }

    /**
     * 还归还一个连接对象
     *
     * @param ftpClient
     */
    public void returnObject(FTPClient ftpClient) {
        if (ftpClient != null) {
            pool.returnObject(ftpClient);
            logger.info("活动FTPClient个数" + pool.getNumActive() + ",等待FTPClient个数" + pool.getNumWaiters());
        }
    }

}