package com.iflytek.dep.server.section;


import com.iflytek.dep.common.exception.BusinessErrorException;
import com.iflytek.dep.common.pack.FileUtil;
import com.iflytek.dep.server.constants.ExceptionState;
import com.iflytek.dep.server.constants.PkgStatus;
import com.iflytek.dep.server.constants.RecvSendStateEnum;
import com.iflytek.dep.server.mapper.DataPackBeanMapper;
import com.iflytek.dep.server.mapper.DataPackSubBeanMapper;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.service.dataPack.CreatePackService;
import com.iflytek.dep.server.service.dataPack.UpStatusService;
import com.iflytek.dep.server.utils.*;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FilePackSection  implements Section, Status {
    @Autowired
    CreatePackService createPackService;
    @Autowired
    UpStatusService upStatusService;
    @Autowired
    DataPackBeanMapper dataPackBeanMapper;
    @Autowired
    DataPackSubBeanMapper dataPackSubBeanMapper;
    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;
    @Autowired
    SectionUtils sectionUtils;

    private final static Logger logger = LoggerFactory.getLogger(FilePackSection.class);

    @Override
    public void doAct(final String pkgId, String jobId, SectionNode sectionNode, BigDecimal totalSectionNumber, ConcurrentHashMap<String, Object> map) throws Exception {
        logger.info("[{}] job has [{}],pkgId id [{}] in method ,jobId is [{}]",this.getClass(),Thread.currentThread().getName(),pkgId,jobId);


        //压缩
        //操作开始时间
        BigDecimal start = new BigDecimal(System.currentTimeMillis());
        String path = null;
        String operaState = "";// 操作状态
        String sendState = "";// 流转状态
        List<String> toNodeId = new ArrayList<String>();
        //构造传输参数
        List<ConcurrentHashMap<String, Object>> param = new ArrayList<ConcurrentHashMap<String, Object>>();
        //需要持久化参数准备
//        ConcurrentHashMap<String, Object> map1 = new ConcurrentHashMap<String, Object>();

        sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));

        // 先清空
        String packagePath = String.valueOf(map.get("PACKAGE_PATH"));
        if (StringUtil.isNotEmpty(packagePath)) {
            FileUtil.delAllFile(packagePath);
        }
        //获取需要参数
        String packDirPath = String.valueOf(map.get("PACK_DIR_PATH"));
        String fileName = String.valueOf(map.get("FILE_NAME"));
        String appIdFrom = PackUtil.splitAppFrom(fileName);
        String appIdToS = PackUtil.splitAppTos(fileName);
        String[] appIdTo = PackUtil.splitAppTo(fileName);
        String nodeId = "";

        try {
            //打包之前先在主包表中插入虚拟数据
            upStatusService.insertPage(fileName);

            //通过包名生成链路信息
            map.put("PACKAGE_ID", fileName + CommonConstants.NAME.ZIP);
            //查询需要的appid对应的当前的nodeid
            NodeAppBean nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(appIdFrom);
            nodeId = nodeAppBean.getNodeId();
            map.put("APP_ID_FROM", appIdFrom);
            //到新的节点就创建链路并且找到去往目的地其对应的nodeId

            for (String app : appIdTo) {
                map.put("APP_ID_TO", app);
                ConcurrentHashMap nodeLink = upStatusService.createNodeLink(map);
                NodeAppBean nodeApp = nodeAppBeanMapper.selectByPrimaryKey(app);
                toNodeId.add(nodeApp.getNodeId());
            }
            //在打包前先插入打包中状态
            ConcurrentHashMap<String, Object> map2 = new ConcurrentHashMap<String, Object>();
            final String nodeIds = nodeId;
            for(String s:toNodeId)
            {
                map2.put("NODE_ID", nodeIds);
                map2.put("PACKAGE_ID", fileName + CommonConstants.NAME.ZIP);
                map2.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.YSZ);
                map2.put("TO_NODE_ID", s);
                //插入数据扭转进程表和更新状态表
                //插入数据包当前状态表和数据包流水状态表
                upStatusService.updateCurState(map2);
            }
            //构造传输参数
             param = new ArrayList<ConcurrentHashMap<String, Object>>();

            path = createPackService.toZip(packDirPath, fileName);
            operaState = CommonConstants.OPERATESTATE.YS;
        } catch (ZipException e) {
            path = FileConfigUtil.PACKEDDIR + CommonConstants.NAME.FILESPLIT + fileName;
            sendState = RecvSendStateEnum.FAIL.getStateCode();
            logger.error("打包异常：" + path);
        }
        //操作结束时间
        BigDecimal end = new BigDecimal(System.currentTimeMillis());
        //计算耗时
        BigDecimal spendTime = end.subtract(start).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP);
        File oldFile = new File(path);
        //通过全路径得到压缩包文件夹
        String pagePath = oldFile.getParent();
        File newFile = new File(pagePath);
        //遍历此文件夹将文件信息入库
        for (File file : newFile.listFiles()) {
            ConcurrentHashMap<String, Object> zip = new ConcurrentHashMap<String, Object>();
            upStatusService.insertPageAndPageSubStart(file, packDirPath);
            String pageId = file.getName();
            String filePath = file.getAbsolutePath();
            String finalOperaState = operaState;
            String finalSendState = sendState;
            final String nodeIds = nodeId;

            //需要持久化参数准备
            final ConcurrentHashMap map1 = new ConcurrentHashMap<String, Object>();
            for(String s:toNodeId){
                map1.put("NODE_ID", nodeIds);
                map1.put("PACKAGE_ID", pageId);
                map1.put("OPERATE_STATE_DM", finalOperaState);
                map1.put("SEND_STATE_DM", finalSendState);
                map1.put("TO_NODE_ID", s);
                map1.put("SPEND_TIME", spendTime);
                //插入数据扭转进程表和更新状态表
                //插入数据包当前状态表和数据包流水状态表
                upStatusService.updateCurState(map1);
            }

            //构造下个section需要参数
            zip.put("FILE_PATH", filePath);
            zip.put("PACKAGE_ID", pageId);
            param.add(zip);
        }

        // 如果流转状态异常
        if (RecvSendStateEnum.FAIL.getStateCode().equals(sendState) ) {
            throw new BusinessErrorException(ExceptionState.DECRYPT.getCode(),ExceptionState.DECRYPT.getName()+ fileName);
        }

        //下个section需要的参数
        ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
        resultMap.put("TO_NODE_ID", toNodeId);
        resultMap.put("CUR_NODE_ID", nodeId);
        resultMap.put("PARAM", param);
        resultMap.put("MARK", CommonConstants.STATE.SELF);
        //验证有效性并返回结果
        boolean valid = PackUtil.isValid(path);
        if(!valid){
            logger.info("[{}]:此包主包损坏，请手动重新处理！",fileName);
            throw new BusinessErrorException(ExceptionState.DECRYPT.getCode(),ExceptionState.DECRYPT.getName()+ fileName);
        }

        if(valid){
            sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
            SectionNode nextSectionNode = sectionNode.getNext();
            if(nextSectionNode!= null){
                nextSectionNode.getCurrent().doAct(pkgId,jobId,nextSectionNode,totalSectionNumber,resultMap);
            }
        }
        else{
            throw new BusinessErrorException(ExceptionState.PACK.getCode(),ExceptionState.PACK.getName() + "压缩失败！pkgId:"+ pkgId);
        }

    }

    @Override
    public void update(String pkgId, PkgStatus status) {
        // update pkg status here

    }

}
