package com.iflytek.dep.admin.utils;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.utils
 * @Description:
 * @date 2019/2/22--13:44
 */
public class CommonConstants {
    //应用相关
    public interface LOG {
        public static final String SUCCESS = "1";
        public static final String FAILURE = "0";
        public static final String SUCCESS_MSG = "SUCCESS";
        public static final String ERROR_MSG = "ERROR";
        public static final String ERROR_LOG = "错误信息：";
        public static final String DEBUG_LOG = "returning content：{}";
    }

    //命名相关
    public interface NAME {
        //发送应用id
        public final static String FROM = "PKG_";
        //目标应用id
        public final static String TO = "_TO_";
        //地址分隔符
        public final static String FILESPLIT = "/";
        //ack前缀
        public final static String ACK = "ACK#";
    }

    //登录相关常量配置
    public interface LOGIN_INFO {
        //地址分隔符
        public final static String SESSION_KEY = "longinKey";
        public static final String SESSION_HEADER_KEY = "sessionStatus";
        public static final String INVALIDA_SESSION_STATUS = "invalid";
        public static final String RESPONSE_BODY_CONTENT = "{\"message\":\"哎呦，session失效，请重新登录！\"}";

    }

    public interface RESPONSE_INFO {
        public final static String ERROR_MESSAGE = "";
        //接口请求成功代码
        public final static String SUCCESS = "1";
        //接口请求成功代码
        public final static String FAILURE = "0";
    }

    //逻辑节点信息
    public interface SERVER_NODE_TYPE {
        public static final String FILE_SERVER = "01";
        public static final String FTP_SERVER = "02";
        public static final String ENABLE = "Y";
        public static final String DISABLE = "N";

    }

    //逻辑节点信息
    public interface FLAG_DELETE {
        public static final String TRUE = "Y";
        public static final String FALSE = "N";
    }

    //逻辑节点类型
    public interface LOGICAL_SERVER_NODE {
        public static final String TYPE = "Z_SERV";// 中心节点
    }
}
