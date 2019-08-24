package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.ServerNodeType;
import java.util.List;

public interface ServerNodeTypeMapper {
    int deleteByPrimaryKey(String serverNodeTypeDm);

    int insert(ServerNodeType record);

    ServerNodeType selectByPrimaryKey(String serverNodeTypeDm);

    List<ServerNodeType> selectAll();

    int updateByPrimaryKey(ServerNodeType record);
}