package com.iflytek.dep.server.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataPackSubBeanMapper {
    int deleteByPrimaryKey(String subPackageId);

    int insert(com.iflytek.dep.server.model.DataPackSubBean record);

    com.iflytek.dep.server.model.DataPackSubBean selectByPrimaryKey(String subPackageId);

    List<com.iflytek.dep.server.model.DataPackSubBean> selectAll();

    int updateByPrimaryKey(com.iflytek.dep.server.model.DataPackSubBean record);
}