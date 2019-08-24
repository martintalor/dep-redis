package com.iflytek.dep.server.redis;

import com.google.gson.Gson;
import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.server.config.redis.DepThreadFactory;
import com.iflytek.dep.server.constants.ExchangeNodeType;
import com.iflytek.dep.server.constants.RedisQueueType;
import com.iflytek.dep.server.down.PkgGetterManger;
import com.iflytek.dep.server.service.dataPack.NodeAppService;
import com.iflytek.dep.server.service.dataPack.ParseAckService;
import com.iflytek.dep.server.up.PkgUploaderManager;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.FileConfigUtil;
import com.iflytek.dep.server.utils.PackUtil;
import com.iflytek.dep.server.utils.RedisUtil;
import net.lingala.zip4j.core.ZipFile;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;


@Component
@EnableScheduling
public class CATask extends UpAndDownTask {

    private Logger logger = LoggerFactory.getLogger(CATask.class);

    final String pengingList_ca= RedisQueueType.PENG_DOWN_PKG.getCode();
    final String doingList_ca = RedisQueueType.DOING_DOWN_PKG.getCode();


    @Autowired
    PkgGetterManger pkgGetterManger;

    @Autowired
    PkgUploaderManager pkgUploaderManager;

    @Autowired
    ParseAckService parseAckService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonService redissonService;

    @Autowired
    private NodeAppService nodeAppService;


    public CATask() {
        if (threadPool == null) {
            BlockingQueue<Runnable> bq = new ArrayBlockingQueue<Runnable>(1);
            threadPool = new ThreadPoolExecutor(1, threadNumber, 50, TimeUnit.MILLISECONDS, bq, new DepThreadFactory("redis-pkg-task") {});
        }
    }


    //@Scheduled(cron = "0/5 * * * * ?")
    public void pkgTaskListScheduled() {
        try {
            logger.debug("pkgTaskListScheduled----do task >>>[{}]", Thread.currentThread().getName());
            //PKG
            //taskScheduled("上传PKG", pengingList_upload_pkg, doingList_upload_pkg, 10, UploadTaskStatus.UP_DEP_PENGING.getName(), UploadTaskStatus.UP_DEP_DOING.getName(), threadPool);

            //taskScheduled("下载PKG", pengingList_download_pkg, doingList_download_pkg, 10, DownloadTaskStatus.DOWN_DEP_PENGING.getName(), DownloadTaskStatus.DOWN_DEP_DOING.getName(), threadPool);

        } catch (Exception e) {
            logger.error("pkgTaskListScheduled----error >>>[{}]", e);
        }

    }

    public void taskScheduled(String taskType, String pengingList, String doingList, int maxLevel, String status_old, String status_new, ThreadPoolExecutor threadPool) throws Exception {
        int queueSize = threadPool.getQueue().size();
        int activeCount = threadPool.getActiveCount();
        if ((queueSize + activeCount) > threadNumber) {
            logger.debug("超过 {} 设置的线程数，退出", threadPool.getClass().getName());
            return;
        }

        Future<Boolean> result = threadPool.submit(new Worker(taskType, pengingList, doingList, maxLevel, status_old, status_new));
    }


    class Worker implements Callable<Boolean> {
        private String taskType;
        private String pengingList;
        private String doingList;
        private int maxLevel;
        private String status_old;
        private String status_new;


        public Worker(String taskType, String pengingList, String doingList, int maxLevel, String status_old, String status_new) {
            this.taskType = taskType;
            this.pengingList = pengingList;
            this.doingList = doingList;
            this.maxLevel = maxLevel;
            this.status_old = status_old;
            this.status_new = status_new;

        }

        public String getTask(String pengingListKey) {
            for (int level = 0; level < maxLevel; level++) {
                List<Object> list = redisUtil.lGet(pengingListKey + "_" + level, 0, -1);
                if (list != null && list.isEmpty()) {
                    int size = list.size();
                    String json = (String) list.get(size - 1);
                    return json;
                }
            }
            return null;
        }

