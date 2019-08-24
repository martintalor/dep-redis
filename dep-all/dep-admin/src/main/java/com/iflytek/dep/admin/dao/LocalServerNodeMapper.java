package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.LocalServerNode;
import com.iflytek.dep.admin.model.dto.LocalServerNodeDto;
import com.iflytek.dep.admin.model.vo.LocalServerNodeTypeVo;
import com.iflytek.dep.admin.model.vo.LocalServerNodeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LocalServerNodeMapper {
    int deleteByPrimaryKey(String serverNodeId);

    int insert(LocalServerNode record);

    LocalServerNode selectByPrimaryKey(String serverNodeId);

    List<LocalServerNodeVo> selectAll(@Param("localServerNodeDto") LocalServerNodeDto localServerNodeDto);

    int updateByPrimaryKey(@Param("localServerNode") LocalServerNode localServerNode);

    List<LocalServerNodeTypeVo> listNode();
}