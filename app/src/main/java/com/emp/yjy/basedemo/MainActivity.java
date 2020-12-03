package com.emp.yjy.basedemo;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.emp.yjy.baselib.base.BaseActivity;

/**
 * 使用示例
 *
 * @author SZ02204
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Button mBtnScanViewDemo;
    private Button mBtnCameraLensView;
    private Button mBtnScanView;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mBtnScanViewDemo = findViewById(R.id.btn_scan_bar_view_demo);
        mBtnCameraLensView = findViewById(R.id.btn_camera_lens_view);
        mBtnScanView = findViewById(R.id.btn_scan_view);
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBtnScanViewDemo.setOnClickListener(this::onClick);
        mBtnCameraLensView.setOnClickListener(this::onClick);
        mBtnScanView.setOnClickListener(this::onClick);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_scan_bar_view_demo:
                jumpToActivity(ScanBarViewDemoActivity.class);
                break;
            case R.id.btn_camera_lens_view:
                jumpToActivity(CameraLensViewDemoActivity.class);
                break;
            case R.id.btn_scan_view:
                jumpToActivity(ScanViewDemoActivity.class);

            default:
                break;
        }
    }


    private void jumpToActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}