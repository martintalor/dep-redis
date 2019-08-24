package com.iflytek.dep.server.service.dataPack;

import com.iflytek.dep.common.exception.BusinessErrorException;
import com.iflytek.dep.common.utils.UUIDGenerator;
import com.iflytek.dep.server.constants.GlobalState;
import com.iflytek.dep.server.constants.RecvSendStateEnum;
import com.iflytek.dep.server.mapper.*;
import com.iflytek.dep.server.model.*;
import com.iflytek.dep.server.utils.CommonConstants;
import com.iflytek.dep.server.utils.ConcurrentCacheSync;
import com.iflytek.dep.server.utils.PackUtil;
import com.iflytek.dep.server.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Package com.iflytek.dep.server.service.dataPack
 * @Description: 数据包状态更新
 * @date 2019/2/22--17:26
 */
@Service
public class UpStatusService {

    private static Logger logger = LoggerFactory.getLogger(UpStatusService.class);


    @Autowired
    SendPackMapper mapper;

    @Autowired
    NodeAppBeanMapper nodeAppBeanMapper;

    @Autowired
    NodeRouteBeanMapper nodeRouteBeanMapper;

    @Autowired
    NodeLinkBeanMapper nodeLinkBeanMapper;

    @Autowired
    DataPackBeanMapper dataPackBeanMapper;

    @Autowired
    PackageCurrentStateBeanMapper pcsBeanMapper;

    @Autowired
    DataNodeProcessBeanMapper dataNodeProcessBeanMapper;

    @Autowired
    NodeOperateStateBeanMapper operateStateBeanMapper;

    @Autowired
    NodeSendStateBeanMapper sendStateBeanMapper;

    @Autowired
    DataPackSubBeanMapper dataPackSubBeanMapper;

    /* 全局状态表**/
    @Autowired
    PackageGlobalStateBeanMapper globalStateBeanMapper;

    @Autowired
    SendAckService sendAckService;


