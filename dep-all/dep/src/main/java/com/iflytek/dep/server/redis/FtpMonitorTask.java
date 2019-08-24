package com.iflytek.dep.server.redis;


import com.github.drapostolos.rdp4j.DirectoryPollerFuture;
import com.iflytek.dep.server.constants.RedisQueueType;
import com.iflytek.dep.server.ftp.FTPMonitor;
import com.iflytek.dep.server.ftp.core.FtpClientTemplate;
import com.iflytek.dep.server.ftp.listener.AckFtpListener;
import com.iflytek.dep.server.ftp.listener.PkgFtpListener;
import com.iflytek.dep.server.model.FTPConfig;
import com.iflytek.dep.server.service.dataPack.FTPService;
import com.iflytek.dep.server.ca.CaServiceImpl;
import com.iflytek.dep.server.utils.FileConfigUtil;
import com.iflytek.dep.server.utils.RedisUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.util.*;


/**
 * @program: FtpMonitorTask
 * @description: 竞争FTP监听
 * @author: dzr
 * @create: 2019-05-26
 */
@Component
@DependsOn("com.iflytek.dep.server.monitor.SpringLoadedListener")
public class FtpMonitorTask {

    private Logger logger = LoggerFactory.getLogger(FtpMonitorTask.class);

    @Value("${ftp.monitor.id}")
    private String monitorId;


    @Autowired
    private RedissonService redissonService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FTPService ftpService;

    @Autowired
    private CaServiceImpl caServiceImpl;

    @Autowired
    AckFtpListener ackFtpListener;

    @Autowired
    PkgFtpListener pkgFtpListener;

    @Autowired
    RePengPkg rePengForPkg;

    @Autowired
    RePengAc rePengForAc;

    @Autowired
    RePengACK rePengForACK;

    String upload_pengingList = RedisQueueType.PENG_UP_PKG.getCode();
    String upload_doingList = RedisQueueType.DOING_UP_PKG.getCode();

    String download_pengingList = RedisQueueType.PENG_DOWN_PKG.getCode();
    String download_doingList = RedisQueueType.DOING_DOWN_PKG.getCode();


