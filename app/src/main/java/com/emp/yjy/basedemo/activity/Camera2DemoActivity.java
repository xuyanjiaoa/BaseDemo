package com.emp.yjy.basedemo.activity;

import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import com.emp.yjy.basedemo.R;
import com.emp.yjy.baselib.base.BaseActivity;
import com.emp.yjy.baselib.utils.LogUtils;
import com.emp.yjy.cameralib.camera.ex.CameraKitEx;
import com.emp.yjy.cameralib.camera2.AutoFitTextureView;
import com.emp.yjy.cameralib.camera2.CameraKit2MultiPreview;

/**
 * @author SZ02204
 */
public class Camera2DemoActivity extends BaseActivity {
    private static final String TAG = "Camera2DemoActivity";
    private CameraKitEx mCameraKitEx;



    @Override
    protected void initData() {

    }

    @Override
    protected int layoutId() {
        return R.layout.activity_camera2_demo;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initView() {
        SurfaceView surfaceView = findViewById(R.id.surface_view);
        mCameraKitEx = new CameraKitEx(0);
        mCameraKitEx.addSurfaceView(surfaceView);
        MediaPlayer player = new MediaPlayer();
        mCameraKitEx.addPlayer(player);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
        mCameraKitEx.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        mCameraKitEx.stop();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStop() {
        super.onStop();

    }
}