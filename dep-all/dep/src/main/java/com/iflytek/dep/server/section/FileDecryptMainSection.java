package com.iflytek.dep.server.section;


import com.iflytek.dep.common.exception.BusinessErrorException;
import com.iflytek.dep.common.security.DecryptException;
import com.iflytek.dep.server.ca.CaServiceImpl;
import com.iflytek.dep.server.constants.ExceptionState;
import com.iflytek.dep.server.constants.PkgStatus;
import com.iflytek.dep.server.constants.RecvSendStateEnum;
import com.iflytek.dep.server.file.CAService;
import com.iflytek.dep.server.mapper.*;
import com.iflytek.dep.server.model.CaStepRecorders;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.service.dataPack.CreatePackService;
import com.iflytek.dep.server.service.dataPack.UpStatusService;
import com.iflytek.dep.server.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FileDecryptMainSection  implements Section, Status {
    private final static Logger logger = LoggerFactory.getLogger(FileDecryptMainSection.class);
    private static ExecutorService fixedUpStreamThreadPool;
    public static AtomicInteger threadJobSize = new AtomicInteger(0);
    @Autowired
    CreatePackService createPackService;
    @Autowired
    UpStatusService upStatusService;
    @Autowired
    NodeLinkBeanMapper nodeLinkBeanMapper;
    @Autowired
    DataPackBeanMapper dataPackBeanMapper;
    @Autowired
    DataPackSubBeanMapper dataPackSubBeanMapper;
    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;
    @Autowired
    SectionUtils sectionUtils;

    @Autowired
    CaServiceImpl caServiceImpl;

    @Override
    public void doAct(final String pkgId,String jobId, SectionNode sectionNode, BigDecimal totalSectionNumber, ConcurrentHashMap<String, Object> map) throws Exception {
        logger.info("[{}] job has [{}],pkgId id [{}] in method ,jobId is [{}]",this.getClass(),threadJobSize.incrementAndGet(),pkgId,jobId);
        sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));

        if (FileConfigUtil.ISCA) {
            List<ConcurrentHashMap<String, Object>> param = new ArrayList<ConcurrentHashMap<String, Object>>();
            //获取加密包的路径
            String packPath = String.valueOf(map.get("PACK_PATH"));
            File file = new File(packPath);
            //获取包名
            String packageId = file.getName();
            //根据包名截取去那里的appID
            String[] appIdTo = PackUtil.splitAppTo(packageId);
            //到新的节点找到去往目的地其对应的nodeId
            List<String> toNodeId = new ArrayList<String>();
            for (String app : appIdTo) {
                NodeAppBean nodeApp = nodeAppBeanMapper.selectByPrimaryKey(app);
                toNodeId.add(nodeApp.getNodeId());
            }
            for (String toNode : toNodeId) {
                if (toNode.equals(FileConfigUtil.CURNODEID)) {

                    //得到解密共享目录
                    String decryptDir = FileConfigUtil.DECRYPTDIR;

                    //拷贝文件到共享目录
                    String source = decryptDir + CommonConstants.NAME.FILESPLIT + "source" + CommonConstants.NAME.FILESPLIT + packageId;
                    FileUtil.copyZipRetry(packPath, source);
                    //共享文件夹下解密完的文件路径
                    String decryptPath = decryptDir + CommonConstants.NAME.FILESPLIT + "target" + CommonConstants.NAME.FILESPLIT + packageId;
                    //准备参数
                    ConcurrentHashMap<String, Object> map1 = new ConcurrentHashMap<String, Object>();
                    map1.put("NODE_ID", FileConfigUtil.CURNODEID);
                    map1.put("PACKAGE_ID", packageId);
                    map1.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.ZXJIEMZ);
                    map1.put("TO_NODE_ID", FileConfigUtil.CURNODEID);
                    //插入数据扭转进程表和更新状态表
                    //插入数据包当前状态表和数据包流水状态表
                    upStatusService.updateCurState(map1);

                    String appcode = FileConfigUtil.APPCODE;
                    String apppwd = FileConfigUtil.APPPWD;
                    //ca接口地址
                    String url = FileConfigUtil.CAURL;
                    //ca回调接口地址
                    String callBackUrl = FileConfigUtil.CACALLBACKURL;
                    //生成biz_sn
                    String biz_sn = UUID.randomUUID().toString();
                    //生成receiver
                    String receiver = FileUtil.parsingReceiver(packageId);
                    //更新参数
                    ConcurrentHashMap<String, Object> map2 = new ConcurrentHashMap<String, Object>();

                    //解密
                    try {
                        //向表中插入调用记录(不管返回结果先插入一条再调用)
                        CaStepRecorders csr = new CaStepRecorders();
                        csr.setBizSn(biz_sn);
                        csr.setCallbackUrl(callBackUrl);
                        csr.setCallStatus(CommonConstants.CA.CALL_SUCCESS);
                        csr.setFileUrl(source);
                        csr.setEfsUrl(decryptPath);
                        csr.setPackageId(packageId);
                        csr.setMode(CommonConstants.CA.DECRYPT);
                        csr.setCreateTime(new Date());
                        csr.setContainerName(FileConfigUtil.CONTAINERNAME);
                        csr.setReceiver(receiver);
                        caServiceImpl.insertSelective(csr);

                        String decrypt = CAService.decrypt(appcode, apppwd, url, source, decryptPath, callBackUrl, biz_sn,FileConfigUtil.CONTAINERNAME,receiver);

                        if ("0".equals(decrypt)) {
                            //如果调用失败则立即更新状态
                            String operaState = CommonConstants.OPERATESTATE.ZXJIEMZ;// 操作状态
                            String sendState = RecvSendStateEnum.FAIL.getStateCode();// 流转状态
                            try {
                                for(String s1:toNodeId)
                                {
                                    //循环更新数据
                                    map2.put("NODE_ID", FileConfigUtil.CURNODEID);
                                    map2.put("OPERATE_STATE_DM", operaState);
                                    map2.put("SEND_STATE_DM", sendState);
                                    map2.put("PACKAGE_ID", packageId);
                                    map2.put("TO_NODE_ID", s1);
                                    upStatusService.updateCurState(map2);
                                }
                            } catch (Exception e) {
                                logger.error("",e);
                                throw e;
                            }
                        }

                    } catch (Exception e) {
                        logger.error("",e);
                        //调用不成功插入或者更新一条数据
                        CaStepRecorders caStepRecorders =  caServiceImpl.selectByPrimaryKey(biz_sn);
                        if (caStepRecorders == null) {
                            CaStepRecorders csr = new CaStepRecorders();
                            csr.setBizSn(biz_sn);
                            csr.setCallbackUrl(callBackUrl);
                            csr.setCallStatus(CommonConstants.CA.CALL_FAIL);
                            csr.setFileUrl(source);
                            csr.setEfsUrl(decryptPath);
                            csr.setPackageId(packageId);
                            csr.setMode(CommonConstants.CA.DECRYPT);
                            csr.setCreateTime(new Date());
                            csr.setContainerName(FileConfigUtil.CONTAINERNAME);
                            csr.setReceiver(receiver);
                            caServiceImpl.insertSelective(csr);
                        } else {
                            CaStepRecorders csr = new CaStepRecorders();
                            csr.setBizSn(biz_sn);
                            csr.setCallStatus(CommonConstants.CA.CALL_FAIL);
                            caServiceImpl.updateByPrimaryKeySelective(csr);
                        }

                        //如果接口调用不成功则更新状态
                        String operaState = CommonConstants.OPERATESTATE.ZXJIEMZ;// 操作状态
                        String sendState = RecvSendStateEnum.FAIL.getStateCode();// 流转状态
                        try {
                            for(String s1:toNodeId)
                            {
                                //循环更新数据
                                map2.put("NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
                                map2.put("OPERATE_STATE_DM", operaState);
                                map2.put("SEND_STATE_DM", sendState);
                                map2.put("PACKAGE_ID", packageId);
                                map2.put("TO_NODE_ID", s1);
                                upStatusService.updateCurState(map2);
                            }
                        } catch (Exception e1) {
                            logger.error("",e1);
                            throw e1;
                        }
                        logger.error(ExceptionState.DECRYPT.getCode() + ExceptionState.DECRYPT.getName() + packPath);
                    }
                    logger.info("包：{}调用完解密接口中心节点等待解密回调",packageId);
                } else {
                    //如果在中心不解压也要插入包大小等信息
                    upStatusService.insertPageAndPageSub(file);
                    logger.info("中心更新包的状态{}",packageId);
                    ConcurrentHashMap<String, Object> zip = new ConcurrentHashMap<String, Object>();
                    zip.put("FILE_PATH", packPath);
                    zip.put("TO_NODE_ID", toNode);
                    zip.put("NODE_ID", FileConfigUtil.CURNODEID);
                    zip.put("PACKAGE_ID", packageId);
                    param.add(zip);
                }

            }
            if(!param.isEmpty()){
                map.put("PARAM", param);
                if (!toNodeId.contains(FileConfigUtil.CURNODEID)) {
                    sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber, new BigDecimal(0));
                }
                SectionNode nextSectionNode = sectionNode.getNext();
                if (nextSectionNode != null && !toNodeId.contains(FileConfigUtil.CURNODEID)) {
                    nextSectionNode.getCurrent().doAct(packageId, jobId, nextSectionNode, totalSectionNumber, map);
                }
            }
            logger.info("ca解密后等待回调，不执行后边代码。");
            return;
        }

        //获取加密包的路径
        String packPath = String.valueOf(map.get("PACK_PATH"));
        File file = new File(packPath);
        String pagePath = file.getParent();
        //获取包名
        String packageId = file.getName();
        //根据包名截取去那里的appID
        String[] appIdTo = PackUtil.splitAppTo(packageId);
        //到新的节点找到去往目的地其对应的nodeId
        List<String> toNodeId = new ArrayList<String>();
        for (String app : appIdTo) {
            NodeAppBean nodeApp = nodeAppBeanMapper.selectByPrimaryKey(app);
            toNodeId.add(nodeApp.getNodeId());
        }
        //参数准备
        ConcurrentHashMap<String, Object> map1 = new ConcurrentHashMap<String, Object>();
        //在解密之前把解密中状态插入
        for (String app : toNodeId) {
            ConcurrentHashMap<String, Object> zip = new ConcurrentHashMap<String, Object>();
            map1.put("NODE_ID", FileConfigUtil.CURNODEID);
            map1.put("PACKAGE_ID", packageId);
            map1.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.ZXJIEMZ);
            map1.put("TO_NODE_ID", app);
            //插入数据扭转进程表和更新状态表
            //插入数据包当前状态表和数据包流水状态表
            upStatusService.updateCurState(map1);
        }
        //操作开始时间
        BigDecimal start = new BigDecimal(System.currentTimeMillis());
        //解密此路径加密文件
        String operaState = "";// 操作状态
        String sendState = "";// 流转状态
        try {
            createPackService.decryptZip(packPath);
            operaState = CommonConstants.OPERATESTATE.ZXJIEM; // 解密成功
            //删除加密文件并更改解密文件名称
            PackUtil.renameAndDeleteZip(packPath);
        } catch (DecryptException e) {
            sendState = RecvSendStateEnum.FAIL.getStateCode();
            logger.error("解密异常：" + packPath);
        }
        //操作结束时间
        BigDecimal end = new BigDecimal(System.currentTimeMillis());
        //计算耗时
        BigDecimal spendTime = end.subtract(start).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP);

        //参数准备
        List<ConcurrentHashMap<String, Object>> param = new ArrayList<ConcurrentHashMap<String, Object>>();
        //插入主包和子包表
        upStatusService.insertPageAndPageSub(file);
        //若是中心节点
        for (String app : toNodeId) {
            ConcurrentHashMap<String, Object> zip = new ConcurrentHashMap<String, Object>();
            map1.put("NODE_ID", FileConfigUtil.CURNODEID);
            map1.put("PACKAGE_ID", packageId);
            map1.put("OPERATE_STATE_DM", operaState);
            map1.put("SEND_STATE_DM", sendState);
            map1.put("TO_NODE_ID", app);
            map1.put("SPEND_TIME", spendTime);
            //插入数据扭转进程表和更新状态表
            //插入数据包当前状态表和数据包流水状态表
            upStatusService.updateCurState(map1);
            //构造下个section需要参数
            zip.put("FILE_PATH", file.getAbsolutePath());
            zip.put("TO_NODE_ID", app);
            zip.put("PACKAGE_ID", packageId);
            param.add(zip);
        }

        map.put("CUR_NODE_ID", FileConfigUtil.CURNODEID);
        map.put("PARAM", param);
        map.put("MARK", CommonConstants.STATE.SELF);
        map.put("FILE_PATH", file.getAbsolutePath());
        //验证有效性并返回结果
        // boolean valid = PackUtil.isValid(packPath);
        boolean valid = true;

        // 如果流转状态异常
        if (RecvSendStateEnum.FAIL.getStateCode().equals(sendState)) {
            valid = false;
            throw new BusinessErrorException(ExceptionState.DECRYPT.getCode(),ExceptionState.DECRYPT.getName()+ packPath);
        }

        if(valid){
            sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
            SectionNode nextSectionNode = sectionNode.getNext();
            if(nextSectionNode!= null){
                nextSectionNode.getCurrent().doAct(packageId,jobId,nextSectionNode,totalSectionNumber,map);
            }
        }
    }


    @Override
    public void update(String pkgId, PkgStatus status) {

    }
}
