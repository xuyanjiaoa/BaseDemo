package com.emp.yjy.basedemo.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

import com.emp.yjy.baselib.multidisplay.MultiDisplay;

/**
 * @author Created by LRH
 * @date 2020/12/16 17:39
 */
public abstract class CusBaseMultiDisplay extends MultiDisplay {

    public CusBaseMultiDisplay(Activity activity, Display display) {
        super(activity, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeAttachView();
        setContentView(layoutId());
        initView();
        initListener();
        initData();
    }

    /**
     * 设置布局id
     *
     * @return 布局页面id
     */
    protected abstract int layoutId();

    /**
     * 初始化控件
     */
    protected abstract void initView();


    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 初始化监听器
     */
    protected void initListener() {

    }

    protected void beforeAttachView() {

    }


    @Override
    public void show() {
        //防止dialog显示时导航栏会显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
    }
}
