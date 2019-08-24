package com.iflytek.dep.admin.model.dto.monitor;

import com.iflytek.dep.admin.model.dto.BaseDto;

/**
 * @author xiliu5
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.dto
 * @Description:
 * @date 2019/3/1
 */
public class HealthDto extends BaseDto {

    private String serverNodeId;

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId;
    }
}