package com.iflytek.dep.server.service.dataPack;

import com.google.gson.Gson;
import com.iflytek.dep.common.exception.BusinessErrorException;
import com.iflytek.dep.server.constants.ExceptionState;
import com.iflytek.dep.server.constants.RecvSendStateEnum;
import com.iflytek.dep.server.file.FileServiceImpl;
import com.iflytek.dep.server.file.ZipServiceImpl;
import com.iflytek.dep.server.ftp.core.FtpClientTemplate;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.mapper.NodeLinkBeanMapper;
import com.iflytek.dep.server.model.FTPConfig;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.model.NodeLinkBean;
import com.iflytek.dep.server.service.threadPool.DepFtpFileUploadService;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.ConcurrentCache;
import com.iflytek.dep.server.utils.FileConfigUtil;
import com.iflytek.dep.server.utils.PackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Package com.iflytek.dep.server.service.dataPack
 * @Description: 数据包发送
 * @date 2019/2/22--17:26
 */
@Service
public class SendPackService {

    private static Logger logger = LoggerFactory.getLogger(SendPackService.class);

    @Autowired
    Environment environment;

    @Autowired
    UpStatusService upStatusService;

    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;

    @Autowired
    NodeLinkBeanMapper linkBeanMapper;

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    ZipServiceImpl zipService;

    @Autowired
    SendAckService sendAckService;

    @Autowired
    DepFtpFileUploadService depFtpFileUploadService;


    /**
     *@描述 获取传入的文件路径列表
     *@参数  [map] PARAM ->filePathList
     *@返回值  void
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/3/14
     *@修改人和其它信息
     */
    // 多个package，顺序发包
    public void upPackList(ConcurrentHashMap<String, Object> map) throws Exception {

        ArrayList<ConcurrentHashMap<String, Object>> filePathList = (ArrayList) map.get("PARAM");

//        SendPackService sendPackService = SpringUtil.getBean(SendPackService.class);

        // 如果传入参数为空
        if (filePathList == null || filePathList.size() == 0) {
            map.put("UP_ALL_FLAG","FALSE");
            map.put("ERROR_MSG","没有可上传文件!");
            logger.error("没有可上传文件!");
            return;
        }

        String packageId = "";// 数据包名
//        CountDownLatch cdl = new CountDownLatch(filePathList.size());
//        Exception ex = null;
        for (Object fileMap : filePathList) {
            Gson gson =  new Gson();
            ConcurrentHashMap<String, Object> mapTemp = gson.fromJson(gson.toJson(fileMap),ConcurrentHashMap.class);

//            listBlock.put(mapTemp);
            // 开启上传线程 end
            packageId = (String) mapTemp.get("PACKAGE_ID");// 数据包名
            logger.info("upPackThread start  : {}" , packageId);
            String toNodeId = (String) mapTemp.get("TO_NODE_ID");
            // 调用上传线程
//            depFtpFileUploadService.upPackThread(mapTemp,cdl,map);
            try {
                // 中心目标节点和当前节点相同，不上传
                if (!toNodeId.equals(FileConfigUtil.CURNODEID)) {
                    upPack(mapTemp);
                }
                logger.info("上传结束！packageId:" + packageId);
            }
            catch (Exception e){
                logger.error("上传失败，当前上传任务终止，目标节点toNodeId:{},packageId:{}",toNodeId,packageId,e);
                //上传失败，打标志阻止前置机数据包被挪走 -- modify by jzkan 20190510
                map.put("UP_ALL_FLAG","FALSE");
                map.put("ERROR_MSG","上传失败!");
//                ex = e;
            }
        }
//        if(ex  != null){
//            throw ex;
//        }
    }


