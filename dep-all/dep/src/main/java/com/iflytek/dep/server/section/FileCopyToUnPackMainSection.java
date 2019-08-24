package com.iflytek.dep.server.section;


import com.google.gson.Gson;
import com.iflytek.dep.server.constants.PkgStatus;
import com.iflytek.dep.server.mapper.DataNodeProcessBeanMapper;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.service.dataPack.CreatePackService;
import com.iflytek.dep.server.service.dataPack.UpStatusService;
import com.iflytek.dep.server.utils.*;
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
public class FileCopyToUnPackMainSection implements Section, Status {
    private final static Logger logger = LoggerFactory.getLogger(FileCopyToUnPackMainSection.class);
    @Autowired
    CreatePackService createPackService;
    @Autowired
    UpStatusService upStatusService;
    @Autowired
    DataNodeProcessBeanMapper dataNodeProcessBeanMapper;
    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;
    @Autowired
    SectionUtils sectionUtils;

    @Override
    public void doAct(final String pkgId, String jobId, SectionNode sectionNode, BigDecimal totalSectionNumber, ConcurrentHashMap<String, Object> map) throws Exception {
        logger.info("[{}] job has [{}],package id [{}] in method ,jobId is [{}]",this.getClass(), Thread.currentThread().getName() ,pkgId,jobId);

        sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));

        //得到参数
        List<ConcurrentHashMap<String, Object>> param = (List<ConcurrentHashMap<String, Object>>) map.get("PARAM");

        boolean result = true;

        map.put("UNPACK_MAIN","FALSE");

        //循环param，判断目标是否有中心节点，如果有中心节点，则复制解密后的压缩包，进行解压，否则不做任何操作
        for (Object item : param) {
            Gson gson = new Gson();
            ConcurrentHashMap<String, Object> s = gson.fromJson(gson.toJson(item), ConcurrentHashMap.class);
            //参数准备
            String filePath = String.valueOf(s.get("FILE_PATH"));
            String packageId = String.valueOf(s.get("PACKAGE_ID"));
            String nodeID = String.valueOf(s.get("TO_NODE_ID"));
            //构造更新参数
            File file = new File(filePath);
            String parentDir = file.getParent();

            //创建中心节点复制解密后压缩包
            if (FileConfigUtil.CURNODEID.equals(nodeID)) {

                // 创建解压缩文件夹
                PackUtil.makeDir(parentDir + CommonConstants.NAME.FILESPLIT + "unpack");

                //新包路径
                String newPath = parentDir + CommonConstants.NAME.FILESPLIT + "unpack" + CommonConstants.NAME.FILESPLIT + packageId;

                //copy解密后文件
                FileUtil.copyZipRetry(filePath, newPath);
                logger.info("----------------复制成功");

                //ETL中心入库重试时，先清除UNPACK的文件夹（如果存在）
                String unPackedDir = newPath.split(CommonConstants.NAME.PACKAGE_FIX)[0];
                map.put("CLEAN_UNPACK_PATH",unPackedDir);
                map.put("UNPACK_MAIN","TRUE");
                map.put("UNPACK_MAIN_PATH",newPath);
//                    FileUtil.delFolder(unPackedDir);

                break;
            }
        } // end for

        // copy成功
        if( result ){
            sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
            SectionNode nextSectionNode = sectionNode.getNext();
            if(nextSectionNode!= null){
                nextSectionNode.getCurrent().doAct(pkgId,jobId,nextSectionNode,totalSectionNumber,map);
            }
        }

    }


    @Override
    public void update(String pkgId, PkgStatus status) {
        // update pkg status here

    }
}
