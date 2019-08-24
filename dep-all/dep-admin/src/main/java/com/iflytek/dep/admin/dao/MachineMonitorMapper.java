package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.MachineMonitor;
import java.util.List;

public interface MachineMonitorMapper {
    int deleteByPrimaryKey(String monitorId);

    int insert(MachineMonitor record);

    MachineMonitor selectByPrimaryKey(String monitorId);

    List<MachineMonitor> selectAll();

    int updateByPrimaryKey(MachineMonitor record);
}