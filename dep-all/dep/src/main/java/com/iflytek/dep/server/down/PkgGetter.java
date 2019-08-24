package com.iflytek.dep.server.down;


import com.iflytek.dep.server.mapper.SectionStepRecordersMapper;
import com.iflytek.dep.server.model.SectionStepRecorders;
import com.iflytek.dep.server.section.SectionNode;
import com.iflytek.dep.server.service.threadPool.DepServerAnalyCommonService;
import com.iflytek.dep.server.utils.LogSectionInfo;
import com.iflytek.dep.server.utils.SectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Section任务执行调度
 *
 * @author Kevin
 */
@Service
@Scope("singleton")
public class PkgGetter {
    private final static Logger logger = LoggerFactory.getLogger(DepServerAnalyCommonService.class);

    @Autowired
    SectionUtils sectionUtils;

    @Autowired
    SectionStepRecordersMapper sectionStepRecordersMapper;


    /**
     * 数据包下载、数据包解密、数据包合并、数据包、加密、上传FTP
     *
     * @param jobId
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
    public void down(final String jobId, SectionNode sectionNode, ConcurrentHashMap<String, Object> outParam) throws Exception {

        List<SectionStepRecorders> sectionRecorderList = sectionStepRecordersMapper.getByPackageId(jobId);

        BigDecimal totalSectionNumber = SectionUtils.getTotalSectionLength(sectionNode);
        LogSectionInfo.loggerSectionChain(sectionNode);
        ConcurrentHashMap<String, Object> param = new ConcurrentHashMap();
        param.putAll(outParam);
        SectionNode sectionNodeInner = SectionUtils.checkSetionStep(sectionRecorderList, sectionNode, param);
        String packageId = String.valueOf(param.get("PACKAGE_ID"));
        logger.info("[{}] package id（from param） : {}", this.getClass(), packageId);
        if (sectionNodeInner != null) {
            //修改param传参为有PARAM信息的参数：outParam->param -- modify by jzkan 20190506
            sectionNodeInner.getCurrent().doAct(jobId, jobId, sectionNodeInner, totalSectionNumber, param);
        } else {
            logger.info("package_id:{} is deal success,unwanted retry!", jobId);
        }
    }
}

