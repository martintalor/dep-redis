package com.iflytek.dep.server.ftp.config;

/**
 * @author yftao
 * @version V1.0
 * @Package com.iflytek.dep.ftp.config
 * @Description: FTP属性配置
 * @date 2019/3/4--14:39
 */
public class FtpClientConfig {
    // ftp连接属性配置
    private String host;                      //ip
    private int port;                         //端口
    private String username;                  //登录名
    private String password;                  //密码
    private boolean passiveMode = false;      //连接是否为主动模式
    private String encoding = "utf-8";        //编码
    private int clientTimeout = 60000;                //超时时间
    private int keepAliveTimeout = 10;         //设置keepAlive单位秒0禁用
    private int transferFileType = 2;         //文件传送类型 0=ASCII_FILE_TYPE（ASCII格式） 1=EBCDIC_FILE_TYPE 2=LOCAL_FILE_TYPE（二进制文件）
    private int bufferSize = 1024*1000;            //缓存大小
    private String workingDirectory;          //默认进入的路径
    private String nodeId;                    //节点id

    //FTP连接池配置
    private int maxTotal = 20;                //最大数
    private int minIdle = 5;                 //最小空闲
    private int maxIdle = 10;                 //最大空闲
    private long maxWait = 10000;                 //最大等待时间
    private boolean blockWhenExhausted = true;      //池对象耗尽之后是否阻塞,maxWait<0时一直等待
    private boolean testOnBorrow = true;      //取对象是验证
    private boolean testOnReturn = true;      //回收验证
    private boolean testOnCreate = true;      //创建时验证
    private boolean testWhileIdle = true;      //空闲验证

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public boolean isPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getClientTimeout() {
        return clientTimeout;
    }

    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

    public int getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public void setKeepAliveTimeout(int keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public int getTransferFileType() {
        return transferFileType;
    }

    public void setTransferFileType(int transferFileType) {
        this.transferFileType = transferFileType;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isBlockWhenExhausted() {
        return blockWhenExhausted;
    }

    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestOnCreate() {
        return testOnCreate;
    }

    public void setTestOnCreate(boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    @Override
    public String toString() {
        return "FtpClientConfig{" +
                "host" + host + '\'' +
                ", port='" + port + '\'' +
                ", username='" + username + '\'' +
                ", nodeId='" + nodeId + '\'' +
                '}';
    }
}
