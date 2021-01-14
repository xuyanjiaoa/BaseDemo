package com.emp.yjy.cameralib.camera;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.IntDef;


/**
 * @author Created by LRH
 * @date 2020/12/3 11:45
 */
public interface ICameraControl {
    long DEFAULT_FOCUS_INTERVAL = 1000;
    /**
     * 闪光灯关 {@link #setFlashMode(int)}
     */
    int FLASH_MODE_OFF = 0;
    /**
     * 闪光灯开 {@link #setFlashMode(int)}
     */
    int FLASH_MODE_TORCH = 1;
    /**
     * 闪光灯自动 {@link #setFlashMode(int)}
     */
    int FLASH_MODE_AUTO = 2;

    @IntDef({FLASH_MODE_TORCH, FLASH_MODE_OFF, FLASH_MODE_AUTO})
    @interface FlashMode {

    }

    /**
     * 垂直方向 {@link #setDisplayOrientation(int)}
     */
    int ORIENTATION_PORTRAIT = 0;
    /**
     * 水平方向 {@link #setDisplayOrientation(int)}
     */
    int ORIENTATION_HORIZONTAL = 90;
    /**
     * 水平翻转方向 {@link #setDisplayOrientation(int)}
     */
    int ORIENTATION_INVERT = 270;

    /**
     * 照相回调。
     */
    @IntDef({ORIENTATION_PORTRAIT, ORIENTATION_HORIZONTAL, ORIENTATION_INVERT})
    public @interface Orientation {

    }


    /**
     * 拍照回调接口
     */
    interface OnTakePictureCallback {
        /**
         * 拍照数据回调
         *
         * @param data 数据
         */
        void onPictureTaken(byte[] data);
    }

    /**
     * 设置预览会回调
     */
    void setPreviewCallback(PreviewCallback callback);

    /**
     * 预览回调
     */
    interface PreviewCallback {
        /**
         * nv21 数据流
         *
         * @param data          数据流
         * @param rotation      旋转角度
         * @param previewWith   预览宽度
         * @param previewHeight 预览高度
         * @return
         */
        int nv21Data(byte[] data, int previewWith, int previewHeight, int rotation);
    }

    /**
     * 打开相机。
     */
    void start();

    /**
     * 关闭相机
     */
    void stop();

    void pause();

//    void resume();


    /**
     * 看到的预览可能不是照片的全部。返回预览视图的全貌。
     *
     * @return 预览视图frame;
     */
    Rect getPreviewFrame();

    /**
     * 拍照。结果在回调中获取。
     *
     * @param callback 拍照结果回调
     */
    void takePicture(OnTakePictureCallback callback);

    /**
     * 设置水平方向
     *
     * @param displayOrientation 参数值见 {}
     */
    void setDisplayOrientation(@Orientation int displayOrientation);

    /**
     * 设置闪光灯状态。
     *
     * @param flashMode {@link #FLASH_MODE_TORCH,#FLASH_MODE_OFF,#FLASH_MODE_AUTO}
     */
    void setFlashMode(@FlashMode int flashMode);

    /**
     * 获取当前闪光灯状态
     *
     * @return 当前闪光灯状态 参见 {@link #setFlashMode(int)}
     */
    @FlashMode
    int getFlashMode();

    /**
     * 获取相机预览视图
     *
     * @return
     */
    View getDisplayView();

    /**
     * 是否自动聚焦
     *
     * @param autoFocus
     * @param interval
     */
    void autoFocus(boolean autoFocus, long interval);


    /**
     * 设置预览镜像
     *
     * @param enable
     */
    void setMirror(boolean enable);

    /**
     * 异步开启摄像头
     */
    void startAsync();
}
