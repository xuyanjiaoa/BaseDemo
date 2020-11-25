package com.emp.yjy.baselib.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

/**
 * 屏幕相关工具类
 *
 * @author Created by LRH
 * @date 2020/11/11 10:25
 */
public class ScreenUtils {

    /**
     * 获取应用程序显示区域宽度
     *
     * @param context 上下文
     * @return 屏幕宽度（单位：px）
     */
    public static int getAppViewWidth(@NonNull Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return 0;
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取应用程序显示区域高度
     *
     * @param context 上下文
     * @return 屏幕宽度（单位：px）
     */
    public static int getAppViewHeight(@NonNull Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return 0;
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 屏幕宽度
     *
     * @param context 上下文
     * @return 屏幕宽度（单位：px）
     */
    public static int getScreenWidth(@NonNull Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return 0;
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.widthPixels;
    }


    /**
     * 屏幕高度
     *
     * @param context 上下文
     * @return 屏幕宽度（单位：px）
     */
    public static int getScreenHeight(@NonNull Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return 0;
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.heightPixels;
    }


    /**
     * 设置显示
     * @param activity
     * @param orientation 方向
     *                    1：竖屏
     *                    0：横屏
     */
    public static void setScreenOrientation(Activity activity, int orientation) {
        activity.setRequestedOrientation(orientation);
    }

    /**
     * 隐藏导航栏
     *
     * @param activity
     */
    public static void hideNavigationBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
