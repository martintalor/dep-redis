package com.iflytek.dep.server.section;


import com.google.gson.Gson;
import com.iflytek.dep.common.security.EncryptException;
import com.iflytek.dep.server.config.web.ApplicationContextRegister;
import com.iflytek.dep.server.constants.ExceptionState;
import com.iflytek.dep.server.constants.PkgStatus;
import com.iflytek.dep.server.constants.RecvSendStateEnum;
import com.iflytek.dep.server.mapper.DataNodeProcessBeanMapper;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FileEncryptMainSection  implements Section, Status {
    private static Logger logger = LoggerFactory.getLogger(FileEncryptMainSection.class);
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
        logger.info("[{}] job has [{}],package id [{}] in method ,jobId is [{}]",this.getClass(),threadJobSize.incrementAndGet(),pkgId,jobId);
        sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));

        if(FileConfigUtil.ISCA){
            //得到参数
            List<Map<String, Object>> param = (List<Map<String, Object>>) map.get("PARAM");
            for (Map<String, Object> item : param) {
                if (((String) item.get("TO_NODE_ID")).equals(FileConfigUtil.CURNODEID)) {
                    logger.info("FileCopyToUnPackMainSection已做处理，直接传递到下一个节点");
                } else {
                    break;
                }
            }

            sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber, new BigDecimal(0));
            SectionNode nextSectionNode = sectionNode.getNext();
            if (nextSectionNode != null) {
                nextSectionNode.getCurrent().doAct(pkgId, jobId, nextSectionNode, totalSectionNumber, map);
            }
            logger.info("此包不需要中心加密，只是传递参数，经过中心加密，后边代码不需要执行");
            return;
        }

        //得到参数
        List<ConcurrentHashMap<String, Object>> param = (List<ConcurrentHashMap<String, Object>>) map.get("PARAM");
        //List<String> toNodeId = (List<String>) map.get("TO_NODE_ID");
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
        //循环之外就用CountDownLatch控制线程的执行
        CountDownLatch countDownLatch = new CountDownLatch(param.size());
        //循环param里的路径有一个就开启一个新的线程来处理加密这个事件
        for (Object item : param) {
            Gson gson = new Gson();
            ConcurrentHashMap<String, Object> s = gson.fromJson(gson.toJson(item), ConcurrentHashMap.class);
            //参数准备
            String filePath = String.valueOf(s.get("FILE_PATH"));
            String packageId = String.valueOf(s.get("PACKAGE_ID"));
            String nodeID = String.valueOf(s.get("TO_NODE_ID"));
            //构造更新参数
            ConcurrentHashMap<String, Object> map2 = new ConcurrentHashMap<String, Object>();
            //加密前将加密中的状态入库
            map2.put("NODE_ID", FileConfigUtil.CURNODEID);
            map2.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.ZXJIAMZ);
            map2.put("PACKAGE_ID", packageId);
            map2.put("TO_NODE_ID", nodeID);
            upStatusService.updateCurState(map2);
            File file = new File(filePath);
            String parentDir = file.getParent();
            //如果是中心节点需要往下传
            //判断用原本系统加密方式，还是用ck预留扩展的加密方式
            String publickey = null;
            if (CommonConstants.STATE.SELF.equals(MARK)) {

                //根据不同appTo选择不同加密公钥并且存放入不同子文件夹
                publickey = environment.getProperty(nodeID + CommonConstants.NAME.KEY);
                //创建存放加密文件的文件夹
                PackUtil.makeDir(parentDir + CommonConstants.NAME.FILESPLIT + nodeID);
                //操作开始时间
                BigDecimal start = new BigDecimal(System.currentTimeMillis());
                String operaState = "";// 操作状态
                String sendState = "";// 流转状态
                //加密
                try {
                    createPackService.encryptZip(filePath, publickey, nodeID);
                    operaState = CommonConstants.OPERATESTATE.ZXJIAM;
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
                    map2.put("NODE_ID", FileConfigUtil.CURNODEID);
                    map2.put("OPERATE_STATE_DM", operaState);
                    map2.put("SEND_STATE_DM", sendState);
                    map2.put("PACKAGE_ID", packageId);
                    map2.put("TO_NODE_ID", nodeID);
                    map2.put("SPEND_TIME", spendTime);
                    upStatusService.updateCurState(map2);
                    s.put("FILE_PATH", parentDir + CommonConstants.NAME.FILESPLIT + nodeID + CommonConstants.NAME.FILESPLIT + packageId);
                    s.put("TO_NODE_ID", nodeID);
                    s.put("NODE_ID", FileConfigUtil.CURNODEID);
                    //构造传给下个节点的参数
                    param1.add(s);
                } catch (Exception e) {
                    logger.error("状态更新失败", packageId,e);
                }
            }

            if (CommonConstants.STATE.CA.equals(MARK)) {

                //ca预留的地方
            }
        }
        //如果上边俩线程没执行完就要等待其执行
        map.put("PARAM", param1);
        if (map.get("flag") == null) {
            //删除原没加密压缩包
//                PackUtil.deleteZip(String.valueOf(map.get("FILE_PATH")));
        }

        //如果只传入中心入库则不传入下个节点上传
        if (param1 == null || param1.size() == 0) {
            sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
            logger.info("此包不需要上传，到中心入库即截止!");
            return;
        }

        if(map.get("flag") == null || true == (boolean)map.get("flag")){
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
