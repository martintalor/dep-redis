package com.iflytek.dep.server.section;


import com.google.gson.Gson;
import com.iflytek.dep.common.security.EncryptException;
import com.iflytek.dep.server.config.web.ApplicationContextRegister;
import com.iflytek.dep.server.constants.PkgStatus;
import com.iflytek.dep.server.constants.RecvSendStateEnum;
import com.iflytek.dep.server.mapper.DataNodeProcessBeanMapper;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.service.dataPack.CreatePackService;
import com.iflytek.dep.server.service.dataPack.UpStatusService;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.FileConfigUtil;
import com.iflytek.dep.server.utils.PackUtil;
import com.iflytek.dep.server.utils.SectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FileEncryptSection   implements Section, Status {
    private static Logger logger = LoggerFactory.getLogger(FileEncryptSection.class);
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
    //获取运行环境实体，然后获取实体数据
    private static Environment environment = ApplicationContextRegister.getApplicationContext().getBean(Environment.class);

    @Override
    public void doAct(final String pkgId, String jobId, SectionNode sectionNode, BigDecimal totalSectionNumber, ConcurrentHashMap<String, Object> map) throws Exception {
        logger.info("[{}] job has [{}],package id [{}] in method ,jobId is [{}]",this.getClass(),Thread.currentThread().getName(),pkgId,jobId);

        try {
            logger.info("[{}] , package id [{}] running",this.getClass(),pkgId);
            sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));

            //得到参数
            List<ConcurrentHashMap<String, Object>> param = (List<ConcurrentHashMap<String, Object>>) map.get("PARAM");
            List<String> toNodeId = (List<String>) map.get("TO_NODE_ID");
            //先得到后期需要存放的list
            List<ConcurrentHashMap<String, Object>> param1=new ArrayList<ConcurrentHashMap<String, Object>>();
            //得到加密方式
            String mark = null;
            Object state = map.get("MARK");
            if (state == null) {
                mark = CommonConstants.STATE.SELF;
            } else {
                mark = String.valueOf(state);
            }
            final String MARK = mark;
            //循环param里的路径有一个就开启一个新的线程来处理加密这个事件
            for (Object item:   param) {
                Gson gson =  new Gson();
                ConcurrentHashMap<String, Object> s = gson.fromJson(gson.toJson(item),ConcurrentHashMap.class);
                //参数准备
                String filePath = String.valueOf(s.get("FILE_PATH"));
                String packageId = String.valueOf(s.get("PACKAGE_ID"));
                File file = new File(filePath);
                String parentDir = file.getParent();
                if (!FileConfigUtil.ISCENTER) {
                    //如果不是中心节点
                    //判断用原本系统加密方式，还是用ck预留扩展的加密方式
                    if (CommonConstants.STATE.SELF.equals(MARK)) {
                        String publickey = FileConfigUtil.PUBLICKEY;
                        //创建存放加密文件的文件夹
                        PackUtil.makeDir(parentDir + CommonConstants.NAME.FILESPLIT + "ZSE01");
                        //往当前状态和操作状态表里插入数据
                        ConcurrentHashMap<String, Object> map2 = new ConcurrentHashMap<String, Object>();
                        //操作开始时间
                        Date createTime = new Date();
                        //加密
                        try {
                            createPackService.encryptZip(filePath, publickey, "ZSE01");
                        } catch (EncryptException e) {
                            s.put("SEND_STATE_DM", RecvSendStateEnum.FAIL.getStateCode());
                            e.printStackTrace();
                        }
                        //操作结束时间
                        Date updateTime = new Date();
                        for(String s1:toNodeId)
                        {
                            //循环更新数据
                            map2.put("NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
                            map2.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.JIAM);
                            map2.put("PACKAGE_ID", packageId);
                            map2.put("TO_NODE_ID",s1);
                            map2.put("CREATE_TIME",createTime);
                            map2.put("UPDATE_TIME",updateTime);
                            upStatusService.updateCurState(map2);
                        }

                        s.put("FILE_PATH", parentDir + CommonConstants.NAME.FILESPLIT + "ZSE01" + CommonConstants.NAME.FILESPLIT + packageId);
                        s.put("TO_NODE_ID", toNodeId.get(0));
                        s.put("NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
                        //构造传给下个节点的参数
                        param1.add(s);
                        //删除原没加密压缩包
                        PackUtil.deleteZip(filePath);

                    }
                    if (CommonConstants.STATE.CA.equals(MARK)) {

                    }
                } else {
                    //如果是中心节点
                    //判断用原本系统加密方式，还是用ck预留扩展的加密方式
                    String publickey = null;
                    if (CommonConstants.STATE.SELF.equals(MARK)) {
                        String[] appTo = PackUtil.splitAppTo(packageId);
                        String nodeID = null;
                        for (String app : appTo) {
                            //根据不同appid查询到其对应的nodeid
                            NodeAppBean nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(app);
                            nodeID = nodeAppBean.getNodeId();
                            //根据不同appTo选择不同加密公钥并且存放入不同子文件夹
                            publickey=environment.getProperty(nodeID+CommonConstants.NAME.KEY);
//                            if ("APP_G1".equals(app)) {
//                                publickey = FileConfigUtil.POLPUBLICKEY;
//                                NodeAppBean nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(app);
//                                nodeID = nodeAppBean.getNodeId();
//                            }
//                            if ("APP_J1".equals(app)) {
//                                publickey = FileConfigUtil.PROPUBLICKEY;
//                                NodeAppBean nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(app);
//                                nodeID = nodeAppBean.getNodeId();
//                            }
//                            if ("APP_F1".equals(app)) {
//                                publickey = FileConfigUtil.COURTPUBLICKEY;
//                                NodeAppBean nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(app);
//                                nodeID = nodeAppBean.getNodeId();
//                            }
                            //构造更新参数
                            ConcurrentHashMap<String, Object> map2 = new ConcurrentHashMap<String, Object>();
                            //创建存放加密文件的文件夹
                            PackUtil.makeDir(parentDir + CommonConstants.NAME.FILESPLIT + nodeID);
                            //操作开始时间
                            Date createTime = new Date();
                            //加密
                            try {
                                createPackService.encryptZip(filePath, publickey, nodeID);
                            } catch (EncryptException e) {
                                s.put("SEND_STATE_DM", RecvSendStateEnum.FAIL.getStateCode());
                                e.printStackTrace();
                            }
                            //操作结束时间
                            Date updateTime = new Date();
                            map2.put("NODE_ID", String.valueOf(map.get("CUR_LINK_ID")));
                            map2.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.ZXJIAM);
                            map2.put("PACKAGE_ID", packageId);
                            map2.put("TO_NODE_ID",nodeID);
                            map2.put("CREATE_TIME",createTime);
                            map2.put("UPDATE_TIME",updateTime);
                            upStatusService.updateCurState(map2);
                            s.put("FILE_PATH", parentDir + CommonConstants.NAME.FILESPLIT + nodeID + CommonConstants.NAME.FILESPLIT + packageId);
                            s.put("TO_NODE_ID", nodeID);
                            s.put("NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
                            //构造传给下个节点的参数
                            param1.add(s);
                        }
                        //删除原没加密压缩包
                        PackUtil.deleteZip(filePath);
                    }
                    if (CommonConstants.STATE.CA.equals(MARK)) {
                        //ca预留的地方
                    }
                }
            }

            map.put("PARAM", param1);
//                    return SectionResult(true, map);
            sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
            SectionNode nextSectionNode = sectionNode.getNext();
            if(nextSectionNode!= null){
                nextSectionNode.getCurrent().doAct(pkgId,jobId,nextSectionNode,totalSectionNumber,map);
            }
        } catch (Exception e) {
            logger.error("[{}] happend error",this.getClass(), e);
        } finally {
            logger.info("[{}] job has [{}]",this.getClass(),threadJobSize.decrementAndGet());
            logger.info("[{}] , package id [{}] run end ",this.getClass(),pkgId);
        }
    }

    @Override
    public void update(String pkgId, PkgStatus status) {
        // update pkg status here

    }


}
