package com.iflytek.dep.server.mapper;

import com.iflytek.dep.server.model.DEPServerMonitorBean;
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
public interface ServerMonitorBeanMapper {
    DEPServerMonitorBean selectByPrimaryKey(String depServerId);
    List<DEPServerMonitorBean> selectAll();
    int updateByPrimaryKey(DEPServerMonitorBean record);

    void insert(DEPServerMonitorBean record);
}