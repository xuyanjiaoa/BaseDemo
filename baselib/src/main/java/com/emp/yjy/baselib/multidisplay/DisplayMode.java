package com.emp.yjy.baselib.multidisplay;

/**
 * 显示模式枚举
 *
 * @author Created by LRH
 * @date 2020/12/16 17:51
 */
public enum DisplayMode {
    //APP模式，可交互
    APP(2),
    //ALERT不可交互
    ALERT(2003);

    private final int mMode;

    DisplayMode(int mode) {
        this.mMode = mode;
    }

    public int getMode() {
        return mMode;
    }
}
