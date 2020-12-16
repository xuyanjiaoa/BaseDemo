package com.emp.yjy.basedemo.activity;

import com.emp.yjy.basedemo.R;
import com.emp.yjy.basedemo.base.CusBaseActivity;
import com.emp.yjy.baselib.base.BaseActivity;
import com.emp.yjy.baselib.screen.ICustomScreenAdapter;

/**
 * @author SZ02204
 * 屏幕适配工具类使用示例
 */
public class ScreenAutoSizeDemoActivity extends CusBaseActivity implements ICustomScreenAdapter {
    @Override
    protected void initData() {

    }

    @Override
    protected int layoutId() {
        return R.layout.activity_screen_auto_size_demo;
    }

    @Override
    protected void initView() {

    }

    @Override
    public boolean isBaseOnWidth() {
        return true;
    }

    /**
     * 自定义单个页面尺寸
     *
     * @return
     */
    @Override
    public float getDpSize() {
        return 600;
    }
}