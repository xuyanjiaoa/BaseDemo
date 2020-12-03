package com.emp.yjy.basedemo;

import android.Manifest;

import com.emp.yjy.baselib.base.BaseActivity;
import com.emp.yjy.baselib.utils.PermissionUtils;
import com.emp.yjy.cameralib.camera.CameraView;
import com.emp.yjy.cameralib.camera.ICameraControl;

import java.util.List;

/**
 * @author Created by LRH
 * @date 2020/12/3 16:05
 */
public class CameraViewDemoActivity extends BaseActivity {
    private CameraView mCameraView;

    @Override
    protected void initData() {

    }

    @Override
    protected int layoutId() {
        return R.layout.activity_camera_view_demo;
    }

    @Override
    protected void initView() {
        mCameraView = findViewById(R.id.camera_view);
        mCameraView.setFlashMode(ICameraControl.FLASH_MODE_AUTO);
        mCameraView.autoFocus(false,500);
        PermissionUtils.checkPermissions(this, new String[]{Manifest.permission.CAMERA}, new PermissionUtils.OnPermissionCallback() {
            @Override
            public void onSuccess() {
                mCameraView.start();
            }

            @Override
            public void onError(List<String> deniedList) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCameraView.stop();
    }
}
