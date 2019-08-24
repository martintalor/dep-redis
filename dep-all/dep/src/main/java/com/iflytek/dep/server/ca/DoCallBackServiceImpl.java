package com.iflytek.dep.server.ca;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.iflytek.dep.common.exception.BusinessErrorException;
import com.iflytek.dep.common.utils.RandomGUID;
import com.iflytek.dep.server.constants.ExchangeNodeType;
import com.iflytek.dep.server.constants.RecvSendStateEnum;
import com.iflytek.dep.server.down.PkgGetterManger;
import com.iflytek.dep.server.mapper.*;
import com.iflytek.dep.server.model.CaStepRecorders;
import com.iflytek.dep.server.model.DataPackBean;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.model.SectionStepRecorders;
import com.iflytek.dep.server.section.*;
import com.iflytek.dep.server.service.dataPack.GetPackService;
import com.iflytek.dep.server.service.dataPack.UpStatusService;
import com.iflytek.dep.server.up.PkgUploaderManager;
import com.iflytek.dep.server.utils.*;
import net.lingala.zip4j.core.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class DoCallBackServiceImpl {
    private static Logger logger = LoggerFactory.getLogger(DoCallBackServiceImpl.class);

    @Autowired
    UpStatusService upStatusService;
    @Autowired
    DataNodeProcessBeanMapper dataNodeProcessBeanMapper;
    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;
    @Autowired
    SectionUtils sectionUtils;
    @Autowired
    CaStepRecordersMapper caStepRecordersMapper;
    @Autowired
    SectionStepRecordersMapper sectionStepRecordersMapper;
    @Autowired
    DataPackBeanMapper dataPackBeanMapper;

    @Autowired
    MachineNodeMapper machineNodeMapper;

    @Autowired
    GetPackService getPackService;

    @Autowired
    private Environment environment;

    @Autowired
    PkgUploaderManager pkgUploaderManager;

    @Autowired
    PkgGetterManger pkgGetterManger;

    @Autowired
    private RedisUtil redisUtil;

    @Transactional(propagation = Propagation.REQUIRED)
    public void doCallBackTask(String biz_sn) {
        CaStepRecorders caStepRecorders = caStepRecordersMapper.selectByPrimaryKey(biz_sn);
        String backStatus = caStepRecorders.getBackStatus();
        BigDecimal executeStatus = caStepRecorders.getExecuteStatus();
        String packageIdHasSuffix = caStepRecorders.getPackageId();

        if (executeStatus != null && executeStatus.intValue() == 1) {
            return;
        }

        try{

            /**
             boolean find = false;
             List<CaStepRecorders> caStepRecordersList = caStepRecordersMapper.selectByPackageId(packageIdHasSuffix);
             for(CaStepRecorders item:caStepRecordersList)
             {
             if(item.getPackageId().equals(packageIdHasSuffix) && item.getExecuteStatus().intValue()==1)
             {
             find = true;
             break;
             }
             }

             if (find) {
             return;
             }
             **/

            logger.info("线程:={} 执行doCallBackTask方法,bis_sn:={},packageId:={},backStatus:={},executeStatus:={}", Thread.currentThread().getName(),biz_sn,packageIdHasSuffix,backStatus,executeStatus);

            String efsUrl = caStepRecorders.getEfsUrl();
            Date startTime = caStepRecorders.getCreateTime();
            String mode = caStepRecorders.getMode();
            String sectionParam = null;
            BigDecimal totalSectionNumber = null;
            //String jobId =null;
            boolean isCenter = Boolean.valueOf(environment.getProperty("is.center"));

            Map<String, String> resultMap = null;
            if (mode.equals(CommonConstants.CA.ENCRYPT)) {
                String packageId = packageIdHasSuffix.split("\\.")[0];
                resultMap = getSectionParam(packageId, isCenter, mode);
            } else {
                //String packageId = packageId1;
                resultMap = getSectionParam(packageIdHasSuffix, isCenter, mode);
            }

            String serverNodeId = environment.getProperty("server.node.id");

            sectionParam = resultMap.get("sectionParam");
            totalSectionNumber = BigDecimal.valueOf(Long.valueOf(resultMap.get("totalSectionNumber")));

            String sectionName =   resultMap.get("sectionName");

            if (mode.equals(CommonConstants.CA.ENCRYPT)) {
                if (!isCenter) {
                    doLeafEncrypt(packageIdHasSuffix, sectionName,mode, isCenter, sectionParam,
                            totalSectionNumber, startTime, biz_sn, efsUrl);
                } else {
                    doMainEncrypt(packageIdHasSuffix, sectionName,mode, isCenter, sectionParam,
                            totalSectionNumber, startTime, biz_sn, efsUrl);
                }
            }
            if (mode.equals(CommonConstants.CA.DECRYPT)) {
                doDecrypt(packageIdHasSuffix, sectionName,mode, isCenter, sectionParam,
                        totalSectionNumber, startTime, biz_sn, efsUrl);
            }

        }
        catch (Exception e)
        {
            logger.error("doCallBackTask发生错误",e);
            throw new RuntimeException("doCallBackTask发生错误");
        }
    }

    private Map<String, String> getSectionParam(String packageId, boolean isCenter, String mode) {
        String sectionParam = null;
        BigDecimal totalSectionNumber = null;
        String jobId = null;
        String sectionName=null;

        List<SectionStepRecorders> sectionRecorderList = sectionStepRecordersMapper.getByPackageId(packageId);
        logger.debug("getSectionParam 执行,packageId:={}",packageId);

        for (SectionStepRecorders sectionStepRecorders : sectionRecorderList) {
            if (isCenter && mode.equals(CommonConstants.CA.ENCRYPT)) {
                if (sectionStepRecorders.getSectionName().equals("class " + FileEncryptMainSection.class.getName())  && sectionStepRecorders.getDoactResult().intValue() == 0) {
                    sectionParam = sectionStepRecorders.getSectionParam();
                    totalSectionNumber = sectionStepRecorders.getTotalSectionNumber();
                    jobId = sectionStepRecorders.getPackageId();
                    sectionName =  "class " + FileEncryptMainSection.class.getName();
                    break;
                }
                if (sectionStepRecorders.getSectionName().equals("class " + FileEncryptOnlyMainSection.class.getName())  && sectionStepRecorders.getDoactResult().intValue() == 0) {
                    sectionParam = sectionStepRecorders.getSectionParam();
                    totalSectionNumber = sectionStepRecorders.getTotalSectionNumber();
                    jobId = sectionStepRecorders.getPackageId();
                    sectionName = "class " +  FileEncryptOnlyMainSection.class.getName();
                    break;
                }
            }
            if (!isCenter && mode.equals(CommonConstants.CA.ENCRYPT)) {
                if (sectionStepRecorders.getSectionName().equals("class " + FileEncryptLeafSection.class.getName()) && sectionStepRecorders.getDoactResult().intValue() == 0) {
                    sectionParam = sectionStepRecorders.getSectionParam();
                    totalSectionNumber = sectionStepRecorders.getTotalSectionNumber();
                    jobId = sectionStepRecorders.getPackageId();
                    sectionName =  "class " + FileEncryptLeafSection.class.getName();
                    break;
                }
            }
            if (isCenter && mode.equals(CommonConstants.CA.DECRYPT)) {
                if (sectionStepRecorders.getSectionName().equals("class " + FileDecryptMainSection.class.getName()) && sectionStepRecorders.getDoactResult().intValue() == 0) {
                    sectionParam = sectionStepRecorders.getSectionParam();
                    totalSectionNumber = sectionStepRecorders.getTotalSectionNumber();
                    jobId = sectionStepRecorders.getPackageId();
                    sectionName =  "class " +  FileDecryptMainSection.class.getName();
                    break;
                }
            }
            if (!isCenter && mode.equals(CommonConstants.CA.DECRYPT)) {
                if (sectionStepRecorders.getSectionName().equals("class " + FileDecryptLeafSection.class.getName()) && sectionStepRecorders.getDoactResult().intValue() == 0) {
                    sectionParam = sectionStepRecorders.getSectionParam();
                    totalSectionNumber = sectionStepRecorders.getTotalSectionNumber();
                    jobId = sectionStepRecorders.getPackageId();
                    sectionName = "class " +  FileDecryptLeafSection.class.getName();
                    break;
                }
            }
        }

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("jobId", jobId);
        resultMap.put("sectionParam", sectionParam);
        resultMap.put("totalSectionNumber", totalSectionNumber.intValue() + "");
        resultMap.put("sectionName", sectionName);
        return resultMap;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void doLeafEncrypt(String packageIdHasSuffix, String sectionName,String mode, boolean isCenter, String sectionParam,
                              BigDecimal totalSectionNumber, Date startTime, String biz_sn, String efsUrl) throws BusinessErrorException {

        String packageId = packageIdHasSuffix.split("\\.")[0];

        Gson gson = new Gson();
        ConcurrentHashMap<String, Object> s = gson.fromJson(sectionParam, ConcurrentHashMap.class);
        List<ConcurrentHashMap<String, Object>> param = (List<ConcurrentHashMap<String, Object>>) s.get("PARAM");
        Map<String, Object> map1 = null;
        for (Map<String, Object> item : param) {
            String PACKAGE_ID = String.valueOf(item.get("PACKAGE_ID"));
            if (PACKAGE_ID.equals(packageIdHasSuffix)) {
                map1 = item;
                break;
            }
        }

        String filePath = String.valueOf(map1.get("FILE_PATH"));
        File file = new File(filePath);
        String parentDir = file.getParent();
        //创建存放加密文件的文件夹
        String moveToPath = parentDir + CommonConstants.NAME.FILESPLIT + "ZSE01";
        PackUtil.makeDir(moveToPath);
        String moveTofile = moveToPath + CommonConstants.NAME.FILESPLIT + file.getName();

        //String sectionName = "class " + FileEncryptLeafSection.class.getName();
        try {
            String operaState = CommonConstants.OPERATESTATE.JIAM;
            String sendState = "";
            FileUtil.copyZipRetry(efsUrl, moveTofile);
            saveCaStepRecorders(biz_sn, CommonConstants.CA.BACK_SUCCESS);

            Set<String> countSet = new HashSet<>();
            for(Map<String, Object> item:param)
            {
                countSet.add((String)item.get("PACKAGE_ID"));
            }

            int fileNum = new File(moveToPath).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if(new File(dir + File.separator + name).isDirectory()){
                        return false;
                    }
                    return true;
                }
            }).length;

            boolean valid = (countSet.size() == fileNum);
            if (valid) {
                ConcurrentHashMap map = saveEncryptLeafSuccessState(packageIdHasSuffix, sectionParam, operaState, sendState, totalSectionNumber, startTime);
                insertSectionStepRecorders(map, new BigDecimal(1), sectionName, packageId, totalSectionNumber, new BigDecimal(0));
                String ftpNodeId = (String) map.get("DOWN_FTP_NODE_ID");
                saveToRedis(packageId, moveTofile, mode, isCenter, ftpNodeId);
                batchUpdateCaStep(packageId,null,null,new BigDecimal(1));
                new File(efsUrl).delete();
            }
        } catch (Exception e) {
            logger.error("", e);
            String operaState = "";
            String sendState = RecvSendStateEnum.FAIL.getStateCode();
            //saveErrorState(packageIdHasSuffix, sectionParam, operaState, sendState, startTime);
            throw new RuntimeException("doLeafEncrypt失败 " + packageIdHasSuffix);

        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void doDecrypt(String packageIdHasSuffix, String sectionName,String mode, boolean isCenter, String sectionParam,
                          BigDecimal totalSectionNumber, Date startTime, String biz_sn, String efsUrl) throws BusinessErrorException {
        logger.info("执行doDecrypt，package：{}", packageIdHasSuffix);
        Gson gson = new Gson();
        ConcurrentHashMap<String, Object> param = gson.fromJson(sectionParam, ConcurrentHashMap.class);
        String packPath = String.valueOf(param.get("PACK_PATH"));
        File file = new File(packPath);
        //获取包名
        String packageId1 = file.getName();
        String parentDir = file.getParent();

        String parentPath = parentDir + CommonConstants.NAME.FILESPLIT + "unpack";
        //拼接解密后文件全路径
        String moveToPath = parentPath + CommonConstants.NAME.FILESPLIT + packageId1;
        PackUtil.makeDir(parentPath);
        String moveTofile = moveToPath;

        try {
            if (isCenter) {
                String operaState = CommonConstants.OPERATESTATE.ZXJIEM;
                String sendState = "";
                FileUtil.copyZipRetry(efsUrl, moveTofile);
                saveCaStepRecorders(biz_sn, CommonConstants.CA.BACK_SUCCESS);

                ConcurrentHashMap map = saveDecryptMainSuccessState(sectionParam, operaState, "", totalSectionNumber, startTime);

                String ftpNodeId = (String) map.get("DOWN_FTP_NODE_ID");
                //防止并发回调n次，先验证包的完整性
                boolean valid = PackUtil.verifyZip(moveTofile.split(CommonConstants.NAME.PACKAGE_FIX)[0] + CommonConstants.NAME.ZIP);

                if (valid) {
                    insertSectionStepRecorders(map, new BigDecimal(1), sectionName, packageIdHasSuffix, totalSectionNumber, new BigDecimal(0));
                    batchUpdateCaStep(packageIdHasSuffix,null,null,new BigDecimal(1));
                    saveToRedis(packageIdHasSuffix, moveTofile.split(CommonConstants.NAME.PACKAGE_FIX)[0] + CommonConstants.NAME.ZIP, mode, isCenter,ftpNodeId);
                    new File(efsUrl).delete();
                }
            } else {
                String operaState = CommonConstants.OPERATESTATE.JIEMI;
                String sendState = "";
                FileUtil.copyZipRetry(efsUrl, moveTofile);
                ConcurrentHashMap map = saveDecryptLeafSuccessState(sectionParam, operaState, "", totalSectionNumber, startTime);
                saveCaStepRecorders(biz_sn, CommonConstants.CA.BACK_SUCCESS);

                String ftpNodeId = (String) map.get("DOWN_FTP_NODE_ID");
                //防止并发回调n次，先验证包的完整性
                boolean valid = PackUtil.verifyZip(moveTofile.split(CommonConstants.NAME.PACKAGE_FIX)[0] + CommonConstants.NAME.ZIP);
                if (valid) {
                    batchUpdateCaStep(packageIdHasSuffix,null,null,new BigDecimal(1));
                    saveToRedis(packageIdHasSuffix, moveTofile.split(CommonConstants.NAME.PACKAGE_FIX)[0] + CommonConstants.NAME.ZIP, mode, isCenter,ftpNodeId);
                    insertSectionStepRecorders(map, new BigDecimal(1), sectionName, packageIdHasSuffix, totalSectionNumber, new BigDecimal(0));
                }
            }

        } catch (Exception e) {
            logger.error("发生错误doDecrypt", e);
            String operaState = "";
            String sendState = RecvSendStateEnum.FAIL.getStateCode();
            //saveErrorState(packageIdHasSuffix, sectionParam, operaState, sendState, startTime);
            throw new RuntimeException("doLeafEncrypt失败 " + packageIdHasSuffix);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertSectionStepRecorders(ConcurrentMap param, BigDecimal doactResult, String sectionName, String
            jobId, BigDecimal sectionTotalNumber, BigDecimal direction) {
        String tempSectionName = sectionName;
        int sectionNameIndex = -1;
        // 如果SectionName中有$符，截取调后续字段内容 -- modify by jzkan，20190505
        if ((sectionNameIndex = tempSectionName.indexOf("$")) != -1) {
            tempSectionName = tempSectionName.substring(0, sectionNameIndex);
            logger.debug("SectionName has $, substring it : " + tempSectionName);
        }
        SectionStepRecorders sectionStepRecorders = new SectionStepRecorders();
        sectionStepRecorders.setId(RandomGUID.getGuid());
        sectionStepRecorders.setSectionParam(new Gson().toJson(param));
        sectionStepRecorders.setDoactResult(doactResult);
        sectionStepRecorders.setSectionName(tempSectionName);
        sectionStepRecorders.setPackageId(jobId);
        sectionStepRecorders.setTotalSectionNumber(sectionTotalNumber);
        sectionStepRecorders.setDirection(direction);
        sectionStepRecordersMapper.insertSelective(sectionStepRecorders);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void doMainEncrypt(String packageIdHasSuffix, String sectionName,String mode, boolean isCenter, String sectionParam,
                              BigDecimal totalSectionNumber, Date startTime, String biz_sn, String efsUrl) {

        Gson gson = new Gson();
        ConcurrentHashMap<String, Object> s = gson.fromJson(sectionParam, ConcurrentHashMap.class);
        List<ConcurrentHashMap<String, Object>> param = (List<ConcurrentHashMap<String, Object>>) s.get("PARAM");
        Map<String, Object> map1 = null;
        for (Map<String, Object> item : param) {
            String PACKAGE_ID = String.valueOf(item.get("PACKAGE_ID"));
            if (PACKAGE_ID.equals(packageIdHasSuffix)) {
                map1 = item;
                break;
            }
        }

        String filePath = String.valueOf(map1.get("FILE_PATH"));
        File file = new File(filePath);
        String parentDir = file.getParent();
        //创建存放加密文件的文件夹
        String moveToPath = parentDir + CommonConstants.NAME.FILESPLIT + "ZSE01";
        PackUtil.makeDir(moveToPath);
        String moveTofile = moveToPath + CommonConstants.NAME.FILESPLIT + file.getName();

        //String sectionName = "class " + FileEncryptLeafSection.class.getName();
        try {
            String operaState = CommonConstants.OPERATESTATE.JIAM;
            String sendState = "";

            ConcurrentHashMap map = saveEncryptMainSuccessState(packageIdHasSuffix, sectionParam, operaState, sendState, totalSectionNumber, startTime);
            saveCaStepRecorders(biz_sn, CommonConstants.CA.BACK_SUCCESS);
            FileUtil.copyZipRetry(efsUrl, moveTofile);

            //FileUtil.delAllFile(efsUrl);
            String packName = packageIdHasSuffix.split("\\.")[0];
            insertSectionStepRecorders(map, new BigDecimal(1), sectionName, packName, totalSectionNumber, new BigDecimal(0));
            String ftpNodeId = (String) map.get("DOWN_FTP_NODE_ID");
            saveToRedis(packageIdHasSuffix, moveTofile, mode, isCenter,ftpNodeId);
            new File(efsUrl).delete();
        } catch (Exception e) {
            logger.error("", e);
            String operaState = "";
            String sendState = RecvSendStateEnum.FAIL.getStateCode();
            //try {
            //saveErrorState(packageIdHasSuffix, sectionParam, operaState, sendState, startTime);
            //} catch (BusinessErrorException e1) {
            //    e1.printStackTrace();
            //}
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void batchUpdateCaStep(String packageId, String mode, String backStatus, BigDecimal executeSuccess) throws Exception {
        String packName= packageId.split("\\.")[0];
        List<CaStepRecorders> list = caStepRecordersMapper.selectByPackageId(packageId);
        for (CaStepRecorders item : list) {
            String biz_sn = item.getBizSn();
            if(item.getPackageId().split("\\.")[0].equals(packName))
            {
                updateCaStepRecorders(biz_sn, mode, backStatus, executeSuccess);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateCaStepRecorders(String biz_sn, String mode, String backStatus, BigDecimal executeSuccess) throws Exception {
        CaStepRecorders csr = new CaStepRecorders();
        csr.setBizSn(biz_sn);
        csr.setMode(mode);
        csr.setCallStatus(new BigDecimal(1));
        csr.setBackStatus(backStatus);
        csr.setExecuteStatus(executeSuccess);
        caStepRecordersMapper.updateByPrimaryKeySelective(csr);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCaStepRecorders(String biz_sn, String backStatus) throws Exception {
        CaStepRecorders csr = new CaStepRecorders();
        csr.setBizSn(biz_sn);
        csr.setBackStatus(backStatus);
        caStepRecordersMapper.updateByPrimaryKeySelective(csr);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ConcurrentHashMap saveEncryptMainSuccessState(String packageIdHasSuffix, String sectionParam, String operaState, String sendState, BigDecimal totalSectionNumber, Date startTime) throws Exception {
        Gson gson = new Gson();
        ConcurrentHashMap<String, Object> map = gson.fromJson(sectionParam, ConcurrentHashMap.class);

        List<LinkedTreeMap<String, Object>> param = (List<LinkedTreeMap<String, Object>>) map.get("PARAM");

        List<LinkedTreeMap<String, Object>> paramItemList = new ArrayList<>();
        for (int i = 0; i < param.size(); i++) {
            //if (String.valueOf(param.get(i).get("PACKAGE_ID")).equals(packageIdHasSuffix)) {
            paramItemList.add(param.get(i));
            //}
        }

        List<String> toNodeId = (List<String>) map.get("TO_NODE_ID");

        //先得到后期需要存放的list
        List<ConcurrentHashMap<String, Object>> param1 = new ArrayList<ConcurrentHashMap<String, Object>>();

        for (Object item : paramItemList) {
            gson = new Gson();
            ConcurrentHashMap<String, Object> s = gson.fromJson(gson.toJson(item), ConcurrentHashMap.class);

            //参数准备
            String filePath = String.valueOf(s.get("FILE_PATH"));
            String packageId = String.valueOf(s.get("PACKAGE_ID"));

            File file = new File(filePath);
            String parentDir = file.getParent();

            //往当前状态和操作状态表里插入数据
            ConcurrentHashMap<String, Object> map2 = new ConcurrentHashMap<String, Object>();
            //操作结束时间
            BigDecimal start = new BigDecimal(startTime.getTime());
            BigDecimal end = new BigDecimal(System.currentTimeMillis());
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
                map2.put("SPEND_TIME", spendTime);
                upStatusService.updateCurState(map2);
            }


            for (String app : toNodeId) {
                NodeAppBean nodeApp = nodeAppBeanMapper.selectByPrimaryKey(app);
                ConcurrentHashMap<String, Object> zip1 = new ConcurrentHashMap<String, Object>();
                zip1.put("FILE_PATH", filePath);
                zip1.put("TO_NODE_ID", app);
                zip1.put("NODE_ID", FileConfigUtil.CURNODEID);
                zip1.put("PACKAGE_ID", packageId);
                param1.add(zip1);
            }

        }
        map.put("PARAM", param1);

        return map;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ConcurrentHashMap saveEncryptLeafSuccessState(String packageIdHasSuffix, String sectionParam, String operaState, String sendState, BigDecimal totalSectionNumber, Date startTime) throws Exception {
        Gson gson = new Gson();
        ConcurrentHashMap<String, Object> map = gson.fromJson(sectionParam, ConcurrentHashMap.class);

        List<LinkedTreeMap<String, Object>> param = (List<LinkedTreeMap<String, Object>>) map.get("PARAM");

        List<LinkedTreeMap<String, Object>> paramItemList = new ArrayList<>();
        for (int i = 0; i < param.size(); i++) {
            //if (String.valueOf(param.get(i).get("PACKAGE_ID")).equals(packageIdHasSuffix)) {
            paramItemList.add(param.get(i));
            //}
        }

        List<String> toNodeId = (List<String>) map.get("TO_NODE_ID");

        //先得到后期需要存放的list
        List<ConcurrentHashMap<String, Object>> param1 = new ArrayList<ConcurrentHashMap<String, Object>>();

        for (Object item : paramItemList) {
            gson = new Gson();
            ConcurrentHashMap<String, Object> s = gson.fromJson(gson.toJson(item), ConcurrentHashMap.class);

            //参数准备
            String filePath = String.valueOf(s.get("FILE_PATH"));
            String packageId = String.valueOf(s.get("PACKAGE_ID"));

            File file = new File(filePath);
            String parentDir = file.getParent();

            //往当前状态和操作状态表里插入数据
            ConcurrentHashMap<String, Object> map2 = new ConcurrentHashMap<String, Object>();
            //操作结束时间
            BigDecimal start = new BigDecimal(startTime.getTime());
            BigDecimal end = new BigDecimal(System.currentTimeMillis());
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
                map2.put("SPEND_TIME", spendTime);
                upStatusService.updateCurState(map2);
            }

            /**
             for (String app : toNodeId) {
             NodeAppBean nodeApp = nodeAppBeanMapper.selectByPrimaryKey(app);

             ConcurrentHashMap<String, Object> zip1 = new ConcurrentHashMap<String, Object>();
             zip1.put("FILE_PATH", filePath);
             zip1.put("TO_NODE_ID", app);
             zip1.put("NODE_ID", FileConfigUtil.CURNODEID);
             zip1.put("PACKAGE_ID", packageId);

             param1.add(zip1);
             }
             **/

            s.put("FILE_PATH", parentDir + CommonConstants.NAME.FILESPLIT + "ZSE01" + CommonConstants.NAME.FILESPLIT + packageId);
            s.put("TO_NODE_ID", toNodeId.get(0));
            s.put("NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
            //构造传给下个节点的参数
            param1.add(s);
        }
        map.put("PARAM", param1);

        return map;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ConcurrentHashMap saveDecryptMainSuccessState(String sectionParam, String operaState, String sendState, BigDecimal totalSectionNumber, Date startTime) throws Exception {

        Gson gson = new Gson();
        ConcurrentHashMap<String, Object> map = gson.fromJson(sectionParam, ConcurrentHashMap.class);

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

        //准备参数
        ConcurrentHashMap<String, Object> map1 = new ConcurrentHashMap<String, Object>();

        BigDecimal start = new BigDecimal(startTime.getTime());


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
        map1.put("SPEND_TIME", spendTime);

        //插入数据扭转进程表和更新状态表
        //插入数据包当前状态表和数据包流水状态表
        upStatusService.updateCurState(map1);

        //根据包名截取去那里的appID
        String[] appIdTo = PackUtil.splitAppTo(packageId);

        for (String app : appIdTo) {
            NodeAppBean nodeApp = nodeAppBeanMapper.selectByPrimaryKey(app);

            ConcurrentHashMap<String, Object> zip1 = new ConcurrentHashMap<String, Object>();
            zip1.put("FILE_PATH", packPath);
            zip1.put("TO_NODE_ID", nodeApp.getNodeId());
            zip1.put("NODE_ID", FileConfigUtil.CURNODEID);
            zip1.put("PACKAGE_ID", packageId);

            param.add(zip1);
        }


        map.put("CUR_NODE_ID", FileConfigUtil.CURNODEID);
        map.put("PARAM", param);

        return map;

    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ConcurrentHashMap saveDecryptLeafSuccessState(String sectionParam, String operaState, String sendState, BigDecimal totalSectionNumber, Date startTime) throws Exception {

        Gson gson = new Gson();
        ConcurrentHashMap<String, Object> map = gson.fromJson(sectionParam, ConcurrentHashMap.class);

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

        //准备参数
        ConcurrentHashMap<String, Object> map1 = new ConcurrentHashMap<String, Object>();

        BigDecimal start = new BigDecimal(startTime.getTime());


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
        map1.put("SPEND_TIME", spendTime);
        //插入数据扭转进程表和更新状态表
        //插入数据包当前状态表和数据包流水状态表
        upStatusService.updateCurState(map1);

        //构造下个section需要参数
        zip.put("FILE_PATH", newzip.getAbsolutePath());
        zip.put("PACKAGE_ID", packageId);
        param.add(zip);


        map.put("CUR_NODE_ID", FileConfigUtil.CURNODEID);
        map.put("PARAM", param);
        //map.put("MARK", CommonConstants.STATE.SELF);

        return map;

    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorState(String packageId, String sectionParam, String operaState, String sendState, Date startTime) throws BusinessErrorException {
        Gson gson = new Gson();
        ConcurrentHashMap<String, Object> map = gson.fromJson(sectionParam, ConcurrentHashMap.class);
        List<String> toNodeId = (List<String>) map.get("TO_NODE_ID");
        for(String s1:toNodeId)
        {
            //往当前状态和操作状态表里插入数据
            ConcurrentHashMap<String, Object> map2 = new ConcurrentHashMap<String, Object>();
            //循环更新数据
            map2.put("NODE_ID", String.valueOf(map.get("CUR_NODE_ID")));
            map2.put("OPERATE_STATE_DM", operaState);
            map2.put("SEND_STATE_DM", sendState);
            map2.put("PACKAGE_ID", packageId);
            map2.put("TO_NODE_ID", s1);
            upStatusService.updateCurState(map2);
        }
    }

    public void mainIntoDatabase(String newPath, String fileName,String ftpNodeId) throws Exception {
        //构造下个section需要参数
        ConcurrentHashMap<String, Object> zip = new ConcurrentHashMap<String, Object>();
        List<ConcurrentHashMap<String, Object>> toNext = new ArrayList<ConcurrentHashMap<String, Object>>();
        ConcurrentHashMap<String, Object> nextMap = new ConcurrentHashMap<String, Object>();
        zip.put("FILE_PATH", newPath);
        zip.put("PACKAGE_ID", fileName);
        toNext.add(zip);

        nextMap.put("CUR_NODE_ID", FileConfigUtil.CURNODEID);
        nextMap.put("PARAM", toNext);
        nextMap.put("DOWN_FTP_NODE_ID",ftpNodeId);

        pkgGetterManger.mainIntoDatabase(ExchangeNodeType.MAIN, fileName, null, null, nextMap);

        logger.info("中心入库-------压缩包：" + fileName + "成功");
    }

    public void mainRetryDown(String fileName) throws Exception {
        logger.info("中心节点下载-------压缩包：" + fileName + "开始");
        // 更新状态
        //getPackService.updateUnfinishedById(fileName.split("\\.")[0]);
        ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<String, Object>();
        paramMap.put("PACKAGE_ID", fileName);// 数据包名
        //paramMap.put("NODE_ID", curNodeId);// 当前FTP节点
        //调用下载链路
        pkgGetterManger.downLoadPackage(ExchangeNodeType.MAIN, fileName, null, null, paramMap);
        logger.info("中心节点下载-------压缩包：" + fileName + "成功");
    }

    public void leafRetryDown(String fileName) throws Exception {
        logger.info("叶子节点下载-------压缩包：" + fileName + "开始");
        // 更新状态
        //getPackService.updateUnfinishedById(fileName.split("\\.")[0]);
        // 传递参数
        ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<String, Object>();
        paramMap.put("PACKAGE_ID", fileName);// 数据包名
        //paramMap.put("NODE_ID", curNodeId);// 当前FTP节点
        //调用下载链路
        pkgGetterManger.downLoadPackage(ExchangeNodeType.LEAF, fileName, null, null, paramMap);
        logger.info("叶子节点下载-------压缩包：" + fileName + "成功");
    }

    public void leafRetryUp(String fileName) throws Exception {
        logger.info("叶子节点上传------压缩包：" + fileName + "开始");
        // 更新状态
        //getPackService.updateUnfinishedById(fileName);
        //获取包的相关参数
        String pageId = fileName.split("\\.")[0] + CommonConstants.NAME.ZIP;
        DataPackBean dataPackBean = dataPackBeanMapper.selectByPrimaryKey(pageId);
        //String folderPath = null;
        String packagePath = null;
        if (dataPackBean != null) {
            //folderPath = dataPackBean.getFolderPath();
            packagePath = dataPackBean.getPackagePath();
        }

        //然后调用上传链路
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        //map.put("PACK_DIR_PATH", folderPath);
        map.put("FILE_NAME", fileName.split("\\.")[0]);
        map.put("PACKAGE_PATH", packagePath);

        pkgUploaderManager.uploadPackage(ExchangeNodeType.LEAF, fileName, null, null, map);
        logger.info("叶子节点上传-------压缩包：" + fileName + "成功");

    }

    public void saveToRedis(String packageId, String filePath, String mode, boolean isCenter,String ftpNodeId) throws Exception {
        if (mode.equals(CommonConstants.CA.ENCRYPT)) {
            //叶子上传
            //leafRetryUp(packageId);
            redisUtil.pushUpTask(filePath,packageId);
        }

        if (mode.equals(CommonConstants.CA.DECRYPT)) {
            if (isCenter) {
                //中心下载
                //mainIntoDatabase(filePath, packageId,ftpNodeId);
                // PKG待下载任务,写入redis
                redisUtil.pushDownTask(filePath,packageId,ftpNodeId);
            } else {
                //leafRetryDown(packageId);
                redisUtil.pushDownTask(filePath,packageId,ftpNodeId);
            }
        }
    }

    /**
     @Transactional(propagation = Propagation.REQUIRES_NEW)
     public void doNextSection(String packageId, String filePath, String mode, boolean isCenter,String ftpNodeId) throws Exception {
     if (mode.equals(CommonConstants.CA.ENCRYPT)) {
     //叶子上传
     leafRetryUp(packageId);
     }

     if (mode.equals(CommonConstants.CA.DECRYPT)) {
     if (isCenter) {
     //中心下载
     mainIntoDatabase(filePath, packageId,ftpNodeId);
     } else {
     leafRetryDown(packageId);
     }
     }
     }
     **/
}
