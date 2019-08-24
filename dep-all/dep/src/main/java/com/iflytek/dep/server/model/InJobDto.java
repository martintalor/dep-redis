package com.iflytek.dep.server.model;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.server.model
 * @Description:
 * @date 2019/5/8--9:06
 */

public class InJobDto {

    private String inPath;
    /**
     * 不含扩展名的包名
     */
    private String packageId;

    public String getInPath() {
        return inPath;
    }

    public void setInPath(String inPath) {
        this.inPath = inPath;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
}
