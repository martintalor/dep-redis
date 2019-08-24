package com.iflytek.dep.server.section;


import com.google.gson.Gson;
import com.iflytek.dep.common.exception.BusinessErrorException;
import com.iflytek.dep.common.security.EncryptException;
import com.iflytek.dep.server.ca.CaServiceImpl;
import com.iflytek.dep.server.constants.ExceptionState;
import com.iflytek.dep.server.constants.PkgStatus;
import com.iflytek.dep.server.constants.RecvSendStateEnum;
import com.iflytek.dep.server.file.CAService;
import com.iflytek.dep.server.mapper.DataNodeProcessBeanMapper;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.model.CaStepRecorders;
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
public class FileEncryptLeafSection implements Section, Status {
    private static Logger logger = LoggerFactory.getLogger(FileEncryptLeafSection.class);
    private static ExecutorService fixedUpStreamThreadPool;
    public static AtomicInteger threadJobSize = new AtomicInteger(0);

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
    @Autowired
    CaServiceImpl caServiceImpl;


    @Override
    public void doAct(final String pkgId, String jobId, SectionNode sectionNode, BigDecimal totalSectionNumber, ConcurrentHashMap<String, Object> map) throws Exception {
        logger.info("[{}] job has [{}],package id [{}] in method ,jobId is [{}]",this.getClass(),threadJobSize.incrementAndGet(),pkgId,jobId);

        sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));

        //得到参数
        List<ConcurrentHashMap<String, Object>> param = (List<ConcurrentHashMap<String, Object>>) map.get("PARAM");
        List<String> toNodeId = (List<String>) map.get("TO_NODE_ID");
        //先得到后期需要存放的list
        List<ConcurrentHashMap<String, Object>> param1 = new ArrayList<ConcurrentHashMap<String, Object>>();
        //得到加密方式
        String mark = null;
        Object state = map.get("MARK");
        if (state == null) {
            mark = CommonConstants.STATE.SELF;
        } else {
            mark = String.valueOf(state);
        }
        final String MARK = mark;

        //循环param里的路径有来处理加密
        for (Object item : param) {
            Gson gson = new Gson();
            ConcurrentHashMap<String, Object> s = gson.fromJson(gson.toJson(item), ConcurrentHashMap.class);

            //参数准备
            String filePath = String.valueOf(s.get("FILE_PATH"));
            String packageId = String.valueOf(s.get("PACKAGE_ID"));
            //往当前状态和操作状态表里插入数据
            ConcurrentHashMap<String, Object> map2 = new ConcurrentHashMap<String, Object>();
            for(String s1:toNodeId)
            {
                //循环更新数据
                map2.put("NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
                map2.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.JIAMZ);
                map2.put("PACKAGE_ID", packageId);
                map2.put("TO_NODE_ID", s1);
                upStatusService.updateCurState(map2);
            }

            File file = new File(filePath);
            String parentDir = file.getParent();
            //如果是子节点
            //判断用原本系统加密方式，还是用ck预留扩展的加密方式
            if (!FileConfigUtil.ISCA) {
                String publickey = FileConfigUtil.PUBLICKEY;
                //创建存放加密文件的文件夹
                PackUtil.makeDir(parentDir + CommonConstants.NAME.FILESPLIT + "ZSE01");
                //操作开始时间
                BigDecimal start = new BigDecimal(System.currentTimeMillis());
                String operaState = "";// 操作状态
                String sendState = "";// 流转状态
                //加密
                try {
                    createPackService.encryptZip(filePath, publickey, "ZSE01");
                    operaState = CommonConstants.OPERATESTATE.JIAM;
                    //删除原没加密压缩包
                    PackUtil.deleteZip(filePath);
                } catch (EncryptException e) {
                    sendState = RecvSendStateEnum.FAIL.getStateCode();
                    //将异常带出去处理
                    map.put("flag", false);
                    logger.error(ExceptionState.ENCRYPT.getCode() + ExceptionState.ENCRYPT.getName() + filePath);
                }
                try {
                    //操作结束时间
                    BigDecimal end = new BigDecimal(System.currentTimeMillis());
                    //计算耗时
                    BigDecimal spendTime = end.subtract(start).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP);
                    String finalOperaState = operaState;
                    String finalSendState = sendState;
                    for(String s1:toNodeId)
                    {
                        //循环更新数据
                        map2.put("NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
                        map2.put("OPERATE_STATE_DM", finalOperaState);
                        map2.put("SEND_STATE_DM", finalSendState);
                        map2.put("PACKAGE_ID", packageId);
                        map2.put("TO_NODE_ID", s1);
                        map2.put("SPEND_TIME",spendTime);
                        upStatusService.updateCurState(map2);
                    }

                    s.put("FILE_PATH", parentDir + CommonConstants.NAME.FILESPLIT + "ZSE01" + CommonConstants.NAME.FILESPLIT + packageId);
                    s.put("TO_NODE_ID", toNodeId.get(0));
                    s.put("NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
                    //构造传给下个节点的参数
                    param1.add(s);
                } catch (Exception e) {
                    logger.error(" 加密失败 [{}] ",this.getClass(), e);
                }

            }
            if (FileConfigUtil.ISCA) {
                String biz_sn=null;
                String callBackUrl=null;
                String efsUrl=null;
                String encryptDir = FileConfigUtil.ENCRYPTDIR;
                String receiver = null;
                //共享文件夹下的加密后文件目录
                String source = encryptDir+CommonConstants.NAME.FILESPLIT+"source"+CommonConstants.NAME.FILESPLIT+packageId;
                try {
                    //加密
                    FileUtil.copyZipRetry(filePath,source);
                    //创建存放加密文件的文件夹
                    //PackUtil.makeDir(parentDir + CommonConstants.NAME.FILESPLIT + "ZSE01");
                    //加密后文件存放地址(共享目录)
                    efsUrl = encryptDir+CommonConstants.NAME.FILESPLIT+"target"+CommonConstants.NAME.FILESPLIT+packageId;
                    String appcode = FileConfigUtil.APPCODE;
                    String apppwd = FileConfigUtil.APPPWD;
                    //ca接口地址
                    String url = FileConfigUtil.CAURL;
                    //ca回调接口地址
                    callBackUrl = FileConfigUtil.CACALLBACKURL;
                    //生成biz_sn
                    biz_sn = UUID.randomUUID().toString();
                    //生成receiver
                    receiver = FileUtil.parsingReceiver(packageId);

                    //向表中插入调用记录(不管返回结果先插入一条然后再调接口)
                    CaStepRecorders csr = new CaStepRecorders();
                    csr.setBizSn(biz_sn);
                    csr.setCallbackUrl(callBackUrl);
                    csr.setCallStatus(CommonConstants.CA.CALL_SUCCESS);
                    csr.setFileUrl(source);
                    csr.setEfsUrl(efsUrl);
                    csr.setPackageId(packageId);
                    csr.setMode(CommonConstants.CA.ENCRYPT);
                    csr.setCreateTime(new Date());
                    csr.setContainerName(FileConfigUtil.CONTAINERNAME);
                    csr.setReceiver(receiver);
                    caServiceImpl.insertSelective(csr);

                    String entrypt = CAService.entrypt(appcode, apppwd, url, source, efsUrl, callBackUrl, biz_sn,FileConfigUtil.CONTAINERNAME,receiver);

                    if ("0".equals(entrypt)) {
                        //如果调用失败则立即更新状态
                        String operaState = CommonConstants.OPERATESTATE.JIAMZ;// 操作状态
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
                        } catch (Exception e) {
                            logger.error("",e);
                            throw e;
                        }
                    }

                } catch (Exception e) {
                    logger.error("",e);
                    //调用不成功插入或者更新一条数据
                    CaStepRecorders caStepRecorders = caServiceImpl.selectByPrimaryKey(biz_sn);
                    if(caStepRecorders == null){
                        CaStepRecorders csr = new CaStepRecorders();
                        csr.setBizSn(biz_sn);
                        csr.setCallbackUrl(callBackUrl);
                        csr.setCallStatus(CommonConstants.CA.CALL_FAIL);
                        csr.setFileUrl(source);
                        csr.setEfsUrl(efsUrl);
                        csr.setPackageId(packageId);
                        csr.setMode(CommonConstants.CA.ENCRYPT);
                        csr.setCreateTime(new Date());
                        csr.setContainerName(FileConfigUtil.CONTAINERNAME);
                        csr.setReceiver(receiver);
                        caServiceImpl.insertSelective(csr);
                    }else{
                        CaStepRecorders csr = new CaStepRecorders();
                        csr.setBizSn(biz_sn);
                        csr.setCallStatus(CommonConstants.CA.CALL_FAIL);
                        caServiceImpl.updateByPrimaryKeySelective(csr);
                    }
                    //如果接口调用不成功则更新状态
                    String operaState = CommonConstants.OPERATESTATE.JIAMZ;// 操作状态
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
                    //将异常带出去处理
                    map.put("flag", false);
                    logger.error(ExceptionState.ENCRYPT.getCode() + ExceptionState.ENCRYPT.getName() + filePath);
                }finally {
                    logger.info("调用完加密接口叶子节点等待加密回调");

                }
            }

        }

        if(map.get("flag")!=null && !(boolean)map.get("flag"))
        {
            throw new BusinessErrorException(ExceptionState.ENCRYPT.getCode(), ExceptionState.ENCRYPT.getName() + pkgId);
        }
        if (FileConfigUtil.ISCA) {
            logger.info("叶子节点加密成功，等待ca回调");
            return;
        }

        map.put("PARAM", param1);

        if(map.get("flag") == null || true == (boolean) map.get("flag")){
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
