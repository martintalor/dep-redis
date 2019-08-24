package com.iflytek.dep.server.mapper;

import com.iflytek.dep.server.model.DataNodeProcessBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataNodeProcessBeanMapper {
    int deleteByPrimaryKey(String processId);

    int insert(DataNodeProcessBean record);

    DataNodeProcessBean selectByPrimaryKey(String processId);

    List<DataNodeProcessBean> selectAll();

    int updateByPrimaryKey(DataNodeProcessBean record);

    DataNodeProcessBean selectByUnique(@Param("packageId") String packageId, @Param("nodeId") String nodeId, @Param("toNodeId") String toNodeId);

}