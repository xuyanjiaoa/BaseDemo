package com.emp.yjy.basedemo.page;

import android.app.Activity;
import android.view.Display;

import com.emp.yjy.basedemo.R;
import com.emp.yjy.basedemo.base.CusBaseMultiDisplay;

/**
 * @author Created by LRH
 * @date 2020/12/16 17:44
 */
public class PageBack extends CusBaseMultiDisplay {

    public PageBack(Activity activity, Display display) {
        super(activity, display);
    }

    @Override
    protected int layoutId() {
        return R.layout.page_back;
    }

    @Override
    protected void initView() {

    }
}
