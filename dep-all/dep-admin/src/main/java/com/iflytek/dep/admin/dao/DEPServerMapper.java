package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.DEPServer;
import com.iflytek.dep.admin.model.dto.DEPServerDto;
import com.iflytek.dep.admin.model.vo.DEPServerVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DEPServerMapper {
    int deleteByPrimaryKey(String depServerId);

    int insert(DEPServer record);

    DEPServer selectByPrimaryKey(String depServerId);

    List<DEPServerVo> selectAll(@Param("depServerDto") DEPServerDto depServerDto);

    int updateByPrimaryKey(@Param("depServer") DEPServer depServer);

    List<String> getDEPServerNameById(@Param("serverNodeId") String serverNodeId);

    List<DEPServer> selectByServerNodeId(@Param("serverNodeId") String serverNodeId);
}