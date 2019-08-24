package com.iflytek.dep.admin.dao;

import com.iflytek.dep.admin.model.OrgType;
import java.util.List;

public interface OrgTypeMapper {
    int deleteByPrimaryKey(String orgTypeDm);

    int insert(OrgType record);

    OrgType selectByPrimaryKey(String orgTypeDm);

    List<OrgType> selectAll();

    int updateByPrimaryKey(OrgType record);
}