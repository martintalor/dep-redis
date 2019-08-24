package com.iflytek.dep.server.redis;

import java.util.Map;

public class PackageInfo {

    //dep
    private String fileName;
    private  String packageId;
    private String curNodeId;
    private String  status;
    private String  time;
    private String filePath;

    //etl
    private String path;
    private String jobName;
    private String appIdFrom;
    private String appIdTo;
    private Map<String, String>  params;

    private String packDirPath;


    private PackageInfo(){

    }



    public String getCurNodeId() { return curNodeId; }

    public void setCurNodeId(String curNodeId) { this.curNodeId = curNodeId; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }

    public String getJobName() { return jobName; }

    public void setJobName(String jobName) { this.jobName = jobName; }

    public String getAppIdFrom() { return appIdFrom; }

    public void setAppIdFrom(String appIdFrom) { this.appIdFrom = appIdFrom; }

    public String getAppIdTo() { return appIdTo; }

    public void setAppIdTo(String appIdTo) { this.appIdTo = appIdTo; }

    public Map<String, String> getParams() { return params; }

    public void setParams(Map<String, String> params) { this.params = params; }

    public String getPackDirPath() {
        return packDirPath;
    }

    public void setPackDirPath(String packDirPath) {
        this.packDirPath = packDirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPackageId() { return packageId; }

    public void setPackageId(String packageId) { this.packageId = packageId; }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    //dep
    //private String packageId;
    //private String curNodeId;
    //private String  status;
    //private String  time;
    //private String path;
    //private String jobName;
    //private String appIdFrom;
    //private String appIdTo;
    //private String params;
    public static PackageInfo from(String fileName,String packageId, String curNodeId, String status, String time,String filePath,String jobName,String appIdFrom,String appIdTo, Map<String, String>   params, String packDirPath)
    {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.setFileName(fileName);
        packageInfo.setPackageId(packageId);
        packageInfo.setCurNodeId(curNodeId);
        packageInfo.setStatus(status);
        packageInfo.setTime(time);
        packageInfo.setFilePath(filePath);
        packageInfo.setJobName(jobName);
        packageInfo.setAppIdFrom(appIdFrom);
        packageInfo.setAppIdTo(appIdTo);
        packageInfo.setParams(params);
        packageInfo.setPackDirPath( packDirPath);
        return packageInfo;
    }

    @Override
    public String toString() {
        return "PackageInfo{" +
                "fileName='" + fileName + '\'' +
                ", packageId='" + packageId + '\'' +
                ", curNodeId='" + curNodeId + '\'' +
                ", status='" + status + '\'' +
                ", time='" + time + '\'' +
                ", path='" + path + '\'' +
                ", jobName='" + jobName + '\'' +
                ", appIdFrom='" + appIdFrom + '\'' +
                ", appIdTo='" + appIdTo + '\'' +
                ", params=" + params +
                ", packDirPath='" + packDirPath + '\'' +
                '}';
    }
}
