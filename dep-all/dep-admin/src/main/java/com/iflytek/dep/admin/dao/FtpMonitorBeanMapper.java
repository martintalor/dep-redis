package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.FtpMonitorBean;
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
public interface FtpMonitorBeanMapper {
    FtpMonitorBean selectByPrimaryKey(String ftpId);
    List<FtpMonitorBean> selectAll();
    int insert(FtpMonitorBean record);
    int updateByPrimaryKey(FtpMonitorBean record);

    FtpMonitorBean selectDocServerMonitorByNodeId(String nodeId);
}