package com.iflytek.dep.server.utils;

import java.math.BigDecimal;

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
        //加密压缩包前缀
        public static final String ENCRYPT_PREFIX = "[encrpted]";
        //地址分隔符
        public final static String FILESPLIT = "/";
        //ZIP包后缀
        public final static String ZIP = ".zip";
        //名称下标
        public final static String SPILT = "_";
        //公钥文件后缀
        public final static String PUB = "_rsa.pub";
        //私钥文件后缀
        public final static String PRI = "_rsa.key";
        //公钥对应键名称后缀
        public final static String KEY = ".public.key";
        //ack包分割符
        public final static String ACK_FIX = "#";
        //package包分割符
        public final static String PACKAGE_FIX = "\\.";
        //发往多节点的app之间的分割符
        public final static String APPSPLIT = ",";
        //发往多节点的receiver之间的分割符
        public final static String RECEIVERSPLIT = ";";
    }

    //登录相关常量配置
    public interface LOGIN_INFO {
        //地址分隔符
        public final static String SESSION_KEY = "longinKey";
        public static final String SESSION_HEADER_KEY = "sessionStatus";
        public static final String INVALIDA_SESSION_STATUS = "invalid";
        public static final String RESPONSE_BODY_CONTENT = "{\"message\":\"哎呦，session失效，请重新登录！\"}";

    }

    //加密状态相关
    public interface STATE {
        public final static String SELF = "self";
        public final static String CA = "ca";
    }

    //OperateState 数据包操作状态
    public interface OPERATESTATE {
        //源节点压缩中
        public final static String YSZ = "A01";
        //源节点压缩完成
        public final static String YS = "A02";
        //源节点加密中
        public final static String JIAMZ = "A11";
        //源节点加密完成
        public final static String JIAM = "A12";
        //中心节点解密中
        public final static String ZXJIEMZ = "B01";
        //中心节点解密完成
        public final static String ZXJIEM = "B02";
        //中心节点加密中
        public final static String ZXJIAMZ = "B11";
        //中心节点加密完成
        public final static String ZXJIAM = "B12";
        //目标节点解密中
        public final static String JIEMIZ = "C01";
        //目标节点解密完成
        public final static String JIEMI = "C02";
        //目标节点解压中
        public final static String JYZ = "C11";
        //目标节点解压完成
        public final static String JY = "C12";
    }

    // 网闸类型,01无网闸，02单通，03双通
    public interface NET_BRAKE_TYPE {
        // 无网闸
        public final static String NO_NET_BRAKE = "01";
        // 单通网闸
        public final static String SINGLE_NET_BRAKE = "02";
        // 双通网闸
        public final static String DOUBLE_NET_BRAKE = "03";
    }

    public interface CA{
        //接口调用成功
        public final static BigDecimal CALL_SUCCESS = new BigDecimal(1);
        //接口调用失败
        public final static BigDecimal CALL_FAIL = new BigDecimal(0);
        //mode加密模式
        public final static String ENCRYPT = "encrypt";
        //mode解密模式
        public final static String DECRYPT = "decrypt";
        //回调成功状态
        public final static String BACK_SUCCESS = "success";
        //回调失败状态
        public final static String BACK_ERROR = "error";
    }

}
