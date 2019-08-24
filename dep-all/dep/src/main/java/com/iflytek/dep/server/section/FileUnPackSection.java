package com.iflytek.dep.server.section;


import com.google.gson.Gson;
import com.iflytek.dep.common.exception.BusinessErrorException;
import com.iflytek.dep.common.pack.Zip4JUtil;
import com.iflytek.dep.server.constants.ExceptionState;
import com.iflytek.dep.server.constants.PkgStatus;
import com.iflytek.dep.server.constants.RecvSendStateEnum;
import com.iflytek.dep.server.mapper.DataNodeProcessBeanMapper;
import com.iflytek.dep.server.mapper.PackageGlobalStateBeanMapper;
import com.iflytek.dep.server.service.dataPack.CreatePackService;
import com.iflytek.dep.server.service.dataPack.UpStatusService;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.PackUtil;
import com.iflytek.dep.server.utils.SectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FileUnPackSection implements Section, Status {
    @Autowired
    CreatePackService createPackService;
    @Autowired
    UpStatusService upStatusService;
    @Autowired
    DataNodeProcessBeanMapper dataNodeProcessBeanMapper;
    @Autowired
    PackageGlobalStateBeanMapper globalStateBeanMapper;
    @Autowired
    SectionUtils sectionUtils;

    private static Logger logger = LoggerFactory.getLogger(FileUnPackSection.class);

    @Override
    public void doAct(final String pkgId, String jobId, SectionNode sectionNode, BigDecimal totalSectionNumber, ConcurrentHashMap<String, Object> map) throws Exception {
        logger.info("[{}] job has [{}],pkgId id [{}] in method ,jobId is [{}]",this.getClass(),Thread.currentThread().getName() ,pkgId,jobId);
        sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
        //整理更新参数
        ConcurrentHashMap<String, Object> map2 = new ConcurrentHashMap<String, Object>();
        //操作开始时间
        BigDecimal start = null;
        //控制节点参数
        boolean isVaild = true;
        try {
            //获取解压路径
            List<ConcurrentHashMap<String, Object>> param = (List<ConcurrentHashMap<String, Object>>) map.get("PARAM");
            Gson gson = new Gson();
            ConcurrentHashMap<String, Object> paramMap = gson.fromJson(gson.toJson(param.get(0)), ConcurrentHashMap.class);
            String packPath = String.valueOf(paramMap.get("FILE_PATH"));
            File file = new File(packPath);
            String parent = file.getParent();
            String packageId = file.getName();

            //解压操作如果包不完整就会抛出异常
            String globalPackage = packageId.split(CommonConstants.NAME.PACKAGE_FIX)[0];
            String mainPackageId = globalPackage + CommonConstants.NAME.ZIP;
            //再解压前先将主包插入解压中数据
            map2.put("NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
            map2.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.JYZ);
            map2.put("PACKAGE_ID", mainPackageId);
            map2.put("TO_NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
            upStatusService.updateCurState(map2);
            //操作开始时间
            start = new BigDecimal(System.currentTimeMillis());
            logger.info("开始解压：" + mainPackageId);
            //createPackService.unZip(packPath.split("\\.")[0] + CommonConstants.NAME.ZIP);
            String sourceDir = packageId.split(CommonConstants.NAME.PACKAGE_FIX)[0];
            String toDir = packageId.split(CommonConstants.NAME.PACKAGE_FIX)[0].replaceAll(CommonConstants.NAME.APPSPLIT,"");
            if(!new File(parent + File.separator + toDir).exists())
            {
                Zip4JUtil.Unzip4j(packPath.split(CommonConstants.NAME.PACKAGE_FIX)[0] + CommonConstants.NAME.ZIP);
                //解压成功后将文件夹名称中间的","去掉，否则传入到job执行时不识别路径
                PackUtil.renameFile(parent,sourceDir,toDir);

                logger.info("解压成功：" + mainPackageId);
            }
            // 成功后传递主包id为packageId，方便移除
            map.put("PACKAGE_ID", mainPackageId);

            //操作结束时间
            BigDecimal end = new BigDecimal(System.currentTimeMillis());
            //计算耗时
            BigDecimal spendTime = end.subtract(start).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP);

            //往当前状态和操作状态表里插入数据
            // 成功只更新主包
            map2.put("NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
            map2.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.JY);
            map2.put("PACKAGE_ID", mainPackageId);
            map2.put("TO_NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
            map2.put("SPEND_TIME",spendTime);
            upStatusService.updateCurState(map2);

        } catch (Exception e) {
            // 失败更新所有分包
            logger.error("压缩包不完整或解压失败{}",e);
            map2.put("SEND_STATE_DM", RecvSendStateEnum.FAIL.getStateCode());
            //操作结束时间
            BigDecimal end = new BigDecimal(System.currentTimeMillis());
            //计算耗时
            BigDecimal spendTime = end.subtract(start).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP);
            //往当前状态和操作状态表里插入数据
            map2.put("NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
            map2.put("PACKAGE_ID", pkgId);
            map2.put("TO_NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
            map2.put("SPEND_TIME",spendTime);
            upStatusService.updateCurState(map2);
            isVaild = false;
            //throw new BusinessErrorException(ExceptionState.UNPACK.getCode(), ExceptionState.UNPACK.getName() + packageId);
        }

//                    return SectionResult(isVaild, map);
        if(isVaild){
            sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
            SectionNode nextSectionNode = sectionNode.getNext();
            if(nextSectionNode!= null){
                nextSectionNode.getCurrent().doAct(pkgId,jobId,nextSectionNode,totalSectionNumber,map);
            }
        }
        else{
            throw new BusinessErrorException(ExceptionState.UNPACK.getCode(),ExceptionState.UNPACK.getName() + "解压失败！pkgId:"+ pkgId);
        }
    }

    @Override
    public void update(String pkgId, PkgStatus status) {
        // update pkg status here

    }


}
