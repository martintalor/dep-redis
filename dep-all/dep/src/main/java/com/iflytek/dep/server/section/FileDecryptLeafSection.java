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
import net.lingala.zip4j.core.ZipFile;
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

@Service
public class FileDecryptLeafSection implements Section, Status {

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


    private final static Logger logger = LoggerFactory.getLogger(FileDecryptLeafSection.class);


	@Override
	public void doAct(final String pkgId, String jobId, SectionNode sectionNode, BigDecimal totalSectionNumber, ConcurrentHashMap<String, Object> map) throws Exception {

        logger.info("解密开始----[{}] , pkgId id [{}] running",this.getClass(),pkgId);
        sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));

        if(FileConfigUtil.ISCA){

            //获取加密包的路径
            String packPath = String.valueOf(map.get("PACK_PATH"));
            //得到解密共享目录
            String decryptDir = FileConfigUtil.DECRYPTDIR;
            File file = new File(packPath);
            //String pagePath = file.getParent();
            //获取包名
            String packageId = file.getName();

            upStatusService.insertPageAndPageSub(file);
            logger.info("更新包的状态{}",packageId);
            //拷贝文件到共享目录
            String source = decryptDir+CommonConstants.NAME.FILESPLIT+"source"+CommonConstants.NAME.FILESPLIT+packageId;
            FileUtil.copyZipRetry(packPath,source);
            //共享文件夹下解密完的文件路径
            String decryptPath = decryptDir+CommonConstants.NAME.FILESPLIT+"target"+CommonConstants.NAME.FILESPLIT+packageId;
//                        //拼接解密后文件夹路径
//                        String parentPath = pagePath + CommonConstants.NAME.FILESPLIT + "unpack";
//                        //拼接解密后文件全路径
//                        String newPath = parentPath + CommonConstants.NAME.FILESPLIT + packageId;
            //根据包名截取去那里的appID
            String[] appIdTo = PackUtil.splitAppTo(packageId);
            //到新的节点找到去往目的地其对应的nodeId
            List<String> toNodeId = new ArrayList<String>();
            for (String app : appIdTo) {
                NodeAppBean nodeApp = nodeAppBeanMapper.selectByPrimaryKey(app);
                toNodeId.add(nodeApp.getNodeId());
            }
            //准备参数
            ConcurrentHashMap<String, Object> map1 = new ConcurrentHashMap<String, Object>();
            map1.put("NODE_ID", FileConfigUtil.CURNODEID);
            map1.put("PACKAGE_ID", packageId);
            map1.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.JIEMIZ);
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
                //向表中插入调用记录(不管返回结果先插入一条然后调用接口)
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
                    String operaState = CommonConstants.OPERATESTATE.JIEMIZ;// 操作状态
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
                    csr.setEfsUrl(decryptPath);
                    csr.setPackageId(packageId);
                    csr.setMode(CommonConstants.CA.DECRYPT);
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
                String operaState = CommonConstants.OPERATESTATE.JIEMIZ;// 操作状态
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
                    logger.error("",e);
                    throw e;
                }
                logger.error(ExceptionState.DECRYPT.getCode() + ExceptionState.DECRYPT.getName() + packPath);
            }
            logger.info("包：{}解密调用完成，等待解密回调",packageId);
            return;
        }
        //获取加密包的路径
        String packPath = String.valueOf(map.get("PACK_PATH"));
        File file = new File(packPath);
        String pagePath = file.getParent();
        //获取包名
        String packageId = file.getName();
        //拼接解密后文件夹路径
        String parentPath = pagePath + CommonConstants.NAME.FILESPLIT + "unpack";
        //拼接解密后文件全路径
        String newPath = parentPath + CommonConstants.NAME.FILESPLIT + packageId;
        //根据包名截取去那里的appID
        String[] appIdTo = PackUtil.splitAppTo(packageId);
        //到新的节点找到去往目的地其对应的nodeId
        List<String> toNodeId = new ArrayList<String>();
        for (String app : appIdTo) {
            NodeAppBean nodeApp = nodeAppBeanMapper.selectByPrimaryKey(app);
            toNodeId.add(nodeApp.getNodeId());
        }
        //准备参数
        ConcurrentHashMap<String, Object> map1 = new ConcurrentHashMap<String, Object>();
        map1.put("NODE_ID", FileConfigUtil.CURNODEID);
        map1.put("PACKAGE_ID", packageId);
        map1.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.JIEMIZ);
        map1.put("TO_NODE_ID", FileConfigUtil.CURNODEID);
        //插入数据扭转进程表和更新状态表
        //插入数据包当前状态表和数据包流水状态表
        upStatusService.updateCurState(map1);
        //操作开始时间
        BigDecimal start = new BigDecimal(System.currentTimeMillis());
        //解密此路径加密文件
        String operaState = "";// 操作状态
        String sendState = "";// 流转状态
        try {
            //创建存放解密文件的文件夹
            PackUtil.makeDir(parentPath);
            //解密
            createPackService.decryptLeafZip(packPath);
            operaState = CommonConstants.OPERATESTATE.JIEMI; // 解密成功
            //删除加密文件并更改解密文件名称
            PackUtil.renameAndDeleteLeafZip(packPath);
        } catch (DecryptException e) {
            sendState = RecvSendStateEnum.FAIL.getStateCode();
            logger.error("解密异常：" + "解密前文件：" + packPath + "解密后文件位置：" + newPath);
        }

        //操作结束时间
        BigDecimal end = new BigDecimal(System.currentTimeMillis());
        //计算耗时
        BigDecimal spendTime = end.subtract(start).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP);

        //参数准备
        List<ConcurrentHashMap<String, Object>> param = new ArrayList<ConcurrentHashMap<String, Object>>();

        //插入主包和子包表
        File newzip = new File(newPath);
        upStatusService.insertPageAndPageSub(newzip);

        //非中心节点时
        ConcurrentHashMap<String, Object> zip = new ConcurrentHashMap<String, Object>();
        map1.put("NODE_ID", FileConfigUtil.CURNODEID);
        map1.put("PACKAGE_ID", packageId);
        map1.put("OPERATE_STATE_DM", operaState);
        map1.put("SEND_STATE_DM", sendState);
        map1.put("TO_NODE_ID", FileConfigUtil.CURNODEID);
        map1.put("SPEND_TIME",spendTime);
        //插入数据扭转进程表和更新状态表
        //插入数据包当前状态表和数据包流水状态表
        upStatusService.updateCurState(map1);

        // 如果流转状态异常
        if (RecvSendStateEnum.FAIL.getStateCode().equals(sendState)) {
            throw new BusinessErrorException(ExceptionState.DECRYPT.getCode(), ExceptionState.DECRYPT.getName() + packPath);
        }

        //构造下个section需要参数
        zip.put("FILE_PATH", newzip.getAbsolutePath());
        zip.put("PACKAGE_ID", packageId);
        param.add(zip);

        map.put("CUR_NODE_ID", FileConfigUtil.CURNODEID);
        map.put("PARAM", param);
        map.put("MARK", CommonConstants.STATE.SELF);
        //解密成功 记录数据库
        sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
        //判断是否能走下个步骤
        boolean valid = PackUtil.isValid(newPath.split(CommonConstants.NAME.PACKAGE_FIX)[0] + CommonConstants.NAME.ZIP);

        //包不全也校验
        if (valid) {
            logger.info("解密成功 [{}] , pkgId id [{}] run end ",this.getClass(),pkgId);
            ZipFile zipFile = new ZipFile(newPath.split(CommonConstants.NAME.PACKAGE_FIX)[0] + CommonConstants.NAME.ZIP);
            //第一时间设置编码格式
            zipFile.setFileNameCharset("UTF-8");

            logger.info("校验数据包完整性 [{}] , pkgId id [{}] run running ",this.getClass(),pkgId);
            valid = (zipFile.getSplitZipFiles().size() == new File(parentPath).listFiles().length);
            logger.info("校验数据包完整性valid，[{}] , class:[{}] , pkgId id [{}] run end ",valid,this.getClass(),pkgId);
        }

        SectionNode nextSectionNode = sectionNode.getNext();
        if(nextSectionNode!= null && valid){
            nextSectionNode.getCurrent().doAct(packageId,jobId,nextSectionNode,totalSectionNumber,map);
        }

        logger.info("解密结束 [{}] , pkgId id [{}] run end ",this.getClass(),pkgId);

	}


    @Override
    public void update(String pkgId, PkgStatus status) {
        // update pkg status here

    }


}
