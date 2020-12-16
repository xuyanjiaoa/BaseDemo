package com.emp.yjy.basedemo;

import android.view.View;
import android.view.ViewGroup;

import com.emp.yjy.basedemo.base.CusBaseActivity;
import com.emp.yjy.baselib.base.BaseActivity;
import com.emp.yjy.baselib.utils.DensityUtils;
import com.emp.yjy.baselib.utils.ScreenUtils;
import com.emp.yjy.uilib.scan.ScannerBarView;

/**
 * 使用示例
 *
 * @author SZ02204
 */
public class ScanBarViewDemoActivity extends CusBaseActivity implements View.OnClickListener {
    private ScannerBarView mScannerBarView;
//    private ScannerBarView2 mScannerBarView;

    @Override
    protected void initData() {

    }

    @Override
    protected int layoutId() {
        return R.layout.activity_scanbar_view_demo;
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView() {
        mScannerBarView = findViewById(R.id.scan_bar_view);

        int appViewWidth = ScreenUtils.getAppViewWidth(this);
        ViewGroup.LayoutParams layoutParams = mScannerBarView.getLayoutParams();
        layoutParams.width = (int) (appViewWidth * 0.8);
        layoutParams.height = (int) (appViewWidth * 0.8);
        mScannerBarView.setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mScannerBarView != null) {
            mScannerBarView.start();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mScannerBarView != null) {
            mScannerBarView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScannerBarView != null) {
            mScannerBarView.stop();
        }
    }
}