package com.iflytek.dep.admin.model.vo;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description: 数据包状态代码
 * @date 2019/3/14--10:23
 */
public class StateVo {
    private String globalStateDm;

    private String globalStateMc;

    public StateVo(){}

    public StateVo(String globalStateDm, String globalStateMc) {
        this.globalStateDm = globalStateDm;
        this.globalStateMc = globalStateMc;
    }

    public String getGlobalStateDm() {
        return globalStateDm;
    }

    public void setGlobalStateDm(String globalStateDm) {
        this.globalStateDm = globalStateDm;
    }

    public String getGlobalStateMc() {
        return globalStateMc;
    }

    public void setGlobalStateMc(String globalStateMc) {
        this.globalStateMc = globalStateMc;
    }
}