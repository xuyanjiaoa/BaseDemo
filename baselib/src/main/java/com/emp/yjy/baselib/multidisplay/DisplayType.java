package com.emp.yjy.baselib.multidisplay;

/**
 * 主副屏枚举
 *
 * @author Created by LRH
 * @date 2020/12/16 17:50
 */
public enum DisplayType {
    //主屏
    FRONT(0),
    //副屏
    BACK(1);

    private final int mType;

    DisplayType(int type) {
        this.mType = type;
    }

    public int getType() {
        return mType;
    }
}