    /**
     * @描述 根据packageId、curNodeId创建链路
     * @参数 [packageId, curNodeId]
     * @返回值 void
     * @创建人 姚伟-weiyao2
     * @创建时间 2019/3/14
     * @修改人和其它信息
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.DEFAULT)
    public void createNodeLinks(String packageId, String curNodeId) throws BusinessErrorException {

        String[] appIdTos = PackUtil.splitAppTo(packageId);// appIdTo,appIdTo,appIdTo...
        String appIdFrom = PackUtil.splitAppFrom(packageId);// 源节点

        // 遍历，创建nodeLink
        for (String appIdTo : appIdTos) {
            ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<String, Object>();
            paramMap.put("PACKAGE_ID", packageId);// 数据包ID
            paramMap.put("CUR_NODE_ID", curNodeId);// 当前节点
            paramMap.put("APP_ID_FROM", appIdFrom);// 发送APP
            paramMap.put("APP_ID_TO", appIdTo);// 接收APP

            // 创建nodeLink
            createNodeLink(paramMap);
        }
    }

    /**
     * @描述 根据from、to的appid生成nodelink,实时读取
     * @参数 [ConcurrentHashMap] packageId、appIdFrom、appIdTo
     * @返回值 java.lang.String linkId
     * @创建人 姚伟-weiyao2
     * @创建时间 2019/2/27
     * @修改人和其它信息
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public ConcurrentHashMap<String, Object> createNodeLink(ConcurrentHashMap<String, Object> map) throws BusinessErrorException {

        String packageId = (String) map.get("PACKAGE_ID");
        String appIdFrom = (String) map.get("APP_ID_FROM");
        String appIdTo = (String) map.get("APP_ID_TO");// 单个app_id_to
        // 当前节点，打包节点根据app_id_from来，后续节点通过网闸摆渡后的FTP监控获取
        String curNodeId = (String) map.get("CUR_NODE_ID");
        // 根据appIdTo 获取对应node节点
        NodeAppBean toApp = nodeAppBeanMapper.selectByPrimaryKey(appIdTo);
        String toNodeId = toApp.getNodeId();// 目标节点id

        // 0、如果package数据包已生成，则返回第一个链路节点
        ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<>();

        // ********* 重点>>>>> 永远只取主包 <<<<<<<*************
        packageId = packageId.split("\\.")[0] + CommonConstants.NAME.ZIP;

        // 输入参数，packageId、appIdTo、curNodeId，查找当前节点链路是否已经存在，已存在，则返回当前环节链接
        NodeLinkBean nodeLinkBean = new NodeLinkBean();
        nodeLinkBean.setPackageId(packageId);
        nodeLinkBean.setToNodeId(toNodeId);
        nodeLinkBean = nodeLinkBeanMapper.getLinkHead(nodeLinkBean);
        if (nodeLinkBean != null) {
            resultMap.put("NODE_LINK_BEAN", nodeLinkBean);
            resultMap.put("LINK_ID", nodeLinkBean.getLinkId());
            return resultMap;
        }

        // 1、读取所有物理路由，并放入map，以便后续试用
        List<NodeRouteBean> nodeRouteBeanList = nodeRouteBeanMapper.selectAll();
        ConcurrentHashMap<String, NodeRouteBean> routeMap = new ConcurrentHashMap<>();
//        Map<String,NodeRouteBean> routeMap = new HashedMap();
        for (NodeRouteBean nodeRouteBean : nodeRouteBeanList) {
            String leftNodeId = nodeRouteBean.getLeftNodeId();
            String rightNodeId = nodeRouteBean.getRightNodeId();
            String leftServerNode = nodeRouteBean.getLeftServerNode();
            String rightServerNode = nodeRouteBean.getRightServerNode();
            String routeType = nodeRouteBean.getRouteType();

            routeMap.put("LEFT_NODE_ID_" + leftNodeId, nodeRouteBean);
            routeMap.put("RIGHT_NODE_ID_" + rightNodeId, nodeRouteBean);

            // 添加routeType 20190429 weiyao2
            routeMap.put("LEFT_NODE_ID_" + leftNodeId + "_" + routeType, nodeRouteBean);
            routeMap.put("RIGHT_NODE_ID_" + rightNodeId + "_" + routeType, nodeRouteBean);


            // 左右物理节点作为key
            routeMap.put(leftNodeId + "_" + rightNodeId, nodeRouteBean);

            // 左右serverNode作为key
            routeMap.put(leftServerNode + "_" + rightServerNode, nodeRouteBean);
        }

        // 2、读取appid的from、to对应的nodeid
        NodeAppBean fromApp = nodeAppBeanMapper.selectByPrimaryKey(appIdFrom);

        String fromNodeId = fromApp.getNodeId();// 发送节点nodeId

        // 3、生成节点链路
        List<NodeLinkBean> linkList = new CopyOnWriteArrayList<NodeLinkBean>();
        short count = 0;
        try {

            // 如果中心节点为开始节点、左节点，则取出来的数据为null
            String nodeFlag = "LEFT_NODE_ID_";

            if (routeMap.get("LEFT_NODE_ID_" + fromNodeId) == null) {
                nodeFlag = "RIGHT_NODE_ID_";
                NodeRouteBean toNodeRouteBean = routeMap.get("LEFT_NODE_ID_" + toNodeId);
                String routeType = "";
                if (toNodeRouteBean != null) {
                    routeType = toNodeRouteBean.getRouteType();
                }

                count = createLinkTheCenter(fromNodeId,
                        packageId,
                        routeMap,
                        toNodeId,
                        linkList,
                        nodeFlag,
                        count,
                        routeType
                );

            } else {

                count = createLink(fromNodeId,
                        packageId,
                        routeMap,
                        toNodeId,
                        linkList,
                        nodeFlag,
                        count
                );
            }

        } catch (Exception e) {
            logger.error(e.getMessage(),"节点链路生成失败,packageId:" + packageId);
//            throw new BusinessErrorException("节点链路生成失败");
        }

        if (linkList == null) {
            logger.error("节点链路生成失败,packageId:" + packageId);
//            throw new BusinessErrorException("节点链路生成失败");
        }

        System.out.println(linkList);

        // 查看生成的SQL
//        String sql = " insert into NODE_LINK (LINK_ID, PACKAGE_ID, TO_NODE_ID,\n" +
//                "          LEFT_NODE_ID, RIGHT_NODE_ID,ORDER_ID) ";
//        int len = 0;
//        for (NodeLinkBean linkBean : linkList) {
//            sql += " ( select '" +
//                    linkBean.getLinkId() + "','" +
//                    linkBean.getPackageId() + "','" +
//                    linkBean.getToNodeId() + "','" +
//                    linkBean.getLeftNodeId() + "','" +
//                    linkBean.getRightNodeId() + "','" +
//                    linkBean.getOrderId() + "' from dual) " ;
//
//            len ++;
//            if ( len < linkList.size()) {
//                sql += " union all ";
//            }
//
//        }
//        System.out.println( sql);

        // 4、持久化节点链路信息
        nodeLinkBeanMapper.insertList(linkList);

        // 5、返回链路id及node
        nodeLinkBean = new NodeLinkBean();
        nodeLinkBean.setPackageId(packageId);
        nodeLinkBean.setToNodeId(appIdTo);
        nodeLinkBean = nodeLinkBeanMapper.getLinkHead(nodeLinkBean);
        if (nodeLinkBean != null) {
            resultMap.put("NODE_LINK_BEAN", nodeLinkBean);
            resultMap.put("LINK_ID", nodeLinkBean.getLinkId());
        }
        return resultMap;
    }

    /**
     * @param leftNodeId 本次环节的左节点id
     * @param packageId  数据包id
     * @param nodeRoute  节点路由
     * @param toNodeId   目标节点
     * @param linkList   链路对象list
     * @param count      递归计数器，递归超过30次直接终止，没有那么长的链条，防止溢出
     * @描述 递归方法，根据左节点寻找下一节点，直到下一节点的serverNodeId不一致为止
     * 左右节点serverNodeId不一致后，可以判断右节点为中心节点，找到第一个中心节点后，左右调转
     * 此时需要找到右逻辑节点（serverNodeId）是中心节点，左逻辑节点（serverNodeId）是目标节点
     * 获取到node_id后，左右调转，直到right_node_id是目标节点
     * @参数 [leftNodeId, nodeRoute, toNodeId, linkList, count]
     * @返回值 java.lang.String
     * @创建人 姚伟-weiyao2
     * @创建时间 2019/2/26
     * @修改人和其它信息
     */
    private short createLink(String leftNodeId,
                             String packageId,
                             ConcurrentHashMap<String, NodeRouteBean> nodeRoute,
                             String toNodeId,
                             List<NodeLinkBean> linkList,
                             String nodeFlag,
                             short count) throws Exception {

        short maxCount = 30; // 最大步数

        NodeLinkBean nodeLinkBean = new NodeLinkBean();

        // 根据左节点获取当前路由对象
        NodeRouteBean nodeRouteBean = nodeRoute.get(nodeFlag + leftNodeId);

        if (null == nodeRouteBean) {
            logger.error("节点链路生成失败,packageId:",packageId);
            new BusinessErrorException("节点链路生成失败");
        }

        // 根据左节点获取右节点
        String rightNodeId = nodeRouteBean.getRightNodeId();

        // 如果nodeFlag是RIGHT_NODE_ID_，说明此时的leftNodeId在路由表里右节点,
        // 所以倒过来，左节点作为右节点，生成连接
        if ("RIGHT_NODE_ID_".equals(nodeFlag)) {
            rightNodeId = nodeRouteBean.getLeftNodeId();
        }

        // 生成一节链路
        nodeLinkBean = getNewLinkBean(packageId,
                toNodeId,
                leftNodeId,
                rightNodeId,
                ++count
        );
        linkList.add(nodeLinkBean);

        // 如果左节点与目标节点不一样，则此时用目标逻辑节点+当前逻辑节点(中心节点)为key取出
        // G01-G02、G02-Z01、Z01-ZSE01、J01-J02、J02-Z02、Z02-ZSE01
        // G02-Z02
        NodeRouteBean toNodeRouteBean = nodeRoute.get("LEFT_NODE_ID_" + toNodeId);

        // 到第一个中心节点前，都是从左向右找，只要经过一个中心，就开始从右向左
        // 比较本次获取的左右逻辑节点，不相同则是到了中心节点
        // 到了中心节点，开始从右往左找，并且多生成2次,Z01-ZSE01、ZSE01-J01
        String leftServNode = nodeRouteBean.getLeftServerNode();
        String rightServNode = nodeRouteBean.getRightServerNode();

//        System.out.println(linkList.size() + linkList.toString());
        if (!(leftServNode.equals(rightServNode)) && "LEFT_NODE_ID_".equals(nodeFlag)) {

            // 此时为第一次找到不同serv节点，但是目标节点就是下个right，所以直接return
            if (toNodeRouteBean == null ) {
                nodeLinkBean = getNewLinkBean(packageId,
                        toNodeId,
                        rightNodeId,
                        toNodeId,
                        ++count
                );
                linkList.add(nodeLinkBean);
                return count;
            }

            // 目标逻辑节点+当前逻辑节点(中心节点)为key取出 J02_Z02
            NodeRouteBean targetRouteBean = nodeRoute.get(toNodeRouteBean.getRightServerNode() + "_" + rightServNode);

            // 跟目标节点对接的中心前置机，如果从G-J，则是 Z02，根据Z02获取ZSE01
            String centerFtpId = targetRouteBean.getRightNodeId();// Z02
            NodeRouteBean routeBean = nodeRoute.get(nodeFlag + centerFtpId);
            String centerDocId = routeBean.getRightNodeId();// ZSE01

            // 需要新new一个bean
            // 保存Z01-ZSE01
            nodeLinkBean = getNewLinkBean(packageId,
                    toNodeId,
                    rightNodeId,
                    centerDocId,
                    ++count
            );
            linkList.add(nodeLinkBean);

            // ZSE01-Z02
            nodeLinkBean = getNewLinkBean(packageId,
                    toNodeId,
                    centerDocId,
                    centerFtpId,
                    ++count
            );
            linkList.add(nodeLinkBean);

            // 此时关系倒转，按照RIGHT_NODE_ID_+右节点作为左节点取数，左节点作为右节点
            // 以右节点作为左节点开始寻找下一次关联关系
            rightNodeId = centerFtpId;
            nodeFlag = "RIGHT_NODE_ID_";
        }

        // 防止数据错误，溢出，强制在大于30时退出，没有那么长的链条
        if (count >= maxCount) {
            return count;
        }

        // 如果目标节点不是右节点，则继续递归，否则不再递归
        if (!toNodeId.endsWith(rightNodeId)) {
            // 将右节点作为左节点寻找下一节点
            count = createLink(rightNodeId, packageId, nodeRoute, toNodeId, linkList, nodeFlag, count);
        }

        return count;
    }