    /**
     * @描述 当前节点是文档服务器，下一节点是待上传的FTP
     * 上传，同时更新本节点、下一节点状态
     * @参数 [map] PACKAGE_ID 数据包id
     * @返回值 void
     * @创建人 姚伟-weiyao2
     * @创建时间 2019/2/27
     * @修改人和其它信息
     */
    //@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
    public void upPack(ConcurrentHashMap<String, Object> map) throws Exception {
        // 开始时间
        BigDecimal start = new BigDecimal(System.currentTimeMillis());

        String packageId = (String) map.get("PACKAGE_ID");// 数据包名
        String curNodeId = (String) map.get("NODE_ID");// 当前节点
        String toNodeId = (String) map.get("TO_NODE_ID");// 目标节点
        String localPath = (String) map.get("FILE_PATH");//源文件地址
        Boolean isCenter = FileConfigUtil.ISCENTER;// 是否中心节点

        // 0、获取待上传的FTP节点
        // nextNodeId为待上传的FTP_ID，根据packageId、toNodeId、curNodeId从nodelink表中查询出来

        // ********* 重点>>>>> 永远只取主包,只有主包有nodeLink <<<<<<<*************
        String mainPackageId = packageId.split("\\.")[0] + CommonConstants.NAME.ZIP;

        NodeLinkBean linkBean = new NodeLinkBean();
        linkBean.setToNodeId(toNodeId);
        linkBean.setPackageId(mainPackageId);
        linkBean.setLeftNodeId(curNodeId);
        try {
            linkBean = linkBeanMapper.getLinkByCurNode(linkBean);
        } catch (Exception e) {
            logger.error("linkBean class [{}] ,eroor[{}] ",this.getClass().getName() ,e);
        }

        if (linkBean == null) {
            logger.error("找不到上传FTP！file path[{}] ,packageId[{}]", localPath, packageId);
            updateStatus(isCenter,
                    packageId,
                    curNodeId,
                    toNodeId,
                    RecvSendStateEnum.FAIL.getStateCode(),null);
            throw new BusinessErrorException(ExceptionState.UP.getCode(),ExceptionState.UP.getName() + "找不到上传FTP！file path:" + localPath + " ,packageId:" + packageId);
        }
        String ftpNodeId = linkBean.getRightNodeId();

        // 1、上传之前要记录下

        // 发送中状态
        String sendState = RecvSendStateEnum.SENDING.getStateCode();

        // 更新状态，生成ack
        updateStatus(isCenter,
                packageId,
                curNodeId,
                toNodeId,
                sendState,null);

        // FTP接收中
        updateStatus(isCenter,
                packageId,
                ftpNodeId,
                toNodeId,
                RecvSendStateEnum.RECVING.getStateCode(),null);

        // 2、根据nextNodeId获取FTP连接池，获取上传路径，上传文件
        logger.info("upPack ftpNodeId:" + ftpNodeId);
        FtpClientTemplate ftpClientTemplate = FtpClientTemplate.FTP_CLIENT_TEMPLATE.get(ftpNodeId);
        FTPConfig config = FtpClientTemplate.FTP_CONFIG.get(ftpNodeId);
        String host = ftpClientTemplate.getFtpClientConfig().getHost();

        logger.info("开始上传==>FTP=" + host + "==>packageId:" + packageId +  " ftp config:" + config.toString());

        String upPath = config.getDataPackageFolderUp();// /up/data

        Boolean bool = ftpClientTemplate.uploadFileReset(localPath, packageId, upPath);
//        Boolean bool = ftpClientTemplate.uploadFile(new File(localPath), upPath);

        // 3、上传之后要更新下状态
        String ftpState = "";// FTP更新状态
        if (bool) {
            sendState = RecvSendStateEnum.SENDSUCC.getStateCode();
            ftpState = RecvSendStateEnum.RECVED.getStateCode();
            logger.info("上传成功==>FTP=" + host + "==>packageId:" + packageId +  " ftp config:" + config.toString());
        } else {
            sendState = RecvSendStateEnum.FAIL.getStateCode();
            ftpState = RecvSendStateEnum.FAIL.getStateCode();
            logger.info("上传异常==>FTP=" + host + "==>packageId:" + packageId +  " ftp config:" + config.toString());
        }

        //操作结束时间
        BigDecimal end = new BigDecimal(System.currentTimeMillis());
        //计算耗时
        BigDecimal spendTime = end.subtract(start).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP);


