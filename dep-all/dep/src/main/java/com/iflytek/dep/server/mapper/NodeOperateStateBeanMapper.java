package com.iflytek.dep.server.mapper;

import com.iflytek.dep.server.model.NodeOperateStateBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeOperateStateBeanMapper {
    int deleteByPrimaryKey(String uuid);

    int insert(NodeOperateStateBean record);

    NodeOperateStateBean selectByPrimaryKey(String uuid);

    List<NodeOperateStateBean> selectAll();

    int updateByPrimaryKey(NodeOperateStateBean record);

    /**
     *@描述  取最大的orderId，by processId
     *@参数 processId
     *@返回值 OrderId
     *@创建人  姚伟-weiyao2
     *@创建时间  2019/3/5
     *@修改人和其它信息
     */
    NodeOperateStateBean getMaxOrderId(String processId);

}