    // 将node_route表中的right作为nodelink中的leftNode
    private short createLinkTheCenter(String leftNodeId,
                             String packageId,
                             ConcurrentHashMap<String, NodeRouteBean> nodeRoute,
                             String toNodeId,
                             List<NodeLinkBean> linkList,
                             String nodeFlag,
                             short count,
                             String toNodeRouteType) throws Exception {

        short maxCount = 30; // 最大步数

        NodeLinkBean nodeLinkBean = new NodeLinkBean();

        // 根据nodeFlag+nodeId+获取当前路由对象
        NodeRouteBean nodeRouteBean = nodeRoute.get(nodeFlag  + leftNodeId + "_" + toNodeRouteType);

        if (null == nodeRouteBean) {
            logger.error("节点链路生成失败,packageId:",packageId);
            new BusinessErrorException("节点链路生成失败");
        }

        // 根据左节点获取右节点
        String rightNodeId = nodeRouteBean.getRightNodeId();

        // 如果nodeFlag是RIGHT_NODE_ID_，说明此时的leftNodeId在路由表里右节点,
        // 所以倒过来，左节点作为右节点，生成连接
        if ("RIGHT_NODE_ID_".equals(nodeFlag)) {
            rightNodeId = nodeRouteBean.getLeftNodeId();
        }

        // 生成一节链路
        nodeLinkBean = getNewLinkBean(packageId,
                toNodeId,
                leftNodeId,
                rightNodeId,
                ++count
        );
        linkList.add(nodeLinkBean);


        // 防止数据错误，溢出，强制在大于30时退出，没有那么长的链条
        if (count >= maxCount) {
            return count;
        }

        // 如果目标节点不是右节点，则继续递归，否则不再递归
        if (!toNodeId.endsWith(rightNodeId)) {
            // 将右节点作为左节点寻找下一节点
            count = createLink(rightNodeId, packageId, nodeRoute, toNodeId, linkList, nodeFlag, count);
        }

        return count;
    }

