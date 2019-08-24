package com.iflytek.dep.server.scheduled;

import com.iflytek.dep.server.constants.LevelEnum;
import com.iflytek.dep.server.controller.CreatePackController;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.model.UnfinishedPack;
import com.iflytek.dep.server.redis.RedissonService;
import com.iflytek.dep.server.service.dataPack.CreatePackService;
import com.iflytek.dep.server.service.dataPack.GetPackService;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.FileConfigUtil;
import com.iflytek.dep.server.utils.FileUtil;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.scheduled
 * @Description:
 * @date 2019/6/4--22:17
 */
public class AckRetryThread implements Runnable {
    private Logger logger = LoggerFactory.getLogger(AckRetryThread.class);


    private GetPackService getPackService;


    private NodeAppBeanMapper nodeAppBeanMapper;


    private CreatePackController createPackController;


    private CreatePackService createPackService;


    private RedissonService redissonService;

    public AckRetryThread( GetPackService getPackService, NodeAppBeanMapper nodeAppBeanMapper, CreatePackController createPackController, CreatePackService createPackService, RedissonService redissonService) {
        this.getPackService = getPackService;
        this.nodeAppBeanMapper = nodeAppBeanMapper;
        this.createPackController = createPackController;
        this.createPackService = createPackService;
        this.redissonService = redissonService;
    }

    @Override
    public void run() {
        //查询出所有处理中的包名
        List<UnfinishedPack> unfinishedPackageList = getPackService.getUnfinishedPackageList(FileConfigUtil.CURNODEID);
        //查出nodeapp中appid对应的nodeid
        List<NodeAppBean> nodeAppBeans = nodeAppBeanMapper.selectAll();
        //appIdFrom
        String appIdFrom = null;
        for (NodeAppBean nodeAppBean : nodeAppBeans) {
            if (FileConfigUtil.CURNODEID.equals(nodeAppBean.getNodeId())) {
                appIdFrom = nodeAppBean.getAppId();
            }
        }
        //创建存放所有参数的map
        Map<String, Map> paramMap = new ConcurrentHashMap<String, Map>();
        RReadWriteLock readWriteLock = null;
        boolean locked = false;
        RLock lock = null;
        //分批次将自己节点待处理的包分别写入对应文件中
        for (UnfinishedPack unfinishedPack : unfinishedPackageList) {

            try {
                readWriteLock = redissonService.getRWLock(unfinishedPack.getPackageId());
                lock = readWriteLock.writeLock();
                //获取锁最多等待5秒，锁自动过期时间5分钟
                locked = lock.tryLock();
                if (locked) {

                    for (NodeAppBean nodeAppBean : nodeAppBeans) {
                        if (unfinishedPack.getToNodeId().equals(nodeAppBean.getNodeId())) {
                            //创建存放一次参数的map
                            Map<String, Object> fileDir = new ConcurrentHashMap<String, Object>();
                            String appIdTo = nodeAppBean.getAppId();
                            try {
                                if (paramMap.get(appIdTo) != null) {
                                    fileDir = paramMap.get(appIdTo);
                                } else {
                                    fileDir = createPackController.getFileDir(appIdFrom, appIdTo, String.valueOf(LevelEnum.LEVEL0.getValue() ));
                                    paramMap.put(appIdTo, fileDir);
                                }
                                //获取到存放目录
                                String path = (String) fileDir.get("path");
                                //构造出此目录下需要创建的文件
                                String filePath = path + CommonConstants.NAME.FILESPLIT + unfinishedPack.getToNodeId() + ".ack";

                                File file = new File(filePath);
                                //将对应包写入目标节点的文件
                                FileUtil.writeFile(file, unfinishedPack.getPackageId());
                            } catch (Exception e) {
                                logger.error("",e);
                                e.printStackTrace();
                            }
                        }
                    }
                }
                logger.info("ack重探包生成");
            } catch (Exception e) {
                logger.error("ack重探打包失败",e);
                e.printStackTrace();
            } finally {
                if(locked){
                    lock.unlock();
                }
            }

        }


        //创建完成后分别将每个节点打包发出
        for (Map<String, Object> value : paramMap.values()) {
            String path = (String) value.get("path");
            String fileName = (String) value.get("fileName");
            try {

                // ? 中心节点怎么办?
                createPackController.doZipJob(path, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
