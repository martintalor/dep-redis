package com.iflytek.dep.admin.model.vo.monitor;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo.monitor
 * @Description:
 * @date 2019/7/22--17:34
 */
public class MonitorEtlVo {

    private  String jobName;

    private  String scheduled;

    private  double packageSize;

    private  double spendTime;

    private  double rate;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getScheduled() {
        return scheduled;
    }

    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }

    public double getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(double packageSize) {
        this.packageSize = packageSize;
    }

    public double getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(double spendTime) {
        this.spendTime = spendTime;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}