package com.emp.yjy.basedemo.activity;

import android.Manifest;

import com.emp.yjy.basedemo.R;
import com.emp.yjy.basedemo.base.CusBaseActivity;
import com.emp.yjy.baselib.base.BaseActivity;
import com.emp.yjy.baselib.utils.LogUtils;
import com.emp.yjy.baselib.utils.PermissionUtils;
import com.emp.yjy.cameralib.camera.CameraView;
import com.emp.yjy.cameralib.camera.ICameraControl;

import java.util.List;

/**
 * @author Created by LRH
 * @date 2020/12/3 16:05
 */
public class CameraViewDemoActivity extends CusBaseActivity {
    private static final String TAG = "CameraViewDemoActivity";
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
        mCameraView.autoFocus(true,500);
        mCameraView.setPreviewCallBack(new ICameraControl.PreviewCallback() {
            @Override
            public int nv21Data(byte[] data, int previewWith, int previewHeight, int rotation) {
                LogUtils.d(TAG, "previewWith  = " + previewWith);
                LogUtils.d(TAG, "previewHeight  = " + previewHeight);
                LogUtils.d(TAG, "rotation  = " + rotation);
                LogUtils.d(TAG, "data len = " + data.length);
                return 0;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
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
    protected void onStop() {
        super.onStop();
        mCameraView.stop();
    }
}
