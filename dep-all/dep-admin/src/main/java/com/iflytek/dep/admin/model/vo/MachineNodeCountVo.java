package com.iflytek.dep.admin.model.vo;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.admin.model.vo
 * @Description: MachineNodeCountVo
 * @date 2019/3/7--11:42
 */
public class MachineNodeCountVo {

    private String serverNodeId;

    private String serverNodeName;

    private int totalFileServer = 0;

    private int totalFTPServer = 0;

    public MachineNodeCountVo() {
    }

    public MachineNodeCountVo(String serverNodeId, String serverNodeName, int totalFileServer, int totalFTPServer) {
        this.serverNodeId = serverNodeId;
        this.serverNodeName = serverNodeName;
        this.totalFileServer = totalFileServer;
        this.totalFTPServer = totalFTPServer;
    }

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId;
    }

    public String getServerNodeName() {
        return serverNodeName;
    }

    public void setServerNodeName(String serverNodeName) {
        this.serverNodeName = serverNodeName;
    }

    public int getTotalFileServer() {
        return totalFileServer;
    }

    public void setTotalFileServer(int totalFileServer) {
        this.totalFileServer = totalFileServer;
    }

    public int getTotalFTPServer() {
        return totalFTPServer;
    }

    public void setTotalFTPServer(int totalFTPServer) {
        this.totalFTPServer = totalFTPServer;
    }
}