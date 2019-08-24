package com.iflytek.dep.server.mapper;

import com.iflytek.dep.server.model.MachineNodeBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MachineNodeMapper {
    List<MachineNodeBean> listDocServer(@Param("serverNodeId") String serverNodeId);

    MachineNodeBean selectByPrimaryKey(@Param("nodeId") String nodeId);
}