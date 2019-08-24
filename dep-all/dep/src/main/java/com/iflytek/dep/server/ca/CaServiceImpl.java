package com.iflytek.dep.server.ca;


import com.google.gson.Gson;
import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.server.config.redis.DepThreadFactory;
import com.iflytek.dep.server.constants.RedisQueueType;
import com.iflytek.dep.server.file.CAService;
import com.iflytek.dep.server.mapper.CaStepRecordersMapper;
import com.iflytek.dep.server.model.CaStepRecorders;
import com.iflytek.dep.server.redis.*;
import com.iflytek.dep.server.utils.*;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

@Service
public class CaServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(CaServiceImpl.class);

    @Autowired
    CaStepRecordersMapper caStepRecordersMapper;

    private int threadNumber = 1;
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonService redissonService;

    public CaServiceImpl() {

    }

    public void saveCallBackToDb(String biz_sn, String mode, String callBackStatus) throws Exception {
        updateCaStepRecorders(biz_sn, mode, callBackStatus, new BigDecimal(0));
        CaStepRecorders caStepRecorders = caStepRecordersMapper.selectByPrimaryKey(biz_sn);
        redisUtil.pushAcCallBackTask(caStepRecorders.getPackageId(), biz_sn);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateCaStepRecorders(String biz_sn, String mode, String callBackStatus, BigDecimal executeSuccess) throws Exception {
        CaStepRecorders csr = new CaStepRecorders();
        csr.setBizSn(biz_sn);
        csr.setMode(mode);
        csr.setCallStatus(new BigDecimal(1));
        csr.setBackStatus(callBackStatus);
        csr.setExecuteStatus(executeSuccess);
        caStepRecordersMapper.updateByPrimaryKeySelective(csr);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor=Exception.class)
    public void insertSelective(CaStepRecorders csr) throws Exception {
        caStepRecordersMapper.insertSelective(csr);
    }

    public CaStepRecorders selectByPrimaryKey(String biz_sn) throws Exception {
        CaStepRecorders caStepRecorders = caStepRecordersMapper.selectByPrimaryKey(biz_sn);
        return caStepRecorders;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor=Exception.class)
    public void updateByPrimaryKeySelective(CaStepRecorders csr) throws Exception {
        caStepRecordersMapper.updateByPrimaryKeySelective(csr);
    }
}
