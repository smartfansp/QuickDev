package com.fansp.quickdev.base;

import android.os.Environment;

import com.fansp.quickdev.util.LogUtils;

import java.io.File;

public class AppConst {
    public static final String TAG = "fansp_log";
    public static final int DEBUGLEVEL = LogUtils.LEVEL_ALL;//日志输出级别,正式包设置 LEVEL_OFF
    public static final String checkVersion = "http://121.204.110.49:8080/download/UpdateCheck.xml";//升级链接
    public static final String BASE_WEATHER = "http://wthrcdn.etouch.cn/weather_mini";//天气
    public static final String key = "1V2M3P99";
    public static final String iv = "99P3M2V1";
    public static final String ISLOGIN = "islogin"; //判断是否需要登陆
    public static final String CHECK_STATE = "check_state"; //判断是否需要登陆
    public static final String ISREGIST = "isregist"; //判断是不是注册
    public static final String LoginName = "LoginName";
    public static final String UserName = "UserName";
    public static final String AccountID = "AccountID";
    public static final String RoleCode = "RoleCode";
    public static final String ISFIRSTOPEN = "isfirstopen";//第一次打开app tag

    public static final String ORDER_ALL = "全部";
    public static final String ORDER_YZF = "已支付";
    public static final String ORDER_WZF = "未支付";
    public static final String ORDER_YQX = "已取消";
}
