package com.iflytek.dep.server.service.impl;

import com.iflytek.dep.server.constants.ExchangeNodeType;
import com.iflytek.dep.server.down.PkgGetterManger;
import com.iflytek.dep.server.mapper.SectionStepRecordersMapper;
import com.iflytek.dep.server.model.SectionStepRecorders;
import com.iflytek.dep.server.service.dataPack.GetPackService;
import com.iflytek.dep.server.utils.FileConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ETL相关service
 * Created by xiliu5 on 2019/4/27.
 * @Deprecated 不用了
 */
@Service
public class EtlServiceImpl {

    private Logger logger = LoggerFactory.getLogger(EtlServiceImpl.class);

    /**
     * 时间区间
     * <p>
     * 30分钟
     */
    private static final long INFO_TIME_INTERVAL = 30 * 1000 * 60;

    @Autowired
    GetPackService getPackService;

    @Autowired
    PkgGetterManger pkgGetterManger;

    @Autowired
    SectionStepRecordersMapper sectionStepRecordersMapper;

    /**
     * 每隔30分钟查询未通知到ETL的数据包，然后尝试调用ETL入库接口
     */
    //@Scheduled(initialDelay = 2000, fixedDelay = INFO_TIME_INTERVAL)
    public void notifyEtl() throws Exception {
        List<SectionStepRecorders> list = sectionStepRecordersMapper.getNeedEtlInfoRecords();

        for (SectionStepRecorders recorder : list) {
            String fileName = recorder.getPackageId();
            if (FileConfigUtil.ISCENTER) {
                try{
                    if(fileName != null){
                        //按照包名称做ETL调用重试，删除数据库多余数据（过个分包，只处理单个包）
                        fileName = fileName.trim();
                    }
                    //删除数据库中，解密后的环节
                    sectionStepRecordersMapper.deleteFailSectionStepRecorders(fileName);
                    logger.info("数据包：{}，ETL调用重试，删除数据库多余数据。",fileName);
                } catch (Exception e) {
                    logger.error("中心节点：ETL定时任务重试前准备错误！", e);
                }
            }
        }

        for (SectionStepRecorders recorder : list) {
            String fileName = recorder.getPackageId();
            if (FileConfigUtil.ISCENTER) {
                logger.info("中心节点通知ETL入库-------压缩包：" + fileName + "开始重试");
                execute(fileName, ExchangeNodeType.MAIN);
                logger.info("中心节点通知ETL入库-------压缩包：" + fileName + "重试成功");
            } else {
                logger.info("叶子节点通知ETL入库-------压缩包：" + fileName + "开始重试");
                execute(fileName, ExchangeNodeType.LEAF);
                logger.info("叶子节点通知ETL入库-------压缩包：" + fileName + "重试成功");
            }
        }
    }

    //不需要再重试
    private void execute(String fileName, ExchangeNodeType exchangeNodeType) throws Exception {
        // 传递参数
        ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<String, Object>();
        paramMap.put("PACKAGE_ID", fileName);// 数据包名
        //paramMap.put("NODE_ID", curNodeId);// 当前FTP节点
        //调用下载链路
        pkgGetterManger.downLoadPackage(exchangeNodeType, fileName, null, null, paramMap);
    }

}
