package com.emp.yjy.baselib.media.video;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.text.TextUtils;

/**
 * @author Created by LRH
 * @description
 * @date 2021/1/19 13:54
 */
public class VideoRecorderConfig {
    private Camera camera;
    private int videoWidth;
    private int videoHeight;
    private int cameraRotation;
    private String path;
    private SurfaceTexture mSurfaceTexture;

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getCameraRotation() {
        return cameraRotation;
    }

    public void setCameraRotation(int cameraRotation) {
        this.cameraRotation = cameraRotation;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean checkParam() {
        return mSurfaceTexture != null && camera != null && videoWidth > 0 && videoHeight > 0 && !TextUtils.isEmpty(path);
    }
}
