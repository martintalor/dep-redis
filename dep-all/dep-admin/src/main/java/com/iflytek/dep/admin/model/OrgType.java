package com.iflytek.dep.admin.model;

public class OrgType {
    private String orgTypeDm;

    private String orgTypeMc;

    public String getOrgTypeDm() {
        return orgTypeDm;
    }

    public void setOrgTypeDm(String orgTypeDm) {
        this.orgTypeDm = orgTypeDm == null ? null : orgTypeDm.trim();
    }

    public String getOrgTypeMc() {
        return orgTypeMc;
    }

    public void setOrgTypeMc(String orgTypeMc) {
        this.orgTypeMc = orgTypeMc == null ? null : orgTypeMc.trim();
    }
}