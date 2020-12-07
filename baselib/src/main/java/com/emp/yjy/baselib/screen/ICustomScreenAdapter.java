package com.emp.yjy.baselib.screen;

/**
 * @author Created by LRH
 * @date 2020/12/7 16:00
 */
public interface ICustomScreenAdapter {
    /**
     * 是否基于宽度适配
     *
     * @return true：是
     * false：否
     */
    boolean isBaseOnWidth();

    /**
     * 获取设计图尺寸
     *
     * @return 尺寸
     */
    float getDpSize();


}
