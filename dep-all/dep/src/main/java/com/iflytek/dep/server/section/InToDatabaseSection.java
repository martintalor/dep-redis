package com.iflytek.dep.server.section;


import com.iflytek.dep.common.exception.BusinessErrorException;
import com.iflytek.dep.common.utils.CommonConstants;
import com.iflytek.dep.server.constants.ExceptionState;
import com.iflytek.dep.server.constants.PkgStatus;
import com.iflytek.dep.server.constants.RedisQueueType;
import com.iflytek.dep.server.mapper.FTPConfigMapper;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.service.dataPack.AckRetryService;
import com.iflytek.dep.server.service.threadPool.DepServerFtpFileBackService;
import com.iflytek.dep.server.utils.FileConfigUtil;
import com.iflytek.dep.server.utils.PackUtil;
import com.iflytek.dep.server.utils.RedisUtil;
import com.iflytek.dep.server.utils.SectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.section
 * @Description:
 * @date 2019/4/17--17:00
 */
@Service
public class InToDatabaseSection implements Section, Status {
    private static Logger logger = LoggerFactory.getLogger(InToDatabaseSection.class);
    @Autowired
    FTPConfigMapper fTPConfigMapper;

    @Autowired
    DepServerFtpFileBackService depServerFtpFileBackService;

    //@Autowired
    //SectionStepRecordersMapper sectionStepRecordersMapper;

    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;

    @Autowired
    SectionUtils sectionUtils;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private Environment environment;

    @Autowired
    AckRetryService ackRetryService;

    String pengingList = RedisQueueType.PENG_DOWN_PKG.getCode();
    String doingList = RedisQueueType.DOING_DOWN_PKG.getCode();


    @Override
    public void doAct(final String pkgId, String jobId, SectionNode sectionNode, BigDecimal totalSectionNumber, ConcurrentHashMap<String, Object> map) throws Exception {

        logger.info("[{}] , package id [{}] running", this.getClass(), pkgId);
        sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber, new BigDecimal(0));
        SectionNode nextSectionNode = sectionNode.getNext();

        boolean valid = isTarget(pkgId);

        // 如果不是目标节点，直接执行后续操作
        if (!valid) {
            sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber, new BigDecimal(0));
            if (nextSectionNode != null) {
                nextSectionNode.getCurrent().doAct(pkgId, jobId, nextSectionNode, totalSectionNumber, map);
            }
            logger.info("[{}] , package id [{}] run end ", this.getClass(), pkgId);
            return;
        }

        String packageId = pkgId.split(com.iflytek.dep.server.utils.CommonConstants.NAME.PACKAGE_FIX)[0];
        String path = environment.getProperty("packed.dir") + "/" + packageId.split("\\.")[0].replaceAll(com.iflytek.dep.server.utils.CommonConstants.NAME.APPSPLIT, "") + "/unpack/" + packageId.split("\\.")[0].replaceAll(com.iflytek.dep.server.utils.CommonConstants.NAME.APPSPLIT, "");

        // ack探测代码精简
        File inPathDirectory = new File(path);
        if (!inPathDirectory.exists() || !inPathDirectory.isDirectory()) {
            throw new BusinessErrorException("inPath{}不存在，或不是目录！", path);
        }

        File[] fileList = inPathDirectory.listFiles();
        if (fileList.length == 0) {
            logger.error("路径{}下没有文件!", path);
            throw new BusinessErrorException("路径{}下没有文件!", path);
        }


        //包中是否又ack后缀额包如果有就就判定这个包是ack重探的包
        boolean ackRetry = false;
        for(File file : fileList){
            if(file.getName().endsWith("ack")){
                ackRetry = true;
                break;
            }
        }

        if (ackRetry) {
            try {

                // ack重探
                ackRetryService.parsing(path , packageId);

            } catch (Exception e) {
                logger.error("ack重探包解析失败,error:{}",e);
                throw e;
            }
        } else {

            try {

                /// 存储redis队列downloadTaskList-etl失败
                notifyEtl(pkgId,path,fileList);

            } catch (Exception e) {
                logger.error("存储redis队列downloadTaskList-etl失败,error:{}",e);
                throw e;
            }
        }

        // 除非报错，无论入不入库，不影响后续流程
        sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber, new BigDecimal(0));
        if (nextSectionNode != null) {
            nextSectionNode.getCurrent().doAct(pkgId, jobId, nextSectionNode, totalSectionNumber, map);
        }
        logger.info("[{}] , package id [{}] run end ", this.getClass(), pkgId);

    }

    // 判断是否目标节点，否则不执行此section操作
    private boolean isTarget(String pkgId) {

        // 可以只查一次数据，根据toApps查出所有nodeId，结果数据可以是多行则循环，拼接成一行则包含比较
        // 数据也可以查询缓存，无需查询数据库

        String[] toApps = PackUtil.splitAppTo(pkgId);
        NodeAppBean nodeAppBean = null;
        for (String itemApp : toApps) {
            nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(itemApp);
            if (FileConfigUtil.CURNODEID.equals(nodeAppBean.getNodeId())) {
                // 只有目标节点是当前节点才会通知etl入库
                return true;
            }
        }

        return false;
    }

    // ETL入库
    private void notifyEtl(String fileName,String path,File[] fileList) throws Exception {
        logger.info("------------------存入redis队列，通知etl入库 开始：{}", fileName);
        //查找injob
        String jobName = "";
        for (File file : fileList) {
            if (file.getName().endsWith(CommonConstants.ETL_FILE_SUFFIX.IN_JOB)) {
                jobName = "in-" + file.getName().substring(0, file.getName().lastIndexOf(CommonConstants.ETL_FILE_SUFFIX.IN_JOB));//不拼接.kjb扩展名
                break;
            }
        }

        /*if (StringUtils.isBlank(jobName)) {
            logger.error("找不到hd文件, path=" + path);
            throw new BusinessErrorException("找不到hd文件, path=", path);
        }*/

        String packName = fileName.split("\\.")[0];
        // 写入redis任务，等待ETL入库
        redisUtil.pushEtlTask(path,packName,jobName);

        logger.info("------------------存入redis队列，通知etl入库 成功：{}", fileName);
    }



    @Override
    public void update(String pkgId, PkgStatus status) {

    }
}