        @Override
        public Boolean call() throws Exception {
            PackageInfo fileInfo=null;
            String paramMapJson=null;
            ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<>();
            try{
                String currentTimeStr = DateUtils.getDateTime();
                DoTaskParam doTaskParam = DoTaskParam.from(pengingList, doingList, maxLevel + "", status_old, status_new, currentTimeStr);
                Gson gson = new Gson();
                paramMapJson = gson.toJson(doTaskParam);
                // ----------------------------------获取任务，锁住任务----------------------------------------------
                String json = redisUtil.doTask(paramMapJson);
                if (json == null || json.equals("")) {
                    return null;
                }
                fileInfo = gson.fromJson(json, PackageInfo.class);

                paramMap.put("PACKAGE_ID", fileInfo.getPackageId());// 数据包名
                paramMap.put("NODE_ID", fileInfo.getCurNodeId());// 当前FTP节点
                paramMap.put("PACK_DIR_PATH", fileInfo.getPackDirPath());
                paramMap.put("FILE_NAME", fileInfo.getFileName());
            }
            catch(Exception e)
            {
                return null;
            }

            String packName = fileInfo.getPackageId().split("\\.")[0];
            String packageId = fileInfo.getPackageId();
            String fileName = fileInfo.getFileName();

            String filePath = fileInfo.getFilePath();

            RReadWriteLock readWriteLock = redissonService.getRWLock(packName);
            RLock lock = readWriteLock.writeLock();
            Boolean locked = false;
            try {
                locked = lock.tryLock(40, TimeUnit.SECONDS);
                if (locked) {
                    Gson gson = new Gson();
                    paramMapJson = gson.toJson(paramMap);
                    logger.info("{}任务开始 packageId:={},paramMap:={} ", taskType, packageId, paramMapJson);

                    if (taskType.equals("下载PKG")) {
                        logger.info("开始文件下载处理 packageId:={},paramMap:={}", taskType, packageId, paramMapJson);
                        verifyZip(filePath);
                        if (FileConfigUtil.ISCENTER) {
                            // 根据filename判断是否单独发往中心
                            String[] appIdTos = PackUtil.splitAppTo(fileInfo.getFileName());
                            String nodeId = nodeAppService.getNodeId(appIdTos[0]);
                            if (appIdTos.length == 1 && FileConfigUtil.CURNODEID.equals(nodeId)) {
                                pkgGetterManger.downLoadPackage(ExchangeNodeType.MAIN, fileName, null, null, paramMap);

                            } else {
                                pkgGetterManger.downLoadPackage(ExchangeNodeType.MAIN, fileName, null, null, paramMap);
                            }
                        } else {
                            pkgGetterManger.downLoadPackage(ExchangeNodeType.LEAF, fileName, null, null, paramMap);
                        }
                    } else if (taskType.equals("上传PKG")) {
                        logger.info("开始打包上传 taskType:={},packageId:={},paramMap:={}", taskType, packageId, paramMapJson);
                        //verifyZip(filePath);
                        if (FileConfigUtil.ISCENTER) {
                            pkgUploaderManager.mainUploadPackage(ExchangeNodeType.MAIN, packageId, null, null, paramMap);
                        } else {
                            pkgUploaderManager.uploadPackage(ExchangeNodeType.LEAF, packageId, null, null, paramMap);
                        }
                    }
                    String level = packName.substring(packName.length() - 1);
                    redisUtil.delTask(doingList + "_" + level, packageId);
                } else {
                    logger.info(" 获得packageId:={} 写锁 失败", packageId);
                    String dateTimeStr = DateUtils.dateFormat(new Date(), DateUtils.DATE_TIME_PATTERN);
                    String level = packName.substring(packName.length() - 1);
                    redisUtil.rePengingTask(pengingList + "_" + level, doingList + "_" + level, packageId, status_new, status_old, dateTimeStr);
                }
            } catch (Exception e) {
                logger.error("执行 packageId:={}失败", packageId, e);
                String dateTimeStr = DateUtils.dateFormat(new Date(), DateUtils.DATE_TIME_PATTERN);
                String level = packName.substring(packName.length() - 1);
                redisUtil.rePengingTask(pengingList + "_" + level, doingList + "_" + level, packageId, status_new, status_old, dateTimeStr);
            } finally {
                if (locked) {
                    lock.unlock();
                }
            }
            return true;
        }
    }

    private boolean verifyZip(String zipfile) throws Exception {
        if(zipfile==null)
        {
            return true;
        }
        boolean valid = PackUtil.isValid(zipfile.split(CommonConstants.NAME.PACKAGE_FIX)[0] + CommonConstants.NAME.ZIP);
        if (valid) {
            ZipFile zipFile2 = new ZipFile(zipfile.split(CommonConstants.NAME.PACKAGE_FIX)[0] + CommonConstants.NAME.ZIP);
            //第一时间设置编码格式
            zipFile2.setFileNameCharset("UTF-8");

            File file = new File(zipfile);
            String parentPath = file.getParent();

            int size = new File(parentPath).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String filePath = dir.getPath() + File.separator + name;
                    if(new File(filePath).isDirectory())
                    {
                        return false;
                    }
                    return true;
                }
            }).length;

            valid = (size == zipFile2.getSplitZipFiles().size());
        }
        if(!valid)
        {
            throw new RuntimeException("verifyZip校验zip文件失败");
        }
        return valid;
    }
}