    /**
     * @描述 根据传入参数，new一个新的NodeLinkBean返回
     * @参数 [packageId, toNodeId, leftNodeId, RightNodeId, orderId]
     * @返回值 com.iflytek.dep.server.model.NodeLinkBean
     * @创建人 姚伟-weiyao2
     * @创建时间 2019/3/1
     * @修改人和其它信息
     */
    private NodeLinkBean getNewLinkBean(String packageId, String toNodeId,
                                        String leftNodeId, String RightNodeId,
                                        short orderId) {
        NodeLinkBean nodeLinkBean = new NodeLinkBean();
        nodeLinkBean.setLinkId(UUIDGenerator.createUUID()); // 主键
        nodeLinkBean.setPackageId(packageId); // 数据包id
        nodeLinkBean.setToNodeId(toNodeId);// 目标节点id
        nodeLinkBean.setLeftNodeId(leftNodeId);// 当前节点id
        nodeLinkBean.setRightNodeId(RightNodeId);// 右节点id
        nodeLinkBean.setOrderId(orderId);// 计数器
        return nodeLinkBean;
    }


    /**
     * @描述 每到一个新节点，创建一个新进程
     * @参数 [map]
     * @返回值 com.iflytek.dep.server.model.DataNodeProcessBean
     * @创建人 姚伟-weiyao2
     * @创建时间 2019/3/4
     * @修改人和其它信息
     */
    // REQUIRED ：如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务。
    // DEFAULT ：这是默认值，表示使用底层数据库的默认隔离级别。对大部分数据库而言，通常这值就是： READ_COMMITTED 。
    // READ_COMMITTED ：该隔离级别表示一个事务只能读取另一个事务已经提交的数据。该级别可以防止脏读，这也是大多数情况下的推荐值。
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.DEFAULT)
    public DataNodeProcessBean createPorcess(ConcurrentHashMap<String, Object> map) {

        // update by weiyao2 20190423 将createPorcess方法改成synchronized

        String nodeId = (String) map.get("NODE_ID");// 物理节点id
        String packageId = (String) map.get("PACKAGE_ID");// 数据包id
        String toNodeId = (String) map.get("TO_NODE_ID");// 目标id
        String proKey = "createProcess_" + packageId + "_" + "_" + nodeId + "_" + toNodeId;
        String processId = UUIDGenerator.createUUID();
        DataNodeProcessBean dataNodeProcessBean = null;

//        DataNodeProcessBean dataNodeProcessBean = dataNodeProcessBeanMapper.selectByPrimaryKey(processId);

        // 来源node
        String fromAppId = PackUtil.splitAppFrom(packageId);
        NodeAppBean nodeAppBean = nodeAppBeanMapper.selectByPrimaryKey(fromAppId);
        String fromNodeId = "";
        if (nodeAppBean != null) {
            fromNodeId = nodeAppBean.getNodeId();
        }

        dataNodeProcessBean = dataNodeProcessBeanMapper.selectByUnique(packageId,nodeId,toNodeId);

        if (null == dataNodeProcessBean) {
            dataNodeProcessBean = new DataNodeProcessBean();
            dataNodeProcessBean.setNodeId(nodeId);
            dataNodeProcessBean.setPackageId(packageId);
            dataNodeProcessBean.setToNodeId(toNodeId);
            dataNodeProcessBean.setFromNodeId(fromNodeId);
            dataNodeProcessBean.setProcessId(processId);
            dataNodeProcessBean.setCreateTime(new Date());

            try {
                dataNodeProcessBeanMapper.insert(dataNodeProcessBean);
            } catch (Exception e) {
                logger.error("本次插入dataNodeProcessBean失败proKey：{}",proKey );
                throw e;
//                dataNodeProcessBean = dataNodeProcessBeanMapper.selectByPrimaryKey(processId);
            }
        }

        // 返回，用以跟新数据包状态
        return dataNodeProcessBean;
    }

    /**
     * @描述 同步更新流转状态流水
     * @参数 [map] SEND_STATE_DM、PROCESS_ID
     * @返回值 java.util.concurrent.ConcurrentHashMap
     * @创建人 姚伟-weiyao2
     * @创建时间 2019/2/27
     * @修改人和其它信息
     */

    private void updateSendState(ConcurrentHashMap<String, Object> map) {

        // 状态有则更新，无则不更新
        String sendStateDm = (String) map.get("SEND_STATE_DM");// 流转状态
        String processId = (String) map.get("PROCESS_ID");// 当前链路进程id
        Date createTime = (Date) map.get("CREATE_TIME");// 创建时间
        Date updateTime = (Date) map.get("UPDATE_TIME");// 更新时间

        if (StringUtil.isEmpty(sendStateDm)) {
//            logger.debug("未传入流转状态，不更新！");
            return;
        }

        // 1、先取最大的orderId，再添加新的状态
        NodeSendStateBean nodeSendStateBean = sendStateBeanMapper.getMaxOrderId(processId);

        short orderId = 0;
        if (nodeSendStateBean != null) {
            orderId = nodeSendStateBean.getOrderId();
           createTime = nodeSendStateBean.getCreateTime();
        }
        orderId = (short) (orderId + 1);

        Date curDate = new Date();
        createTime = createTime == null ? curDate : createTime;
        updateTime = updateTime == null ? curDate : updateTime;

        // 2、保存
        NodeSendStateBean sendStateBean = new NodeSendStateBean();
        sendStateBean.setOrderId(orderId);
        sendStateBean.setUuid(UUIDGenerator.createUUID());
        sendStateBean.setSendStateDm(sendStateDm);
        sendStateBean.setProcessId(processId);
        sendStateBean.setCreateTime(createTime);
        sendStateBean.setUpdateTime(updateTime);

        // 保存新的数据包流转状态
        sendStateBeanMapper.insert(sendStateBean);
    }


    /**
     * @描述 同步更新操作状态流水
     * @参数 [map] OPERATE_STATE_DM、PROCESS_ID
     * @返回值 java.util.concurrent.ConcurrentHashMap
     * @创建人 姚伟-weiyao2
     * @创建时间 2019/2/27
     * @修改人和其它信息
     */

    private void updateOperateState(ConcurrentHashMap<String, Object> map) {

        // 返回参数值

        // 状态有则更新，无则不更新
        String operateStateDm = (String) map.get("OPERATE_STATE_DM");// 操作状态
//        String sendStateDm = map.get("SEND_STATE_DM");// 流转状态
        String processId = (String) map.get("PROCESS_ID");// 当前链路进程id
        Date createTime = (Date) map.get("CREATE_TIME");// 创建时间
        Date updateTime = (Date) map.get("UPDATE_TIME");// 更新时间
        BigDecimal spendTime = (BigDecimal) map.get("SPEND_TIME");//耗时

        if (StringUtil.isEmpty(operateStateDm)) {
//            logger.debug("未传入操作状态，不更新！");
            return;
        }

        // 1、先取最大的orderId，再添加新的状态
        NodeOperateStateBean operateStateBean = operateStateBeanMapper.getMaxOrderId(processId);

        short orderId = 0;
        if (operateStateBean != null) {
            orderId = operateStateBean.getOrderId();
           createTime = operateStateBean.getCreateTime();
        }
        orderId = (short) (orderId + 1);

        Date curDate = new Date();
        createTime = createTime == null ? curDate : createTime;
        updateTime = updateTime == null ? curDate : updateTime;

        // 2、保存
        NodeOperateStateBean newOperateStateBean = new NodeOperateStateBean();
        newOperateStateBean.setOrderId(orderId);
        newOperateStateBean.setUuid(UUIDGenerator.createUUID());
        newOperateStateBean.setOperateStateDm(operateStateDm);
        newOperateStateBean.setProcessId(processId);
        newOperateStateBean.setCreateTime(createTime);
        newOperateStateBean.setUpdateTime(updateTime);
        newOperateStateBean.setSpendTime(spendTime);

        // 保存新的数据包状态
        operateStateBeanMapper.insert(newOperateStateBean);
    }


    /**
     * @描述 根据PACKAGE_ID、TO_NODE_ID、NODE_ID当前最新状态，并插入流水
     * @参数 [map] PACKAGE_ID、NODE_ID、SEND_STATE_DM、OPERATE_STATE_DM
     * @返回值 java.util.concurrent.ConcurrentHashMap<java.lang.String       ,       java.lang.Object>
     * @创建人 姚伟-weiyao2
     * @创建时间 2019/3/2
     * @修改人和其它信息
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor=Exception.class)
    public ConcurrentHashMap<String, Object>  updateCurState(ConcurrentHashMap<String, Object> map) throws BusinessErrorException {
        ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
        String packageId = "";
        String proKey = "";// 唯一key
        try {
             packageId = (String) map.get("PACKAGE_ID"); // 数据包名

              resultMap = updateCurStateCatch(map);

        } catch (Exception e) {
            logger.error("状态更新失败！packageId:{},Exception:{}",packageId, e);
            throw new BusinessErrorException("updateCurState 失败");
        }
        return resultMap;
    }

    public  void updateAckState(ConcurrentHashMap<String, Object> map) {
        ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
        String ackId = "";// 唯一key
        try {

            ackId = (String) map.get("ACK_ID");

            // 全局标志 非空则退出
            if (  ConcurrentCacheSync.dataMap.get(ackId) != null ) {
                return;
            }
//
            ConcurrentCacheSync.dataMap.put(ackId,"true");

            updateCurStateCatch(map);

            // 清除ackId
            ConcurrentCacheSync.dataMap.remove(ackId);
        } catch (Exception e) {
            logger.error("ack状态更新失败！ackId:{},Exception:{}",ackId, e);
        }
    }

    /**
     * @描述 状态更新方法，添加事务隔离Propagation.REQUIRES_NEW、Isolation.DEFAULT
     * update by 20190322 事务级别改成Isolation.READ_UNCOMMITTED，未提交可读取
     * @参数 [map]
     * @返回值 java.util.concurrent.ConcurrentHashMap<java.lang.String       ,       java.lang.Object>
     * @创建人 姚伟-weiyao2
     * @创建时间 2019/3/12
     * @修改人和其它信息
     */
    private ConcurrentHashMap<String, Object>  updateCurStateCatch(ConcurrentHashMap<String, Object> map) throws SQLException {

        ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<>();
        String packageId = (String) map.get("PACKAGE_ID"); // 数据包名
        String nodeId = (String) map.get("NODE_ID");// 当前节点
        String sendStateDm = (String) map.get("SEND_STATE_DM");// 流转状态
        String operateStateDm = (String) map.get("OPERATE_STATE_DM");// 操作状态
        String toNodeId = (String) map.get("TO_NODE_ID");// 当前节点
        Date createTime = (Date) map.get("CREATE_TIME");// 创建时间
        Date updateTime = (Date) map.get("UPDATE_TIME");// 修改时间
//        BigDecimal spendTime = (BigDecimal) map.get("SPEND_TIME");//耗时
//        String processId = (String) map.get("PROCESS_ID");// 进程ID
        String processId = "";// 进程ID

        Date curDate = new Date();// 当前时间
        createTime = createTime == null ? curDate : createTime;
        updateTime = updateTime == null ? curDate : updateTime;


        // 1、先获取数据包的当前状态、当前节点
        PackageCurrentStateBean pcsBean = pcsBeanMapper.selectByPrimaryKey(packageId, nodeId, toNodeId);

        // 找不到当前状态，新增一个DataNodeProcessBean,新增一个PackageCurrentStateBean
        if (null == pcsBean) {

            // 如果传入的processId为空，则生成一个；processId不为空一般为ack返回
//            if ( StringUtil.isEmpty(processId) ) {
            DataNodeProcessBean processBean = createPorcess(map);
            processId = processBean.getProcessId();
//            }

            // 当前状态表只更新成功和异常状态，中间状态不记录
            // 只有状态结尾为0的00、异常，20、接收成功，30、发送成功
//            if (  sendStateDm.endsWith("0") ) {

            pcsBean = new PackageCurrentStateBean();
            pcsBean.setPackageId(packageId);
            pcsBean.setProcessId(processId);
            pcsBean.setToNodeId(toNodeId);
            pcsBean.setSendStateDm(sendStateDm);
            pcsBean.setOperateStateDm(operateStateDm);
            pcsBean.setNodeId(nodeId);
            pcsBean.setCreateTime(createTime);
            pcsBean.setUpdateTime(updateTime);
            pcsBeanMapper.insert(pcsBean);
//            }

        } else {// 已经有此状态

            // 只有状态结尾为0的00、异常，20、接收成功，30、发送成功
            if (StringUtil.isEmpty(sendStateDm) || !sendStateDm.endsWith("0")) {

                sendStateDm = pcsBean.getSendStateDm();
            }

            if (operateStateDm == null || operateStateDm.isEmpty()) {
                operateStateDm = pcsBean.getOperateStateDm();
            }

            // 修改时流转状态为空，不更新当前状态表
//            pcsBean.setCreateTime(createTime); // 创建时间
            pcsBean.setUpdateTime(updateTime); // 更新时间
            pcsBean.setSendStateDm(sendStateDm);// 更新流转状态
            pcsBean.setOperateStateDm(operateStateDm);// 更新操作状态
            pcsBeanMapper.updateByPrimaryKey(pcsBean);

            processId = pcsBean.getProcessId();
        }

        map.put("PROCESS_ID", processId);
        map.put("CREATE_TIME", createTime);
        map.put("UPDATE_TIME", updateTime);

        // 5、更新流转状态
        updateSendState(map);

        // 6、更新操作状态
        updateOperateState(map);

        // 7、更新全局状态
        updateGlobalState(map);

        resultMap.put("PackageCurrentStateBean", pcsBean);
        resultMap.put("PROCESS_ID", processId);

        // 8、创建ack，并上传
        sendAckService.createUpAck(map);

        // 9、等到0.1秒，在更新下一条数据
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            logger.error("线程等待",e);
//        }
        return resultMap;
    }


    /**
     * @描述
     * @参数 [map] PACKAGE_ID、TO_NODE_ID、NODE_ID、SEND_STATE_DM、GLOBAL_STATE_DM、CREATE_TIME、UPDATE_TIME
     * @返回值 void
     * @创建人 姚伟-weiyao2
     * @创建时间 2019/3/13
     * @修改人和其它信息
     */
    private void updateGlobalState(ConcurrentHashMap<String, Object> map) {

//        ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
        String packageId = (String) map.get("PACKAGE_ID"); // 数据包名
        String toNodeId = (String) map.get("TO_NODE_ID");// 当前节点
        String nodeId = (String) map.get("NODE_ID");// 当前节点
        String operateStateDm = (String) map.get("OPERATE_STATE_DM");// 操作状态
        String sendStateDm = (String) map.get("SEND_STATE_DM");// 流转状态
        String globalStateDm = (String) map.get("GLOBAL_STATE_DM");// 全局状态
        Date createTime = (Date) map.get("CREATE_TIME");// 创建时间
        Date updateTime = (Date) map.get("UPDATE_TIME");// 修改时间


//        // 如果流转状态为已接收，说明正在交换中
//        if ( RecvSendStateEnum.RECVED.equals(sendStateDm) ) {
//            globalStateDm = GlobalState.EXCHANGEING.getCode();
//        }

        // 如果操作状态是打包，则是开始交换
        if (CommonConstants.OPERATESTATE.YS.equals(operateStateDm)) {
            globalStateDm = GlobalState.EXCHANGEING.getCode();
        }

        // 如果操作状态是解压，则是结束
        if (CommonConstants.OPERATESTATE.JY.equals(operateStateDm)) {
            globalStateDm = GlobalState.FINISHED.getCode();
        }

        // 如果流转状态是异常，全局状态也要变为异常
        if (RecvSendStateEnum.FAIL.getStateCode().equals(sendStateDm)) {
            globalStateDm = GlobalState.FAIL.getCode();
        }

        // 处理每个分包的状态
        // 1、先查找是否已存在全局状态，有则修改，无则插入（每个分包）
        PackageGlobalStateBean pgsBean = globalStateBeanMapper.selectByPrimaryKey(packageId, toNodeId);

        // 未输入全局状态，无需更新
        if (StringUtil.isEmpty(globalStateDm) && pgsBean == null) {
            globalStateDm = GlobalState.EXCHANGEING.getCode();
//            return;
        }

        if (StringUtil.isEmpty(globalStateDm)) {
            return;
        }

        Date curDate = new Date();// 当前时间
        createTime = createTime == null ? curDate : createTime;
        updateTime = updateTime == null ? curDate : updateTime;

        // 处理每个分包的状态
        // 1、先查找是否已存在全局状态，有则修改，无则插入（每个分包）
//        PackageGlobalStateBean pgsBean = globalStateBeanMapper.selectByPrimaryKey(packageId, toNodeId);

        // 找不到当前状态，新增一个DataNodeProcessBean,新增一个PackageCurrentStateBean
        if (null == pgsBean) {

            pgsBean = new PackageGlobalStateBean();
            pgsBean.setPackageId(packageId);
            pgsBean.setToNodeId(toNodeId);
            pgsBean.setNodeId(nodeId);
            pgsBean.setGlobalStateDm(globalStateDm);
            pgsBean.setCreateTime(createTime);
            pgsBean.setUpdateTime(updateTime);
            globalStateBeanMapper.insert(pgsBean);

        } else {// 已经有此状态

            // 2.1 如果已完成状态，则不再更新更新状态
            String curGloabl = pgsBean.getGlobalStateDm();
            if (GlobalState.FINISHED.getCode().equals(curGloabl)) {
                return;
            }

            // 2.2 更新全局状态
            pgsBean.setUpdateTime(updateTime); // 更新时间
            pgsBean.setGlobalStateDm(globalStateDm);// 更新流转状态
            pgsBean.setNodeId(nodeId);// 更新流转状态
            globalStateBeanMapper.updateByPrimaryKey(pgsBean);
        }

        // 处理整包的状态
        // 1、先查找是否已存在全局状态，有则修改，无则插入（整包）
        String packageName = packageId.split(CommonConstants.NAME.PACKAGE_FIX)[0];
        PackageGlobalStateBean packageBean = globalStateBeanMapper.selectByPrimaryKey(packageName, toNodeId);
        // 找不到当前状态，新增一个DataNodeProcessBean,新增一个PackageCurrentStateBean
        if (null == packageBean) {
            packageBean = new PackageGlobalStateBean();
            packageBean.setPackageId(packageName);
            packageBean.setToNodeId(toNodeId);
            packageBean.setNodeId(nodeId);
            packageBean.setGlobalStateDm(globalStateDm);
            packageBean.setCreateTime(createTime);
            packageBean.setUpdateTime(updateTime);
            globalStateBeanMapper.insert(packageBean);
        } else {// 已经有此状态
            // 2.1 如果已完成状态，则不再更新更新状态
            String curGloabl = packageBean.getGlobalStateDm();
            if (GlobalState.FINISHED.getCode().equals(curGloabl)) {
                return;
            }

            // 2.2 子包有异常，则更新整包异常状态
            packageBean.setUpdateTime(updateTime); // 更新时间
            packageBean.setNodeId(nodeId);// 更新流转状态
            packageBean.setGlobalStateDm(globalStateDm);

            // 如果有已完成状态
            if (GlobalState.FINISHED.getCode().equals(globalStateDm)) {

                // 将所有分包状态改为完成
                globalStateBeanMapper.updateFinishedById(packageName,toNodeId);

            } else {

                // 如果主包不是完成状态，则查看子包是否有异常状态，有的话主包也为异常状态
                int count = globalStateBeanMapper.getFailPackageId(packageName, toNodeId);
                if (count > 0) {
                    packageBean.setGlobalStateDm(GlobalState.FAIL.getCode());// 更新流转状态
                }

            }

            globalStateBeanMapper.updateByPrimaryKey(packageBean);
        }

    }


    /**
     * @描述 insertPageAndPageSub 将数据插入主包表和子包表
     * @参数 [file]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/3/4
     * @修改人和其它信息
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.DEFAULT)
    public void insertPageAndPageSub(File file) {
        //获取需要数据
        String pageId = file.getName();
        DataPackBean page = new DataPackBean();
        DataPackSubBean pageSub = new DataPackSubBean();
        page.setAppIdFrom(PackUtil.splitAppFrom(pageId));
        page.setAppIdTo(PackUtil.splitAppTos(pageId));
        page.setPackagePath(file.getParent());
//        Integer pageSize = new Long(file.length() / 1024l / 1024l).intValue();
//        将数据包大小修改为BigDecimal
        BigDecimal size = new BigDecimal(file.length());
        BigDecimal pic = new BigDecimal(1024);
        BigDecimal pageSize = size.divide(pic).divide(pic, 6, BigDecimal.ROUND_HALF_UP);
        logger.info("数据包:{}大小为:{}",pageId,pageSize);

        //判断是否是主包分别插入数据包表和数据包子表
        if (CommonConstants.NAME.ZIP.equals(pageId.substring(pageId.length() - 4))) {

            // add by yaowei 20190311 如果已经插入过了，不再插入
            DataPackBean dataPackBean = dataPackBeanMapper.selectByPrimaryKey(pageId);
            if (dataPackBean != null) {
                logger.info("主数据包已插入，更新pageId：" + pageId);
                page.setPackageId(pageId);
                page.setPackageSize(pageSize);
                page.setCreateTime(dataPackBean.getCreateTime());
                page.setSendLevel("3");//默认为3
                dataPackBeanMapper.updateByPrimaryKey(page);
                return;
            }

            page.setPackageId(pageId);
            page.setPackageSize(pageSize);
            page.setCreateTime(new Date());
            page.setSendLevel("3");//默认为3
            dataPackBeanMapper.insert(page);
        } else {
            // add by yaowei 20190312 如果已经插入过了，不再插入
            DataPackSubBean packSubBean = dataPackSubBeanMapper.selectByPrimaryKey(pageId);
            if (packSubBean != null) {
                logger.info("该子数据包已插入，更新pageId：" + pageId);
                pageSub.setPackageId(pageId.split("\\.")[0] + CommonConstants.NAME.ZIP);
                pageSub.setSubPackageId(pageId);
                pageSub.setPackageSize(pageSize);
                pageSub.setCreateTime(packSubBean.getCreateTime());
                dataPackSubBeanMapper.updateByPrimaryKey(pageSub);
                return;
            }

            pageSub.setPackageId(pageId.split("\\.")[0] + CommonConstants.NAME.ZIP);
            pageSub.setSubPackageId(pageId);
            pageSub.setPackageSize(pageSize);
            pageSub.setCreateTime(new Date());
            dataPackSubBeanMapper.insert(pageSub);
        }
    }


    /**
     * @描述 insertPageAndPageSubStart
     * @参数 [file, folderPath 打包文件夹路径]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/3/15
     * @修改人和其它信息
     */

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.DEFAULT)
    public void insertPageAndPageSubStart(File file, String folderPath) {
        //获取需要数据
        String pageId = file.getName();
        DataPackBean page = new DataPackBean();
        DataPackSubBean pageSub = new DataPackSubBean();
        page.setAppIdFrom(PackUtil.splitAppFrom(pageId));
        page.setAppIdTo(PackUtil.splitAppTos(pageId));
        page.setPackagePath(file.getParent());
//        Integer pageSize = new Long(file.length() / 1024l / 1024l).intValue();
//        将数据包大小修改为BigDecimal
        BigDecimal size = new BigDecimal(file.length());
        BigDecimal pic = new BigDecimal(1024);
        BigDecimal pageSize = size.divide(pic).divide(pic, 6, BigDecimal.ROUND_HALF_UP);


        //判断是否是主包分别插入数据包表和数据包子表
        if (CommonConstants.NAME.ZIP.equals(pageId.substring(pageId.length() - 4))) {

            // add by yaowei 20190311 如果已经插入过了，不再插入
            DataPackBean dataPackBean = dataPackBeanMapper.selectByPrimaryKey(pageId);
            if (dataPackBean != null) {
                logger.info("主数据包已插入，更新pageId：" + pageId);
                page.setPackageId(pageId);
                page.setPackageSize(pageSize);
                page.setCreateTime(dataPackBean.getCreateTime());
                page.setFolderPath(folderPath);
                page.setSendLevel("3");//默认为3
                dataPackBeanMapper.updateByPrimaryKey(page);
                return;
            }

            page.setPackageId(pageId);
            page.setPackageSize(pageSize);
            page.setCreateTime(new Date());
            page.setFolderPath(folderPath);
            page.setSendLevel("3");//默认为3
            dataPackBeanMapper.insert(page);
        } else {
            // add by yaowei 20190312 如果已经插入过了，不再插入
            DataPackSubBean packSubBean = dataPackSubBeanMapper.selectByPrimaryKey(pageId);
            if (packSubBean != null) {
                logger.info("该子数据包已插入，更新pageId：" + pageId);
                pageSub.setPackageId(pageId.split("\\.")[0] + CommonConstants.NAME.ZIP);
                pageSub.setSubPackageId(pageId);
                pageSub.setPackageSize(pageSize);
                pageSub.setCreateTime(packSubBean.getCreateTime());
                dataPackSubBeanMapper.updateByPrimaryKey(pageSub);
                return;
            }

            pageSub.setPackageId(pageId.split("\\.")[0] + CommonConstants.NAME.ZIP);
            pageSub.setSubPackageId(pageId);
            pageSub.setPackageSize(pageSize);
            pageSub.setCreateTime(new Date());
            dataPackSubBeanMapper.insert(pageSub);
        }
    }


    /**
     * @描述 insertPage
     * @参数 [fileName]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/3/24
     * @修改人和其它信息
     */
    //每个节点初始化的时候往主包表中插入一条数据