    //每隔5秒钟轮询一次
    @Scheduled(cron = "0/5 * * * * *")
    public void ftpMonitorScheduled() {
       //logger.info("=====>>>>>使用cron  {}", System.currentTimeMillis());
//        String key = "ftpMonitor";
        RReadWriteLock readWriteLock = null;
        boolean locked = false;
        RLock lock = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress(); //获取本机ip
            readWriteLock = redissonService.getRWLock(monitorId);
            lock = readWriteLock.writeLock();
            //获取锁最多等待5秒，锁自动过期时间5分钟
            locked = lock.tryLock();
            if (locked) {
                logger.info("depServer:={} 获得FTP监听权限成功,ip:{}", monitorId , ip);
                Map<String, List<DirectoryPollerFuture>> map = startMonitor();
                long startTime = System.currentTimeMillis();
                while (true) {
                    if (!lock.isHeldByCurrentThread()) {
                        stopMonitor(map);
                        break;
                   } else {
                        logger.debug("rePendingScheduled runing:{}",Thread.currentThread().getName());
                        long endTime = System.currentTimeMillis();
                        if((endTime-startTime)>(2 * 1000))
                        {
                            startTime = System.currentTimeMillis();
                            rePengForACK.rePendingScheduled();
                            logger.debug("rePendingScheduled run end:{}",Thread.currentThread().getName());
                        }

                        if((endTime-startTime)>(5 * 1000))
                        {
                            startTime = System.currentTimeMillis();
                            rePengForPkg.rePendingScheduled();
                            rePengForAc.rePendingScheduled();
                            logger.debug("rePendingScheduled run end:{}",Thread.currentThread().getName());
                        }
                    }
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            logger.error("depServer:={} 获得FTP监听权限失败", e);
        } finally {
            if(locked){
                lock.unlock();
            }
        }
    }

    private Map<String, List<DirectoryPollerFuture>> startMonitor(Map<String, List<DirectoryPollerFuture>> map, List<FTPConfig> configs) {
        for (FTPConfig config : configs) {
            List<DirectoryPollerFuture> res = startMonitor(config);
            map.put(config.getNodeId(), res);
        }
        logger.info("Monitor ftp dir done..." + configs.size());
        return map;
    }

    private List<DirectoryPollerFuture> startMonitor(FTPConfig config) {
        List<DirectoryPollerFuture> res = new ArrayList<>();
        // 获取当前ftp
        String nodeId = config.getNodeId();
        //----------------------------------------ACK监听-------------------------
        // 创建ack监听目录
        String ackDir = config.getAckPackageFolderDown();
        // ack监听启动
        FTPMonitor moniorAck = new FTPMonitor(ackDir,
                FileConfigUtil.FTP_POLLING_INTERVAL, FtpClientTemplate.FTP_CLIENT_TEMPLATE.get(nodeId));
        DirectoryPollerFuture ackDirectoryPollerFuture = moniorAck.execute(ackFtpListener);
        //----------------------------------------pkg监听-------------------------
        // 创建pkg监听目录
        String pkgDir = config.getDataPackageFolderDown();
        FTPMonitor moniorPkg = new FTPMonitor(pkgDir,
                FileConfigUtil.FTP_POLLING_INTERVAL, FtpClientTemplate.FTP_CLIENT_TEMPLATE.get(nodeId));
        // pkg监听启动
        DirectoryPollerFuture pkgDirectoryPollerFuture = moniorPkg.execute(pkgFtpListener);
        logger.info("FTP 监听创建 end");
        res.add(ackDirectoryPollerFuture);
        res.add(pkgDirectoryPollerFuture);
        return res;
    }

    public Map<String, List<DirectoryPollerFuture>> startMonitor() {
        logger.info("Monitor ftp dir starting...");
        Map<String, List<DirectoryPollerFuture>> map = new HashMap<>();
        List<FTPConfig> configs = ftpService.selectByServerNodeId(FileConfigUtil.SERVER_NODE_ID);
        try {
            if (!CollectionUtils.isEmpty(configs)) {
                for (FTPConfig config : configs) {
                    String nodeId = config.getNodeId();
                    // 初始化文件夹
                    logger.info("init mkdir:" + nodeId);
                    initFtpDir(FtpClientTemplate.FTP_CLIENT_TEMPLATE.get(nodeId), config);
                    logger.info("init mkdir end:" + nodeId);
                }
            }
            startMonitor(map, configs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return map;
    }

    public void stopMonitor(Map<String, List<DirectoryPollerFuture>> map) {
        try {
            for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                List<DirectoryPollerFuture> list = (List<DirectoryPollerFuture>) entry.getValue();
                for (DirectoryPollerFuture future : list) {
                    future.get().stopAsyncNow();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    private void initFtpDir(FtpClientTemplate ftpClientTemplate, FTPConfig config) {
        // 创建FTP
        FTPClient ftpClient = ftpClientTemplate.getFtpClient();

        // 创建ack上传目录
        String ackDir = config.getAckPackageFolderDown();
        ftpClientTemplate.makeDirectory(ftpClient, ackDir);

        // 创建ack上传目录
        String ackUpDir = config.getAckPackageFolderUp();
        ftpClientTemplate.makeDirectory(ftpClient, ackUpDir);

        // 创建pkg下载目录
        String pkgDir = config.getDataPackageFolderDown();
        ftpClientTemplate.makeDirectory(ftpClient, pkgDir);

        // 创建pkg上传目录
        String upPkgDir = config.getDataPackageFolderUp();
        ftpClientTemplate.makeDirectory(ftpClient, upPkgDir);

        // 创建tmp目录
        String tmpDir = config.getTmpPackageFolder();
        ftpClientTemplate.makeDirectory(ftpClient, tmpDir);

        // 销毁FTP
        ftpClientTemplate.destroyFtp(ftpClient);
    }

}