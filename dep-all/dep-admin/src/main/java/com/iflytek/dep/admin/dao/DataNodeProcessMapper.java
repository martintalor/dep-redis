package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.DataNodeProcess;
import com.iflytek.dep.admin.model.vo.OperateStateVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataNodeProcessMapper {
    int deleteByPrimaryKey(String processId);

    int insert(DataNodeProcess record);

    DataNodeProcess selectByPrimaryKey(String processId);

    List<DataNodeProcess> selectAll();

    int updateByPrimaryKey(DataNodeProcess record);

    List<OperateStateVo> selectByIDs(@Param("packageId") String packageId, @Param("nodeId") String nodeId, @Param("toNode") String toNode);
}