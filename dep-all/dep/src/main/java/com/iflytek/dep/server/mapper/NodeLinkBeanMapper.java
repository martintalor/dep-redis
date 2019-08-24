package com.iflytek.dep.server.mapper;

import com.iflytek.dep.server.model.NodeLinkBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeLinkBeanMapper {
    int deleteByPrimaryKey(String linkId);

    int insert(NodeLinkBean record);

    NodeLinkBean selectByPrimaryKey(String linkId);

    List<NodeLinkBean> selectAll();

    int updateByPrimaryKey(NodeLinkBean record);


    /**
     * 根据packageId+toNodeId生成多个link
     * @param linkBeanList 一个packageId+toNodeId，等于多个link
     * @return
     */
    int insertList(List<NodeLinkBean> linkBeanList);

    /**
     * 获取头部link
     * @param linkBean PACKAGE_ID TO_NODE_ID
     * @return
     */
    NodeLinkBean getLinkHead(NodeLinkBean linkBean);


    /**
     * 根据packageId、当前节点id获取下一条链路
     * @param linkBean  PACKAGE_ID、LEFT_NODE_ID
     * @return
     */
    NodeLinkBean getLinkByCurNode(NodeLinkBean linkBean);

    /**
     * 根据packageId、当前节点id获取下一条链路
     * @param linkBean  PACKAGE_ID、LEFT_NODE_ID
     * @return
     */
    NodeLinkBean getLinkByRightNode(NodeLinkBean linkBean);

    NodeLinkBean getLinkByAppId(@Param("packageId")String packageId,
                          @Param("leftNodeId") String leftNodeId,
                          @Param("appId") String appId);

    NodeLinkBean getToNodeLink(@Param("packageId")String packageId,
                          @Param("leftNodeId") String leftNodeId,
                          @Param("rightNodeId") String appId);
}