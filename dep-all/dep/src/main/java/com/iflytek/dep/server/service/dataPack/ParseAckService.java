package com.iflytek.dep.server.service.dataPack;

import com.iflytek.dep.common.exception.BusinessErrorException;
import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.server.ftp.core.FtpClientTemplate;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.mapper.NodeLinkBeanMapper;
import com.iflytek.dep.server.model.FTPConfig;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.model.NodeLinkBean;
import com.iflytek.dep.server.utils.*;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Package com.iflytek.dep.server.service.dataPack
 * @Description: ack生成及发送
 * @date 2019/3/20
 */
@Service
public class ParseAckService {

    private static Logger logger = LoggerFactory.getLogger(ParseAckService.class);


    @Autowired
    Environment environment;

    @Autowired
    UpStatusService upStatusService;

    @Autowired
    NodeLinkBeanMapper nodeLinkBeanMapper;

    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;



    /**
     *@描述 调用parseAckCatch方法，做catch处理，ack失败不影响主业务流程
     *@参数  [map]
     *@返回值  java.util.concurrent.ConcurrentHashMap<java.lang.String,java.lang.Object>
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/3/11
     *@修改人和其它信息
     */
    //@Async("parseAckAsyncServiceExecutor")
    public void parseAck(ConcurrentHashMap<String, Object> map) throws Exception {
        ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
        String ackId = "";
        String ftpNodeId = "";

        ackId = (String) map.get("PACKAGE_ID");// 监听ack包名

        logger.info(Thread.currentThread().getName() + " ACK包解析开始！ackId...{}",ackId);

        ftpNodeId = (String)map.get("NODE_ID");// 当前FTP

        parseAckCatch(map);// 解析ack

        logger.info(Thread.currentThread().getName() + " ACK包解析完成！ackId...{}",ackId);

        moveAck(ackId,ftpNodeId);// 解析成功后移除ack

        logger.info(Thread.currentThread().getName() + " ACK包move结束！ackId...{}",ackId);

    }

    /**
     *@描述 解析ack包含义,入库数据包状态
     *     解析ack包，调用upStatusByAckService.updateCurState
     *@参数  [map]
     *@返回值  java.util.concurrent.ConcurrentHashMap<java.lang.String,java.lang.Object>
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/3/11
     *@修改人和其它信息
     */
    public void parseAckCatch(ConcurrentHashMap<String, Object> map) throws Exception {

        ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
        String ackId = (String) map.get("PACKAGE_ID");// 监听ack包名
//        String nodeId = (String)map.get("NODE_ID");// 当前FTP

        String[] ackIds = ackId.split(CommonConstants.NAME.ACK_FIX);

        if (ackIds.length < 10) {
           throw new BusinessErrorException("ack包格式不正确，无法解析ackId：{}", ackId);
        }

        String nodeId = ackIds[1];//
        String toNodeId = ackIds[2];// 目标节点
        String sendStateDm = ackIds[3];// 流转状态
        String operateStateDm = ackIds[4];// 操作状态
        String packageId = ackIds[5];
        String createTime = ackIds[6];// 创建时间
        String updateTime = ackIds[7];// 修改时间
        String processId = ackIds[8];// 进程id
        String dateSN = ackIds[9];// 更新日期

        //            date = DateUtils.dateParse(updateTime,DateUtils.DATE_PATTERN_D);
        Date createDate = new Date( Long.valueOf(createTime));
        Date updateDate = new Date( Long.valueOf(updateTime));

        if ("99".equals(sendStateDm)) {
            sendStateDm = "";
        }

        if ("99".equals(operateStateDm)) {
            operateStateDm = "";
        }
        map.put("ACK_FLAG", "TRUE");// ack包修改状态标记
        map.put("ACK_ID", ackId);// ack包ID
        map.put("PACKAGE_ID", packageId);
        map.put("NODE_ID", nodeId);
        map.put("TO_NODE_ID", toNodeId);
        map.put("SEND_STATE_DM", sendStateDm);
        map.put("OPERATE_STATE_DM", operateStateDm);
        map.put("CREATE_TIME", createDate );// 创建时间
        map.put("UPDATE_TIME", updateDate );// 修改时间
        map.put("PROCESS_ID", processId );// 进程节点

        // 根据ack带回状态修改
        upStatusService.updateAckState(map);

        // 如果是非起始节点，则需要一直上传
//        createUpAck(map);

        // 解析之后移走
    }

    public void moveAck(FTPClient ftpClient, String ackId, String ftpNodeId) {

        ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
        try {
            FtpClientTemplate ftpClientTemplate = FtpClientTemplate.FTP_CLIENT_TEMPLATE.get(ftpNodeId);
            FTPConfig config = FtpClientTemplate.FTP_CONFIG.get(ftpNodeId);

            // ack备份目录
            String ackBack = environment.getProperty("ack.back.path") + "/" + FileConfigUtil.CURNODEID + "/";

            // ack监听目录
            String upDir = config.getAckPackageFolderDown();

            // ack解析后放到别的文件夹下
            String ackMoveDir = ackBack + DateUtils.getTodaySN() ;

            // 移动ack包路径
            ftpClientTemplate.moveFile(ftpClient, upDir + ackId, ackMoveDir, ackId);

        } catch (Exception e) {
            logger.error("ACK包转移失败！{}",e);
        }

    }

    /**
     *@描述 移除ACK
     *@参数  [ackId]
     *@参数  [ftpNodeId]
     *@返回值  java.util.concurrent.ConcurrentHashMap<java.lang.String,java.lang.Object>
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/3/21
     *@修改人和其它信息
     */
    public void moveAck(String ackId, String ftpNodeId) {

        ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
        try {
            FtpClientTemplate ftpClientTemplate = FtpClientTemplate.FTP_CLIENT_TEMPLATE.get(ftpNodeId);
            FTPConfig config = FtpClientTemplate.FTP_CONFIG.get(ftpNodeId);

            // ack备份目录
            String ackBack = environment.getProperty("ack.back.path") + "/" + FileConfigUtil.CURNODEID + "/";

            // ack监听目录
            String upDir = config.getAckPackageFolderDown();

            // ack解析后放到别的文件夹下
            String ackMoveDir = ackBack + DateUtils.getTodaySN() ;

            // 移动ack包路径
            ftpClientTemplate.moveFile(upDir + ackId, ackMoveDir, ackId);

        } catch (Exception e) {
            logger.error("ACK包转移失败！{}",e);
        }

    }


}
