package com.iflytek.dep.server.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataPackBeanMapper {
    int deleteByPrimaryKey(String packageId);

    int insert(com.iflytek.dep.server.model.DataPackBean record);

    com.iflytek.dep.server.model.DataPackBean selectByPrimaryKey(String packageId);

    List<com.iflytek.dep.server.model.DataPackBean> selectAll();

    int updateByPrimaryKey(com.iflytek.dep.server.model.DataPackBean record);
}