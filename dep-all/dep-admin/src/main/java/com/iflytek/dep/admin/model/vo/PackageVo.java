package com.iflytek.dep.admin.model.vo;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description:
 * @date 2019/3/1--15:40
 */
public class PackageVo implements Comparable<PackageVo> {

    private String packageId;

    private String parentId;

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public int compareTo(PackageVo o) {
        return this.packageId.compareTo(o.getPackageId());
    }
}