package com.iflytek.dep.server.utils;

/**
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Package com.iflytek.dep.server.utils
 * @Description:
 * @date 2019/3/11--11:03
 */
public class StringUtil {
    /**
     * 判断是否为空字符串最优代码
     * @param str
     * @return 如果为空，则返回true
     */
    public static boolean isEmpty(String str){
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断字符串是否非空
     * @param str 如果不为空，则返回true
     * @return
     */
    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }
}
