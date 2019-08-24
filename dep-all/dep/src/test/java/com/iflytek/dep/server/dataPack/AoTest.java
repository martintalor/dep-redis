package com.iflytek.dep.server.dataPack;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * @author 姚伟-weiyao2
 * @version V1.0
 * @Package com.iflytek.dep.server.dataPack
 * @Description:
 * @date 2019/4/10--11:29
 */
public class AoTest {
    public static void main(String[] arge) {
        String s = StringEscapeUtils.unescapeHtml4("&#x24;&#x27;&#x2c;");
        System.out.println(s);
        String s1 = StringEscapeUtils.escapeHtml4("\\,");
        System.out.println(s1);

        String s2 = unicode2String("&#x24;&#x2c;");
        System.out.println(s2);

        byte[] b2 = s2.getBytes();
        String sby = new String(b2);
        System.out.println( new String(b2) );
        System.out.println( sby );
    }


    public static String unicode2String(String unicode)
    {
        StringBuffer string = new StringBuffer();

        if (unicode.startsWith("&#x")) {
            String[] hex = unicode.replace("&#x", "").split(";");
            for (int i=0; i<hex.length; i++) {
                int data = Integer.parseInt(hex[i], 16);
                string.append((char) data);
            }
        } else if (unicode.startsWith("&#")) {
            String[] hex = unicode.replace("&#", "").split(";");
            for (int i=0; i<hex.length; i++) {
                int data = Integer.parseInt(hex[i], 10);
                string.append((char) data);
            }
        }

        return string.toString();
    }


}
