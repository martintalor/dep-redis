package com.iflytek.dep.admin.model.dto;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.dto
 * @Description:
 * @date 2019/2/26--14:36
 */
public class MachineNodeDetailDto {

    // 物理节点信息
    private String nodeId;

    private String machineIp;

    private String serverNodeId;

    private String nodeRemark;

    private String nodeTypeDm;

    private String flagEnable;

    private String flagDelete;

    // 前置机信息
    private String ftpId;

    //private String nodeId;

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

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId;
    }

    public String getNodeRemark() {
        return nodeRemark;
    }

    public void setNodeRemark(String nodeRemark) {
        this.nodeRemark = nodeRemark;
    }

    public String getNodeTypeDm() {
        return nodeTypeDm;
    }

    public void setNodeTypeDm(String nodeTypeDm) {
        this.nodeTypeDm = nodeTypeDm;
    }

    public String getFlagEnable() {
        return flagEnable;
    }

    public void setFlagEnable(String flagEnable) {
        this.flagEnable = flagEnable;
    }

    public String getFlagDelete() {
        return flagDelete;
    }

    public void setFlagDelete(String flagDelete) {
        this.flagDelete = flagDelete;
    }

    public String getFtpId() {
        return ftpId;
    }

    public void setFtpId(String ftpId) {
        this.ftpId = ftpId;
    }

    public String getFtpIp() {
        return ftpIp;
    }

    public void setFtpIp(String ftpIp) {
        this.ftpIp = ftpIp;
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
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDataPackageFolderUp() {
        return dataPackageFolderUp;
    }

    public void setDataPackageFolderUp(String dataPackageFolderUp) {
        this.dataPackageFolderUp = dataPackageFolderUp;
    }

    public String getDataPackageFolderDown() {
        return dataPackageFolderDown;
    }

    public void setDataPackageFolderDown(String dataPackageFolderDown) {
        this.dataPackageFolderDown = dataPackageFolderDown;
    }

    public String getAckPackageFolderUp() {
        return ackPackageFolderUp;
    }

    public void setAckPackageFolderUp(String ackPackageFolderUp) {
        this.ackPackageFolderUp = ackPackageFolderUp;
    }

    public String getAckPackageFolderDown() {
        return ackPackageFolderDown;
    }

    public void setAckPackageFolderDown(String ackPackageFolderDown) {
        this.ackPackageFolderDown = ackPackageFolderDown;
    }

    public String getTmpPackageFolder() {
        return tmpPackageFolder;
    }

    public void setTmpPackageFolder(String tmpPackageFolder) {
        this.tmpPackageFolder = tmpPackageFolder;
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