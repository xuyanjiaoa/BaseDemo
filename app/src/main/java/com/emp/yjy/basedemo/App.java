package com.emp.yjy.basedemo;

import android.app.Application;

import com.emp.yjy.baselib.screen.ScreenAdapter;

/**
 * @author Created by LRH
 * @date 2020/12/7 11:08
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //屏幕适配，设计图宽度为400dp，基于宽度适配。适配单位为dp
        ScreenAdapter.autoSize(this, 400, ScreenAdapter.MATCH_BASE_WIDTH, ScreenAdapter.MATCH_UNIT_DP);

    }
}
