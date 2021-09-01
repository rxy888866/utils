package com.example.utils.util.Date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 时间常用方法
 * SimpleDateFormat函数语法：
 *          G 年代标志符
 *          y 年
 *          M 月
 *          d 日
 *          h 时 在上午或下午 (1~12)
 *          H 时 在一天中 (0~23)
 *          m 分
 *          s 秒
 *          S 毫秒
 *          E 星期
 *          D 一年中的第几天
 *          F 一月中第几个星期几
 *          w 一年中第几个星期
 *          W 一月中第几个星期
 *          a 上午 / 下午 标记符
 *          k 时 在一天中 (1~24)
 *          K 时 在上午或下午 (0~11)
 *          z 时区
 *       常见标准的写法"yyyy-MM-dd HH:mm:ss",注意大小写，时间是24小时制，24小时制转换成12
 */
public class DateUtils {



    public static void main(String[] args) throws Exception {
        String startday =  "2017-09-20";
        String endday ="2017-09-28";
        long DistanceDays = getDistanceDays(startday,endday);//两个时间之间相差距离多少天
        System.out.println(DistanceDays);

        String starttimes =  "2017-01-17 00:10:20";
        String endtimes ="2017-01-18 00:10:21";
        long[] DistanceTimes = getDistanceTimes(starttimes,endtimes);//两个时间相差距离多少天多少小时多少分多少秒 ，以long[]形式返回
        for (int i = 0; i < DistanceTimes.length; i++) {
            System.out.println(DistanceTimes[i]);
        }

        String DistanceTime = getDistanceTime(starttimes,endtimes);//两个时间相差距离多少天多少小时多少分多少秒 ，以String形式返回
        System.out.println(DistanceTime);
    }



    /**
     * 获取时间戳，几秒钟之前
     * @param times  秒钟
     * @return
     */
    public  String DateTime(String times){
        Date date = new Date();
        long time = date.getTime();
        String dateTime = String.valueOf(time - Long.valueOf(times)*1000);
        return dateTime;
    }


    /**
     * String 转换成 Date
     */
    public void StringTODate(){
        try{
            SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd ");
            String s= "2011-07-09 ";
            Date date =  formatter.parse(s);
        }catch (Exception e ){
            e.printStackTrace();
        }
    }


    /**
     *  Date转换成 String
     */
    public void DateTOString(){
        try{
            java.text.SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd ");
            String date = formatter.format(new Date());//格式化数据
        }catch (Exception e ){
            e.printStackTrace();
        }
    }


    /**
     * 获取前几天的日期
     * @param day  几天前
     * @return
     */
    public  String daytime(int day){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -day);
        Date time = calendar.getTime();
        String format = df.format(time);
        return format;
    }


    /**
     * 两个时间之间相差距离多少天
     * @param one 时间参数 1：
     * @param two 时间参数 2：
     * @return 相差天数
     */
    public static long getDistanceDays(String starttime, String endtime) throws Exception{
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date one;
        Date two;
        long days=0;
        try {
            one = df.parse(starttime);
            two = df.parse(endtime);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            days = diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;//返回相差多少天
    }


    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(String starttime, String endtime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(starttime);
            two = df.parse(endtime);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = {day, hour, min, sec};
        return times;
    }



    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(String starttime, String endtime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(starttime);
            two = df.parse(endtime);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day + "天" + hour + "小时" + min + "分" + sec + "秒";
    }

}
