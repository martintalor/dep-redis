package com.iflytek.dep.server.service.dataPack;

import com.iflytek.dep.common.utils.DateUtils;
import com.iflytek.dep.server.ftp.core.FtpClientTemplate;
import com.iflytek.dep.server.mapper.NodeAppBeanMapper;
import com.iflytek.dep.server.mapper.NodeLinkBeanMapper;
import com.iflytek.dep.server.model.FTPConfig;
import com.iflytek.dep.server.model.NodeAppBean;
import com.iflytek.dep.server.model.NodeLinkBean;
import com.iflytek.dep.server.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
public class SendAckService {

    private static Logger logger = LoggerFactory.getLogger(SendAckService.class);


    @Autowired
    Environment environment;

    @Autowired
    NodeLinkBeanMapper nodeLinkBeanMapper;

    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;


    /**
     *@描述 生成ack
     * 如果ack在当前区域内则不生成ack
     *@参数  [map]
     *@返回值  void
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/3/8
     *@修改人和其它信息
     */
    //@Async("ackAsyncServiceExecutor")
    public void createUpAck(ConcurrentHashMap<String, Object> map) {

        String packageId = "";// 数据包名
        try {
            packageId = (String) map.get("PACKAGE_ID");// 数据包名

            logger.debug(Thread.currentThread().getName() + " ACK包生成、上传开始！pkgId...{}",packageId);

            // 生成上传ack
            createUpAckCatch(map);

        } catch (Exception e) {
            logger.error("ACK包生成、上传失败！ackId...{},ERROR{}",packageId,e);
        }

    }

    /**
     *@描述 生成ack，3次重试
     *@参数  [map]
     *@返回值  void
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/4/15
     *@修改人和其它信息
     */
    //@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000l, multiplier = 2))
    public void createUpAckCatch(ConcurrentHashMap<String, Object> map) throws Exception {
        // ACK_fromId_toId_packageId
        // fromId = 当前NODE_ID


        String ackName = "";
        try {
            String packageId = (String) map.get("PACKAGE_ID");
            String nodeId = (String) map.get("NODE_ID");// 待更新状态的节点
            String sendStateDm = (String) map.get("SEND_STATE_DM");// 流转状态
            String operateStateDm = (String) map.get("OPERATE_STATE_DM");// 操作状态
            String toNodeId = (String) map.get("TO_NODE_ID");// 目标节点
            Date createTime = (Date) map.get("CREATE_TIME");// 创建时间
            Date updateTime = (Date) map.get("UPDATE_TIME");// 修改时间
            String processId = (String) map.get("PROCESS_ID");// 进程id
            String curNodeId = FileConfigUtil.CURNODEID;// 当前节点

            //解决当前节点为空的bug如果为空就直接设置为当前节点
            if (StringUtils.isEmpty(nodeId) || "null".equals(nodeId)) {
                nodeId = FileConfigUtil.CURNODEID;
            }

            String appIdFrom = PackUtil.splitAppFrom(packageId);// 源节点
            NodeAppBean nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(appIdFrom);

            //  1.如果是源节点并且，不生成ACk
            if ( curNodeId.equals( nodeAppBean.getNodeId() )  ) {
                logger.debug("源节点，不生成上传ACK！！nodeId：" + nodeId);
                return;
            }

            // 防止两条下划线之间有空数据
            if ( StringUtil.isEmpty(sendStateDm) ) {
                sendStateDm = "99";
            }

            if ( StringUtil.isEmpty(operateStateDm) ) {
                operateStateDm = "99";
            }

            Date curDate = new Date();// 当前时间
            createTime = createTime == null ? curDate : createTime;
            updateTime = updateTime == null ? curDate : updateTime;

            // 2、创建ackName
            String ackFix = CommonConstants.NAME.ACK_FIX;
            ackName = "ACK" + ackFix + nodeId
                    + ackFix + toNodeId
                    + ackFix + sendStateDm
                    + ackFix + operateStateDm
                    + ackFix + packageId
                    + ackFix + createTime.getTime()
                    + ackFix + updateTime.getTime()
                    + ackFix + processId
                    + ackFix + DateUtils.getTodaySN();// 年月日
    //                + ackFix + "0";// 返回通道

    //        logger.info( ackName );


            // 3、获取ftp
            // 反向发送,根据packageId、toNodeId，将nodeId作为rightNodeId，反向查询要上传的FTP
            String mainPackageId = packageId.split("\\.")[0] + CommonConstants.NAME.ZIP;
            NodeLinkBean linkBean = new NodeLinkBean();
            linkBean.setToNodeId(toNodeId);
            linkBean.setPackageId(mainPackageId);
            linkBean.setRightNodeId(curNodeId);
            linkBean = nodeLinkBeanMapper.getLinkByRightNode(linkBean);
            if (linkBean == null) {
                logger.error("ack没找到返回链路！ackId:" + ackName);
                return;
            }
            String nextNodeId = linkBean.getLeftNodeId();

            // 根据nodeId查找FtpClientTemplate
            FtpClientTemplate ftpClientTemplate = FtpClientTemplate.FTP_CLIENT_TEMPLATE.get(nextNodeId);
            FTPConfig config = FtpClientTemplate.FTP_CONFIG.get(nextNodeId);
            if ( !ftpClientTemplate.getFtpClientConfig().getHost().equals(config.getFtpIp()) ) {
                logger.error("FTP连接有误！");
                return;
            }
            // 单通网闸不做ack返回
            String netBrakeType = config.getNetBrakeType();
            if (!StringUtils.isEmpty(netBrakeType) && netBrakeType.equals(CommonConstants.NET_BRAKE_TYPE.SINGLE_NET_BRAKE)) {
                return;
            }
            // 4、创建ack包并得到待上传文件夹
            // FTP的上传路径 叶子节点："/up/ack/"；中心节点“/down/ack/”;
    //        String upDir = config.getAckPackageFolderUp() + DateUtils.getTodaySN() ;
            // FTP只能监听根目录，所以不能存在日期文件夹下
            String upDir = config.getAckPackageFolderUp()  ;
            String upPath = upDir + "/" + ackName ;

            // ack包存放路径
            String localDir =  FileConfigUtil.ACKDIR + File.separatorChar + DateUtils.getTodaySN() ;
            String localPath = localDir +  File.separatorChar + ackName;

            // 创建本地ACK文件夹
            FileUtil.mkdirs(localDir);
            // 创建本地ACK文件
            Boolean isFile = FileUtil.touchFile(localPath);

            if (!isFile) {
                logger.error("ACK包生成失败，localPath：" + localPath);
                return;
            }

            // 屏蔽调ack上传信息 by yaowei 20190404
            // 连接FTP上传文件到
    //        logger.info("ACK包上传开始！上传文件：" + localPath);

            // 5、上传文件
            // 创建FTP远程的上传文件夹
    //        ftpClientTemplate.makeDirectory(upDir);
            boolean bool = ftpClientTemplate.uploadFileReset(localPath,ackName, upDir);
    //        boolean bool = ftpClientTemplate.uploadFile( new File(localPath), upPath);

            // 6、提示上传成功、失败
            if ( bool ) {
                logger.debug("ACK包上传成功！upDir{},ackName:{}" , upDir, ackName);
            } else {
                logger.error("ACK包上传失败！本地文件路径：" + localPath);
            }
        } catch (Exception e) {
            logger.error("ack上传失败，ackName：{}，error：{}",ackName,e);
            throw new Exception(e);
        }

    }



}
