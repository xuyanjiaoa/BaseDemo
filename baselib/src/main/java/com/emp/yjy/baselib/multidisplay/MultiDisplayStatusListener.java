package com.emp.yjy.baselib.multidisplay;

/**
 * @author Created by LRH
 * @description 双屏显示状态监听
 * @date 2021/1/6 13:16
 */
public interface MultiDisplayStatusListener {

    /**
     * 挡墙状态
     *
     * @param multiDisplay
     * @param status       状态{@link MultiDisplayStatus}
     */
    void status(MultiDisplay multiDisplay, MultiDisplayStatus status);
}
