package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.NodeApp;
import com.iflytek.dep.admin.model.dto.NodeAppDto;
import com.iflytek.dep.admin.model.vo.NodeAppVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NodeAppMapper {
    int deleteByPrimaryKey(String appId);

    int insert(NodeApp record);

    NodeApp selectByPrimaryKey(String appId);

    List<NodeAppVo> selectAll(@Param("nodeAppDto") NodeAppDto nodeAppDto);

    int updateByPrimaryKey(@Param("nodeApp") NodeApp nodeApp);

    List<String> getAppNameByNodeId(String nodeId);
}