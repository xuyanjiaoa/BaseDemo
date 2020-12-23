package com.emp.yjy.cameralib.camera.ex;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.emp.yjy.cameralib.Utils.CMLogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by LRH
 * @date 2020/12/22 10:45
 */
public class CameraKitEx {
    private static final String TAG = "CameraKitEx";
    private Camera mCamera;
    private int mCameraId;
    private List<SurfaceView> mSurfaceViews = new ArrayList<>();
    private Camera.Parameters mParameters;
    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;


    public CameraKitEx(int cameraId) {
        mCameraId = cameraId;
    }

    public CameraKitEx(int cameraId, List<SurfaceView> surfaceViewList) {
        mSurfaceViews = surfaceViewList;
    }

    public void addSurfaceView(SurfaceView surfaceView) {
        mSurfaceViews.add(surfaceView);
    }

    public boolean removeSurfaceView(SurfaceView surfaceView) {
        return mSurfaceViews.remove(surfaceView);
    }

    public void addPlayer(MediaPlayer player) {
        this.mMediaPlayer = player;
    }

    private void initCamera() {

        try {
            if (mCamera == null) {
                mCamera = Camera.open(mCameraId);

                if (mParameters == null) {
                    mParameters = mCamera.getParameters();
                    mParameters.setPreviewFormat(ImageFormat.NV21);
                }
            }
        } catch (Exception e) {
            CMLogUtils.e(TAG, e.getMessage());
        }


    }


    private void release() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            // 避免同步代码，为了先设置null后release
            Camera tempC = mCamera;
            mCamera = null;
            tempC.release();
            mCamera = null;
        }
    }

    public void stop() {
        release();
    }

    public void start() {
        if (mSurfaceViews == null || mSurfaceViews.size() <= 0) {
            return;
        }
        for (SurfaceView surfaceView : mSurfaceViews) {
            surfaceView.getHolder().addCallback(new Callback());
//            startPreview(surfaceView.getHolder());
        }
    }

    private void startPreview(SurfaceHolder surfaceHolder) {
        try {
            if (surfaceHolder != null) {
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {

                    }
                });
                mCamera.startPreview();
            }

        } catch (Exception e) {
            e.printStackTrace();
            CMLogUtils.e(TAG, e.getMessage());
        }

    }

    int i = 0;

    private class Callback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            initCamera();
            CMLogUtils.e(TAG, "surfaceCreated------");
            startPreview(holder);
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(holder);
            }
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            CMLogUtils.e(TAG, "surfaceChanged------");
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            CMLogUtils.e(TAG, "surfaceDestroyed------");
            release();
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
            }
        }
    }


}