//    @Transactional(propagation = Propagation.REQUIRED)
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.DEFAULT)
    public void insertPage(String fileName)  {
        DataPackBean dataPackBean = dataPackBeanMapper.selectByPrimaryKey(fileName + CommonConstants.NAME.ZIP);
        if (dataPackBean == null) {
            DataPackBean page = new DataPackBean();
            page.setSendLevel(fileName.substring(fileName.length()-1));
            page.setCreateTime(new Date());
            page.setAppIdFrom(PackUtil.splitAppFrom(fileName));
            page.setAppIdTo(PackUtil.splitAppTos(fileName));
            page.setPackageId(fileName + CommonConstants.NAME.ZIP);
            page.setPackageSize(new BigDecimal(0));
            dataPackBeanMapper.insert(page);
        }

    }

    /**
     * @描述 insertSubPage
     * @参数 [packageId]
     * @返回值 void
     * @创建人 朱一帆
     * @创建时间 2019/3/25
     * @修改人和其它信息
     */
    //在下载节点的时候插入子包虚拟信息同步信息
//    @Transactional(propagation = Propagation.REQUIRED)
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.DEFAULT)
    public void insertSubPage(String packageId)  {
        if (!packageId.endsWith(CommonConstants.NAME.ZIP)) {
            DataPackSubBean dataPackSubBean = dataPackSubBeanMapper.selectByPrimaryKey(packageId);
            if (dataPackSubBean == null) {
                DataPackSubBean pageSub = new DataPackSubBean();
                pageSub.setPackageId(packageId.split("\\.")[0] + CommonConstants.NAME.ZIP);
                pageSub.setSubPackageId(packageId);
                pageSub.setPackageSize(new BigDecimal(0));
                pageSub.setCreateTime(new Date());
                dataPackSubBeanMapper.insert(pageSub);
            }
        }


    }

}
