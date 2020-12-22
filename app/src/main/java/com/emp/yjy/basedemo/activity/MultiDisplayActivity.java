package com.emp.yjy.basedemo.activity;

import android.view.Display;
import android.view.KeyEvent;

import com.emp.yjy.basedemo.R;
import com.emp.yjy.basedemo.base.CusBaseActivity;
import com.emp.yjy.basedemo.page.PageBack;
import com.emp.yjy.basedemo.page.PageFront;
import com.emp.yjy.baselib.multidisplay.DisplayMode;
import com.emp.yjy.baselib.multidisplay.DisplayType;

/**
 * 双屏适用示例
 *
 * @author SZ02204
 */
public class MultiDisplayActivity extends CusBaseActivity {
    private PageFront mPageFront;
    private PageBack mPageBack;


    @Override
    protected void initData() {

    }

    @Override
    protected int layoutId() {
        return R.layout.activity_multi_display;
    }

    @Override
    protected void initView() {
        // TODO: 2020/12/16  需要进行动态权限申请或者让targetSdkVersion<=22
        Display displayFront = PageFront.getDisplay(this, DisplayType.FRONT);
        if (displayFront != null) {
            mPageFront = new PageFront(this, displayFront);
            mPageFront.setDisplayMode(DisplayMode.ALERT);
            mPageFront.setCancelable(false);
            mPageFront.show();
        }

        Display displayBack = PageBack.getDisplay(this, DisplayType.BACK);
        if (displayBack != null) {
            mPageBack = new PageBack(this, displayBack);
            mPageBack.setDisplayMode(DisplayMode.ALERT);
            mPageBack.setCancelable(false);
            mPageBack.show();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //按返回键关闭dialog
//            if (mPageFront != null) {
//                mPageFront.dismiss();
//            }
//            if (mPageBack != null) {
//                mPageBack.dismiss();
//            }
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mPageFront != null) {
            mPageFront.dismiss();
        }
        if (mPageBack != null) {
            mPageBack.dismiss();
        }
        super.onDestroy();

    }
}