        // 更新状态，生成ack
        updateStatus(isCenter, packageId, curNodeId, toNodeId, sendState, spendTime);

        // FTP接收完成
        updateStatus(isCenter,
                packageId,
                ftpNodeId,
                toNodeId,
                ftpState,
                spendTime);

        if (RecvSendStateEnum.FAIL.getStateCode().equals(sendState)) {
            throw new BusinessErrorException(ExceptionState.UP.getCode(),ExceptionState.UP.getName() + "curNodeId:" + curNodeId + ",packageId:" + packageId);
        }
    }

    /**
     *@描述 判断是否需要根据toNodeId循环更新
     *@参数  [flag, packageId, curNodeId, toNodeId, sendStateDm]
     * @param flag  [true/false]ture：叶子节点下载、中心节点上传，false：中心节点下载、叶子节点上传
     *@返回值  void
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/3/14
     *@修改人和其它信息
     */
    public void updateStatus(boolean flag,
                             String packageId,
                             String curNodeId,
                             String toNodeId,
                             String sendStateDm,
                             BigDecimal spendTime) throws BusinessErrorException {


        ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<String, Object>();

        if ( flag ) {
            // 叶子节点下载、中心节点上传，更新状态

            paramMap.put("PACKAGE_ID", packageId);
            paramMap.put("NODE_ID", curNodeId);
            paramMap.put("TO_NODE_ID", toNodeId);
            paramMap.put("OPERATE_STATE_DM", sendStateDm);
            paramMap.put("SEND_STATE_DM", sendStateDm);

            if ( spendTime != null) {
                paramMap.put("SPEND_TIME",spendTime);
            }

            upStatusService.updateCurState(paramMap);
//            createUpAck(paramMap);
//            sendAckService.createUpAck(paramMap);
        } else {

            // 中心节点下载、叶子节点上传，要循环toNodeId，更新状态，
            String[] appIdTos = PackUtil.splitAppTo(packageId);
            for (String appIdTo : appIdTos) {

                // 获取appId对应的toNodeId 后续这些数据都可以作为缓存表读入server中
                String toId = (String) ConcurrentCache.getFieldValue("TO_NODE_ID_" + appIdTo);
                if (toId == null) {
                    NodeAppBean nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(appIdTo);
                    toId =  nodeAppBean.getNodeId();
                    ConcurrentCache.setFieldValue("TO_NODE_ID_" + appIdTo,  toId);
                }
                toId = (String) ConcurrentCache.getFieldValue("TO_NODE_ID_" + appIdTo);

                paramMap = new ConcurrentHashMap<String, Object>();
                paramMap.put("PACKAGE_ID", packageId);
                paramMap.put("NODE_ID", curNodeId);
                paramMap.put("TO_NODE_ID", toId);
                paramMap.put("OPERATE_STATE_DM", sendStateDm);
                paramMap.put("SEND_STATE_DM", sendStateDm);

                if ( spendTime != null) {
                    paramMap.put("SPEND_TIME",spendTime);
                }
                // 3 修改节点状态
                upStatusService.updateCurState(paramMap);

                // 4、每次发送都有ack生成
//                createUpAck(paramMap);
//                sendAckService.createUpAck(paramMap);
            }// for end

        }// else end

    }


    /**
     * @描述 叶子节点下载时，toNodeId就是当前节点
     * 数据包当前节点是FTP服务器，应用下一节点（中心文档服务器）记录状态
     * 下载同时更新本节点、上一节点状态，
     * @参数 [map] PACKAGE_ID、NODE_ID
     * @返回值 void
     * @创建人 姚伟-weiyao2
     * @创建时间 2019/3/4
     * @修改人和其它信息
     */
    //@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
    public void downPackLeaf(ConcurrentHashMap<String, Object> map) throws BusinessErrorException {

        String msg = Thread.currentThread().getName() + " downPackLeaf start -->"; // 异常日志

        // 开始时间
        BigDecimal start = new BigDecimal(System.currentTimeMillis());

        String packageId = (String) map.get("PACKAGE_ID");
        String ftpNodeId = (String) map.get("NODE_ID");
        Boolean statusFlag = !FileConfigUtil.ISCENTER;// 叶子节点下载

        logger.info( msg + " ,packageId：" + packageId+ ", 调用upStatusService.insertPage|insertSubPage start") ;

        logger.info( msg + " ,packageId：" + packageId+ ", 调用upStatusService.insertPage|insertSubPage end") ;


        // 文档服务器节点,下载时叶子节点时就是目标节点
        // 如果要经过多条链路，叶子节点下载时不是最终目标，怎么找下个节点,所以还是根据packageId、left、right找link
        String curNodeId = FileConfigUtil.CURNODEID;
        String toNodeId = curNodeId;

        // 0、先创建nodeLink
        logger.info("downPackLeaf packageId:" + packageId);
//        upStatusService.createNodeLinks(packageId,curNodeId);

        try {
            //打包之前先在主包表中插入虚拟数据
            String fileName1 = packageId.split("\\.")[0];
            upStatusService.insertPage(fileName1);
            upStatusService.insertSubPage(packageId);
            upStatusService.createNodeLinks(packageId,curNodeId);
        } catch (Exception e) {
            logger.error("[{}],error[{}]",this.getClass().getName(),e);
            updateStatus(statusFlag,
                    packageId,
                    ftpNodeId,
                    toNodeId,
                    RecvSendStateEnum.FAIL.getStateCode(),
                    null
            );
            throw new BusinessErrorException(ExceptionState.DOWN.getCode(),ExceptionState.DOWN.getName() + "msg:" + e.getMessage());
        }

        map.put("NODE_ID",curNodeId);// 重置当前节点
        map.put("CUR_NODE_ID", curNodeId);// 当前节点
        map.put("TO_NODE_ID", curNodeId);// 目标节点


        // 1、下载前跟新状态

        // FTP节点发送中状态更新
        // 1.1 FTP发送
        // flag:false,叶子节点下载
        updateStatus(statusFlag,
                packageId,
                ftpNodeId,
                toNodeId,
                RecvSendStateEnum.SENDING.getStateCode(),
                null
        );

        // 1.2 当前节点在接收中
        updateStatus(statusFlag,
                packageId,
                curNodeId,
                toNodeId,
                RecvSendStateEnum.RECVING.getStateCode(),
                null);

        // 2、根据curNodeId获取FTP连接池，获取上传路径，上传文件
        FtpClientTemplate ftpClientTemplate = FtpClientTemplate.FTP_CLIENT_TEMPLATE.get(ftpNodeId);
        FTPConfig config = FtpClientTemplate.FTP_CONFIG.get(ftpNodeId);
        String downPath = config.getDataPackageFolderDown();// FTP的下载路径 "/down/data/"
        //叶子节点生成不含逗号的下载路径！注意与中心节点区别
        String localPath = fileService.makeDirByPackageId(packageId.replaceAll(CommonConstants.NAME.APPSPLIT,"")) + File.separator;
        String fileName = packageId;// 本地文件路径

        // 连接FTP下载文件到本地（文档服务器）
        msg =  "curNodeId:" + curNodeId
                + " ftp config:" + config.toString()
                + ",packageId:" + packageId
                + ", ftpNodeId：" + ftpNodeId
                + ", localPath：" + localPath + fileName
                + ", ftpPath：" + downPath;

        logger.info("文件开始下载！msg:" + msg);

        // 断点续传方法
        Boolean bool = ftpClientTemplate.downloadFileReset(downPath, fileName, localPath);

//        Boolean bool = ftpClientTemplate.downloadFile(downPath, fileName, localPath);

        map.put("PACK_PATH", localPath + fileName);// 传递到下个节点用
        map.put("DOWN_FTP_NODE_ID",ftpNodeId);// 下载文件的FTP，传递到后续节点使用

        // 3、下载之后要更新下状态
        String ftpState = "";
        String sendState = "";
        if (bool) {

            logger.info("文件下载成功！msg:" + msg);
            sendState = RecvSendStateEnum.RECVED.getStateCode();
            map.put("SEND_STATE_DM", RecvSendStateEnum.RECVED.getStateCode()); // 已接收
            ftpState = RecvSendStateEnum.SENDSUCC.getStateCode();

        } else {
            logger.info("文件下载失败！msg:" + msg);
            sendState = RecvSendStateEnum.FAIL.getStateCode();
            ftpState = RecvSendStateEnum.FAIL.getStateCode();
        }

        // 非中心节点当前id为toNodeId，如果中心节点，可能appIdTo发往多个地方，则遍历插入状态，否则

        // 3.1 修改节点状态

        //操作结束时间
        BigDecimal end = new BigDecimal(System.currentTimeMillis());
        //计算耗时
        BigDecimal spendTime = end.subtract(start).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP);

        // FTP发送完成
        updateStatus(statusFlag,
                packageId,
                ftpNodeId,
                toNodeId,
                ftpState,
                spendTime);

        // 当前节点接收完成
        updateStatus(statusFlag,
                packageId,
                curNodeId,
                toNodeId,
                sendState,
                spendTime);


        if (RecvSendStateEnum.FAIL.getStateCode().equals(sendState)) {
            throw new BusinessErrorException(ExceptionState.DOWN.getCode(),ExceptionState.DOWN.getName() + "msg:" + msg );
//            throw new BusinessErrorException("002","数据包下载异常！" + ",curNodeId:" + curNodeId + ",packageId:" + packageId);
        }

    }

    /**
     * @描述 中心节点下载时，每次更新多个toNodeId的状态
     * 数据包当前节点是FTP服务器，应用下一节点（中心文档服务器）记录状态
     * 下载同时更新本节点、上一节点状态，
     * @参数 [map] PACKAGE_ID、NODE_ID
     * @返回值 void
     * @创建人 姚伟-weiyao2
     * @创建时间 2019/3/4
     * @修改人和其它信息
     */
    //@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
    public void downPackMain(ConcurrentHashMap<String, Object> map) throws BusinessErrorException {

        String msg = Thread.currentThread().getName() + "  downPackMain start -->"; // 异常日志

        logger.info( msg ) ;

        // 开始时间
        BigDecimal start = new BigDecimal(System.currentTimeMillis());

        String packageId = (String) map.get("PACKAGE_ID");
        String ftpNodeId = (String) map.get("NODE_ID");
        // 判断是什么节点，一个逻辑节点只会有一个文档服务器
        String curNodeId = FileConfigUtil.CURNODEID;// 文档服务器节点
        Boolean statusFlag = !FileConfigUtil.ISCENTER;// 中心节点下载，!true -> false

        logger.info( msg + " packageId：" + packageId+ ", 调用upStatusService.insertPage|insertSubPage start") ;


        try {
            // 打包之前先在主包表中插入虚拟数据
            String fileName1 = packageId.split("\\.")[0];
            upStatusService.insertPage(fileName1);
            upStatusService.insertSubPage(packageId);

            logger.info( msg + " packageId：" + packageId+ ", 调用upStatusService.insertPage|insertSubPage end") ;

            ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<String, Object>();

            // 0、先创建nodeLink
            logger.info( msg + " createNodeLinks ");
            upStatusService.createNodeLinks(packageId,curNodeId);
            logger.info( msg + " createNodeLinks end");

        } catch (Exception e) {
            logger.error("[{}],error[{}]",this.getClass().getName(),e);
            updateStatus(statusFlag,
                    packageId,
                    ftpNodeId,
                    "",
                    RecvSendStateEnum.FAIL.getStateCode(),
                    null
            );
            throw new BusinessErrorException(ExceptionState.DOWN.getCode(),ExceptionState.DOWN.getName() + "msg:" + e.getMessage());
        }

        // 1、更新状态

        // 1.1 FTP发送
        updateStatus(statusFlag, packageId, ftpNodeId, "",
                RecvSendStateEnum.SENDING.getStateCode(),null);

        // 1.2 当前节点接收中
        updateStatus(statusFlag, packageId, curNodeId, "",
                RecvSendStateEnum.RECVING.getStateCode(),null);

        // 2、根据curNodeId获取FTP连接池，获取上传路径，上传文件
        FtpClientTemplate ftpClientTemplate = FtpClientTemplate.FTP_CLIENT_TEMPLATE.get(ftpNodeId);
        FTPConfig config = FtpClientTemplate.FTP_CONFIG.get(ftpNodeId);
        String downPath = config.getDataPackageFolderDown() ;// FTP的下载路径 "/down/data/"
        String localPath = fileService.makeDirByPackageId(packageId.replaceAll(CommonConstants.NAME.APPSPLIT,"")) + File.separator;
        String fileName = packageId;// 本地文件路径

        // 连接FTP下载文件到本地（文档服务器）

        msg +=  "curNodeId:" + curNodeId
                + ",packageId:" + packageId
                + " ftp config:" + config.toString()
                + ", ftpNodeId：" + ftpNodeId
                + ", localPath：" + localPath + fileName
                + ", ftpPath：" + downPath;

        logger.info("文件开始下载！msg:" + msg);

        Boolean bool = ftpClientTemplate.downloadFileReset(downPath, fileName, localPath);

        map.put("PACK_PATH", localPath + fileName);// 传递到下个节点用
        map.put("DOWN_FTP_NODE_ID",ftpNodeId);// 下载文件的FTP，传递到后续节点使用

        // 3、下载之后要更新下状态，生成ack
        String sendStateDm = "";
        String ftpStateDm = "";
        if (bool) {
            logger.info("文件下载成功！msg:" + msg);
            sendStateDm = RecvSendStateEnum.RECVED.getStateCode();// 已接收
            ftpStateDm = RecvSendStateEnum.SENDSUCC.getStateCode();// 已经发送成功
        } else {

            sendStateDm = RecvSendStateEnum.FAIL.getStateCode();// 异常
            logger.error("FTP下载异常！msg:" + msg);
            ftpStateDm = RecvSendStateEnum.FAIL.getStateCode();// 发送失败
        }


        //操作结束时间
        BigDecimal end = new BigDecimal(System.currentTimeMillis());
        //计算耗时
        BigDecimal spendTime = end.subtract(start).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_UP);

        // 3.1 FTP发送成功
        updateStatus(statusFlag, packageId, ftpNodeId, "",
                ftpStateDm,spendTime);

        // 3.2 接收成功
        updateStatus(statusFlag, packageId, curNodeId, "",
                sendStateDm,spendTime);

        if (RecvSendStateEnum.FAIL.getStateCode().equals(sendStateDm)) {
            throw new BusinessErrorException(ExceptionState.DOWN.getCode(),ExceptionState.DOWN.getName() + " msg:" + msg);
//            throw new BusinessErrorException("002","数据包下载异常！" + ",curNodeId:" + curNodeId + ",packageId:" + packageId);
        }

        logger.info(Thread.currentThread().getName() + " downPackMain end packageId:" + msg);
    }

    // 每到一个新的节点，更新一个数据包的状态，就要生成一个ack包
    // 记录当前的最新状态
    // 其实可以变成广播模式，将每个节点的状态表，每隔30秒发出广播，发送到其他节点->
    // ->再由接收节点插入数据库




}
