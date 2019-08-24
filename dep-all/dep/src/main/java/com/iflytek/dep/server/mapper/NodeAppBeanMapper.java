package com.iflytek.dep.server.mapper;

import com.iflytek.dep.server.model.NodeAppBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeAppBeanMapper {
    int deleteByPrimaryKey(String appId);

    int insert(com.iflytek.dep.server.model.NodeAppBean record);

    com.iflytek.dep.server.model.NodeAppBean selectByPrimaryKey(String appId);

    List<com.iflytek.dep.server.model.NodeAppBean> selectAll();


    List<NodeAppBean> selectByNodeIdAndAppId(NodeAppBean record);

    int updateByPrimaryKey(com.iflytek.dep.server.model.NodeAppBean record);



}