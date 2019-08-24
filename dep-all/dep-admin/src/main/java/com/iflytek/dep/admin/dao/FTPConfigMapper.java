package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.FTPConfig;
import com.iflytek.dep.admin.model.vo.FTPConfigVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FTPConfigMapper {
    int deleteByPrimaryKey(String ftpId);

    int insert(FTPConfig record);

    FTPConfig selectByPrimaryKey(String ftpId);

    List<FTPConfig> selectAll();

    FTPConfig selectByNodeId(String nodeId);

    int updateByNodeId(@Param("ftpConfig") FTPConfig ftpConfig);

    List<FTPConfigVo> selectByServerNodeId(@Param("serverNodeId") String serverNodeId);

    int deleteByNodeId(String nodeId);

    List<FTPConfig> selectAllByServerNodeId(@Param("serverNodeId") String serverNodeId);
}