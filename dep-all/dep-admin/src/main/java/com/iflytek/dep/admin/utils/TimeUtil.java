package com.iflytek.dep.admin.utils;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 朱一帆
 * @version V1.0
 * @Package com.iflytek.dep.admin.utils
 * @Description:
 * @date 2019/6/10--21:16
 */
public class TimeUtil {



    /**
     * 获取某个日期的开始时间
     * @param d
     * @return
     */
    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar=Calendar.getInstance();
        if(null!=d){
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }
    /**
     * 获取某个日期的结束时间
     * @param d
     * @return
     */
    public static Date getDayEndTime(Date d) {
        Calendar calendar=Calendar.getInstance();
        if(null!=d){
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }


    /**
     * 获取本周的开始时间
     * @return
     */
    public static Date getBeginDayOfWeek(){
        Date date=new Date();
        if(date==null){
            return null;
        }
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek=cal.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek==1){
            dayOfWeek+=7;
        }
        cal.add(Calendar.DATE, 2-dayOfWeek);
        return getDayStartTime(cal.getTime());
    }

    /**
     * 获取某周的结束时间
     * @param d
     * @return
     */
    public static Date getEndDayOfWeek(Date d){
        Calendar cal=Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    /**
     * date类型进行格式化输出（返回类型：String）
     * @param date
     * @return
     */
    public static String dateFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 将"2015-08-31 21:08:06"型字符串转化为Date
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date StringToDate(String str) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = (Date) formatter.parse(str);
        return date;
    }
}
