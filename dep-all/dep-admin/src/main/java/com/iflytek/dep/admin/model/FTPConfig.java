package com.iflytek.dep.admin.model;

public class FTPConfig {
    private String ftpId;

    private String nodeId;

    private String ftpIp;

    private int ftpPort;

    private String username;

    private String password;

    private String dataPackageFolderUp;

    private String dataPackageFolderDown;

    private String ackPackageFolderUp;

    private String ackPackageFolderDown;

    private String tmpPackageFolder;

    private int connectMax;

    private int timeout;

    private int heartbeat;

    public String getFtpId() {
        return ftpId;
    }

    public void setFtpId(String ftpId) {
        this.ftpId = ftpId == null ? null : ftpId.trim();
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId == null ? null : nodeId.trim();
    }

    public String getFtpIp() {
        return ftpIp;
    }

    public void setFtpIp(String ftpIp) {
        this.ftpIp = ftpIp == null ? null : ftpIp.trim();
    }

    public int getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(int ftpPort) {
        this.ftpPort = ftpPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getDataPackageFolderUp() {
        return dataPackageFolderUp;
    }

    public void setDataPackageFolderUp(String dataPackageFolderUp) {
        this.dataPackageFolderUp = dataPackageFolderUp == null ? null : dataPackageFolderUp.trim();
    }

    public String getDataPackageFolderDown() {
        return dataPackageFolderDown;
    }

    public void setDataPackageFolderDown(String dataPackageFolderDown) {
        this.dataPackageFolderDown = dataPackageFolderDown == null ? null : dataPackageFolderDown.trim();
    }

    public String getAckPackageFolderUp() {
        return ackPackageFolderUp;
    }

    public void setAckPackageFolderUp(String ackPackageFolderUp) {
        this.ackPackageFolderUp = ackPackageFolderUp == null ? null : ackPackageFolderUp.trim();
    }

    public String getAckPackageFolderDown() {
        return ackPackageFolderDown;
    }

    public void setAckPackageFolderDown(String ackPackageFolderDown) {
        this.ackPackageFolderDown = ackPackageFolderDown == null ? null : ackPackageFolderDown.trim();
    }

    public String getTmpPackageFolder() {
        return tmpPackageFolder;
    }

    public void setTmpPackageFolder(String tmpPackageFolder) {
        this.tmpPackageFolder = tmpPackageFolder == null ? null : tmpPackageFolder.trim();
    }

    public int getConnectMax() {
        return connectMax;
    }

    public void setConnectMax(int connectMax) {
        this.connectMax = connectMax;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
    }
}