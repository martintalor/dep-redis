package com.iflytek.dep.server.mapper;

import com.iflytek.dep.server.model.FTPConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.mapper
 * @Description:
 * @date 2019/2/28--10:40
 */
@Repository
public interface FTPConfigMapper {

    FTPConfig selectByPrimaryKey(@Param("ftpId") String ftpId);

    List<FTPConfig> selectAll();

    FTPConfig selectByNodeId(@Param("nodeId") String nodeId);

    List<FTPConfig> selectByServerNodeId(@Param("serverNodeId") String serverNodeId);

}