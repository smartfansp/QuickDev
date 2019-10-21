package com.fansp.quickdev.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    /*
     * 将时间转换为时间戳
     */
    public static String getTime() {
        String timeStamp = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try{
            timeStamp = sdf.format(System.currentTimeMillis());
        } catch(Exception e){
            e.printStackTrace();
        }
        return timeStamp;
    }
    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) {
        String timeStamp = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        Date d;
        try{
            d = sdf.parse(s);
            long l = d.getTime();
            timeStamp = String.valueOf(l);
        } catch(ParseException e){
            e.printStackTrace();
        }
        return timeStamp;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    public static boolean isValidDate(String str) {
        boolean convertSuccess=true;
// 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        try {
// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess=false;
        }
        return convertSuccess;
    }

    /**
     * 将日期格式yyyyMMdd转成yyyy.MM.dd
     * @param str
     * @return
     */
    public static  String  formatDate(String str) {
        String s="";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            format.setLenient(false);
            Date date=format.parse(str);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            s=sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }

}
