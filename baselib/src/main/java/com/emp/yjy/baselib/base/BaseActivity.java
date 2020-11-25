package com.emp.yjy.baselib.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity基类
 *
 * @author Created by LRH
 * @date 2020/11/11 11:24
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeAttachView();
        setContentView(layoutId());
        beforeInitView();
        initView();
        initListener();
        initData();
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化监听器
     */
    protected void initListener() {

    }


    /**
     * 布局id
     *
     * @return 布局id
     */
    protected abstract int layoutId();

    /**
     * 设置布局之前的接口
     * 可用于设置window属性等
     */
    protected void beforeAttachView() {

    }

    protected abstract void initView();

    protected void beforeInitView() {

    }
}
