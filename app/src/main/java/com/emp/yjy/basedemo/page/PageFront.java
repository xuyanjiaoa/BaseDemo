package com.emp.yjy.basedemo.page;

import android.app.Activity;
import android.view.Display;

import com.emp.yjy.basedemo.R;
import com.emp.yjy.basedemo.base.CusBaseMultiDisplay;

/**
 * 主屏页面
 * @author Created by LRH
 * @date 2020/12/16 17:43
 */
public class PageFront extends CusBaseMultiDisplay {

    public PageFront(Activity activity, Display display) {
        super(activity, display);
    }

    @Override
    protected int layoutId() {
        return R.layout.page_front;
    }

    @Override
    protected void initView() {

    }
}
