package com.emp.yjy.cameralib.Utils;

import android.util.Log;

/**
 * 日志打印工具类
 *
 * @author Created by LRH
 * @date 2020/9/9 14:08
 */
public class CMLogUtils {


    private static final String DEFAULT_TAG = "DEFAULT_TAG";
    private static int defaultLogLev = 5;

    //数字越小等级越高
    public static final int LEV_VERBOSE = 5;
    public static final int LEV_DEBUG = 4;
    public static final int LEV_INFO = 3;
    public static final int LEV_WARN = 2;
    public static final int LEV_ERROR = 1;
    public static final int LEV_WTF = 0;


    public static void w(String tag, String msg) {
        if (defaultLogLev >= LEV_WARN)
            Log.w(tag, msg);
    }

    public static void w(String msg) {
        if (defaultLogLev >= LEV_WARN)
            Log.w(DEFAULT_TAG, msg);
    }


    public static void e(String tag, String msg) {
        if (defaultLogLev >= LEV_ERROR)
            Log.e(tag, msg);
    }

    public static void e(String msg) {
        if (defaultLogLev >= LEV_ERROR)
            Log.e(DEFAULT_TAG, msg);
    }


    public static void d(String tag, String msg) {
        if (defaultLogLev >= LEV_DEBUG)
            Log.d(tag, msg);
    }

    public static void d(String msg) {
        if (defaultLogLev >= LEV_DEBUG)
            Log.d(DEFAULT_TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (defaultLogLev >= LEV_INFO)
            Log.i(tag, msg);
    }

    public static void i(String msg) {
        if (defaultLogLev >= LEV_INFO)
            Log.i(DEFAULT_TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (defaultLogLev >= LEV_VERBOSE)
            Log.v(tag, msg);
    }

    public static void v(String msg) {
        if (defaultLogLev >= LEV_VERBOSE)
            Log.v(DEFAULT_TAG, msg);
    }

    public static void wtf(String tag, String msg) {
        if (defaultLogLev >= LEV_WTF)
            Log.wtf(tag, msg);
    }


    public static void wtf(String msg) {
        if (defaultLogLev >= LEV_WTF)
            Log.wtf(DEFAULT_TAG, msg);
    }

    public static void setLogLev(int level) {
        defaultLogLev = level;
    }
}
