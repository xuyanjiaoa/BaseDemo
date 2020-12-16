package com.emp.yjy.basedemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.emp.yjy.basedemo.R;
import com.emp.yjy.basedemo.base.CusBaseActivity;

/**
 * 使用示例
 *
 * @author SZ02204
 */
public class MainActivity extends CusBaseActivity implements View.OnClickListener {
    private Button mBtnScanViewDemo;
    private Button mBtnCameraLensView;
    private Button mBtnScanView;
    private Button mBtnCameraView;
    private Button mBtnScreenAutoSize;
    private Button mBtnSoundPlay;
    private Button mBtnMultiDisplay;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mBtnScanViewDemo = findViewById(R.id.btn_scan_bar_view_demo);
        mBtnCameraLensView = findViewById(R.id.btn_camera_lens_view);
        mBtnScanView = findViewById(R.id.btn_scan_view);
        mBtnCameraView = findViewById(R.id.btn_camera_view);
        mBtnScreenAutoSize = findViewById(R.id.screen_auto_size);
        mBtnSoundPlay = findViewById(R.id.btn_sound_play);
        mBtnMultiDisplay = findViewById(R.id.btn_multi_display);
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
        mBtnCameraView.setOnClickListener(this::onClick);
        mBtnScreenAutoSize.setOnClickListener(this::onClick);
        mBtnSoundPlay.setOnClickListener(this::onClick);
        mBtnMultiDisplay.setOnClickListener(this::onClick);
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
                break;
            case R.id.btn_camera_view:
                jumpToActivity(CameraViewDemoActivity.class);
                break;
            case R.id.screen_auto_size:
                jumpToActivity(ScreenAutoSizeDemoActivity.class);
                break;
            case R.id.btn_sound_play:
                jumpToActivity(SoundPlayDemoActivity.class);
                break;
            case R.id.btn_multi_display:
                jumpToActivity(MultiDisplayActivity.class);
                break;
            default:
                break;
        }
    }


    private void jumpToActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}