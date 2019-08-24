package com.iflytek.dep.server.mapper;

import com.iflytek.dep.server.model.NodeSendStateBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeSendStateBeanMapper {
    int deleteByPrimaryKey(String uuid);

    int insert(NodeSendStateBean record);

    NodeSendStateBean selectByPrimaryKey(String uuid);

    List<NodeSendStateBean> selectAll();

    int updateByPrimaryKey(NodeSendStateBean record);

    /**
     *@描述  取最大的orderId，by processId
     *@参数 processId
     *@返回值 OrderId
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/3/5
     *@修改人和其它信息
     */
    NodeSendStateBean getMaxOrderId(String processId);
}