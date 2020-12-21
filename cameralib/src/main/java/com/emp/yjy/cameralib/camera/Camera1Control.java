package com.emp.yjy.cameralib.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import com.emp.yjy.cameralib.Utils.CMLogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Created by LRH
 * @date 2020/12/3 14:03
 */
class Camera1Control implements ICameraControl {
    private static final String TAG = "Camera1Control";


    //显示方向
    private int displayOrientation = 0;
    //摄像头id
    private int cameraId = 0;
    //对焦模式
    private int flashMode;
    //拍照
    private AtomicBoolean takingPicture = new AtomicBoolean(false);
    private Context context;
    private Camera camera;

    //参数
    private Camera.Parameters parameters;
    private Rect previewFrame = new Rect();

    private PreviewView previewView;
    private int rotation = 0;
    private PreviewCallback detectCallback;
    private int previewFrameCount = 0;
    private Camera.Size optSize;
    private View displayView;
    private boolean mAutoFocus;
    private long mFocusInterVal = DEFAULT_FOCUS_INTERVAL;
    private Thread mFocusThread;


    @Override
    public void setPreviewCallback(PreviewCallback callback) {
        detectCallback = callback;
    }

    @Override
    public View getDisplayView() {
        return displayView;
    }

    @Override
    public void autoFocus(boolean autoFocus, long focusInterval) {
        mAutoFocus = autoFocus;
        mFocusInterVal = focusInterval;
    }

    private void onRequestDetect(byte[] data) {
        // 相机已经关闭
        if (camera == null || data == null || optSize == null || previewView == null) {
            return;
        }
        if (detectCallback != null) {
            detectCallback.nv21Data(data, previewView.getWidth(), previewView.getHeight(), rotation);
        }
    }

    @Override
    public void setDisplayOrientation(@ICameraControl.Orientation int displayOrientation) {
        this.displayOrientation = displayOrientation;
        switch (displayOrientation) {
            case ICameraControl.ORIENTATION_PORTRAIT:
                rotation = 90;
                break;
            case ICameraControl.ORIENTATION_HORIZONTAL:
                rotation = 0;
                break;
            case ICameraControl.ORIENTATION_INVERT:
                rotation = 180;
                break;
            default:
                rotation = 0;
        }
        previewView.requestLayout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFlashMode(@FlashMode int flashMode) {
        if (this.flashMode == flashMode) {
            return;
        }
        this.flashMode = flashMode;
//        updateFlashMode(flashMode);
    }

    @Override
    public int getFlashMode() {
        return flashMode;
    }

    @Override
    public void start() {
        startPreview(false);
    }

    @Override
    public void stop() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            stopPreview();
            // 避免同步代码，为了先设置null后release
            Camera tempC = camera;
            camera = null;
            tempC.release();
            camera = null;
            buffer = null;
        }
        if (mFocusThread != null) {
            mFocusThread.interrupt();
            mFocusThread = null;
        }
    }

    private void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
        }
    }

    @Override
    public void pause() {
        if (camera != null) {
            stopPreview();
        }
        setFlashMode(FLASH_MODE_OFF);
    }

//    @Deprecated
//    @Override
//    public void resume() {
//        takingPicture.set(false);
//        if (camera == null) {
//            openCamera();
//        } else {
//            previewView.textureView.setSurfaceTextureListener(surfaceTextureListener);
//            if (previewView.textureView.isAvailable()) {
//                startPreview(false);
//            }
//        }
//    }

    @Override
    public void takePicture(final OnTakePictureCallback onTakePictureCallback) {
        if (takingPicture.get()) {
            return;
        }
        switch (displayOrientation) {
            case ICameraControl.ORIENTATION_PORTRAIT:
                parameters.setRotation(90);
                break;
            case ICameraControl.ORIENTATION_HORIZONTAL:
                parameters.setRotation(0);
                break;
            case ICameraControl.ORIENTATION_INVERT:
                parameters.setRotation(180);
                break;
        }
        try {
            Camera.Size picSize = getOptimalSize(camera.getParameters().getSupportedPictureSizes());
            parameters.setPictureSize(picSize.width, picSize.height);
            camera.setParameters(parameters);
            takingPicture.set(true);
            cancelAutoFocus();
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    startPreview(false);
                    takingPicture.set(false);
                    if (onTakePictureCallback != null) {
                        onTakePictureCallback.onPictureTaken(data);
                    }
                }
            });
        } catch (RuntimeException e) {
            e.printStackTrace();
            startPreview(false);
            takingPicture.set(false);
        }
    }

    public Camera1Control(Context context) {
        this.context = context;
        previewView = new PreviewView(context);
        openCamera();
    }

    private void openCamera() {
        setupDisplayView();
    }

    private void setupDisplayView() {
        final TextureView textureView = new TextureView(context);
        previewView.textureView = textureView;
        previewView.setTextureView(textureView);
        displayView = previewView;
        //textureView.setSurfaceTextureListener(surfaceTextureListener);
    }

    private SurfaceTexture surfaceCache;

    private byte[] buffer = null;

    private void setPreviewCallbackImpl() {
        if (buffer == null) {
            buffer = new byte[displayView.getWidth()
                    * displayView.getHeight() * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8];
        }
        if (camera != null) {
            camera.addCallbackBuffer(buffer);
            camera.setPreviewCallback(previewCallback);
        }
    }

    private void clearPreviewCallback() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            stopPreview();
        }
    }

    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(final byte[] data, Camera camera) {
            // 节流
            if (previewFrameCount++ % 5 != 0) {
                return;
            }

            // 在某些机型和某项项目中，某些帧的data的数据不符合nv21的格式，需要过滤，否则后续处理会导致crash
            if (data.length != parameters.getPreviewSize().width * parameters.getPreviewSize().height * 1.5) {
                return;
            }

            camera.addCallbackBuffer(buffer);


            Camera1Control.this.onRequestDetect(data);

        }
    };


    private void initCamera() {
        try {
            if (camera == null) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        cameraId = i;
                    }
                }
                try {
                    camera = Camera.open(cameraId);
                } catch (Throwable e) {
                    e.printStackTrace();
                    CMLogUtils.e(TAG, e.getMessage());
                    return;
                }

            }
            if (parameters == null) {
                parameters = camera.getParameters();
                parameters.setPreviewFormat(ImageFormat.NV21);
            }
            opPreviewSize(previewView.getWidth(), previewView.getHeight());
            camera.setPreviewTexture(surfaceCache);
            setPreviewCallbackImpl();
            startPreview(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            surfaceCache = surface;
            initCamera();
            updateFlashMode(flashMode);
            CMLogUtils.d(TAG, "onSurfaceTextureAvailable------>");
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            opPreviewSize(previewView.getWidth(), previewView.getHeight());
            startPreview(false);
            setPreviewCallbackImpl();
            updateFlashMode(flashMode);
            CMLogUtils.d(TAG, "onSurfaceTextureSizeChanged------>");

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            updateFlashMode(flashMode);
            setPreviewCallbackImpl();
            CMLogUtils.d(TAG, "onSurfaceTextureUpdated------>");
        }
    };

    // 开启预览
    private void startPreview(boolean checkPermission) {
        previewView.textureView.setSurfaceTextureListener(surfaceTextureListener);
        if (camera == null) {
            initCamera();
        } else {
            camera.startPreview();
            startAutoFocus();
        }
    }

    private void cancelAutoFocus() {
        camera.cancelAutoFocus();
    }

    private void startAutoFocus() {
        if (!mAutoFocus || mFocusThread != null) {
            return;
        }
        CMLogUtils.d(TAG, "开启自动对焦，间隔为：" + mFocusInterVal + "ms");
        mFocusThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mAutoFocus) {
                    if (camera != null && !takingPicture.get()) {
                        try {
                            camera.autoFocus(new Camera.AutoFocusCallback() {
                                @Override
                                public void onAutoFocus(boolean success, Camera camera) {
                                }
                            });
                        } catch (Throwable e) {
                            // startPreview是异步实现，可能在某些机器上前几次调用会autofocus failß
                        }
                    }
                    try {
                        Thread.sleep(mFocusInterVal);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                CMLogUtils.d(TAG, "退出自动对焦！");
            }
        });
        mFocusThread.start();

    }

    private void opPreviewSize(int width, int height) {
        if (parameters != null && camera != null && width > 0 && height > 0) {
            optSize = getOptimalSize(camera.getParameters().getSupportedPreviewSizes());
            parameters.setPreviewSize(optSize.width, optSize.height);
            previewView.setRatio(1.0f * optSize.width / optSize.height);
            camera.setDisplayOrientation(getSurfaceOrientation());
            stopPreview();
            try {
                camera.setParameters(parameters);
            } catch (RuntimeException e) {
                e.printStackTrace();
                CMLogUtils.e(TAG, e.getMessage());

            }
        }
    }

    private Camera.Size getOptimalSize(List<Camera.Size> sizes) {
        int width = previewView.textureView.getWidth();
        int height = previewView.textureView.getHeight();
        Camera.Size pictureSize = sizes.get(0);

        List<Camera.Size> candidates = new ArrayList<>();

        for (Camera.Size size : sizes) {
            if (size.width >= width && size.height >= height && size.width * height == size.height * width) {
                // 比例相同
                candidates.add(size);
            } else if (size.height >= width && size.width >= height && size.width * width == size.height * height) {
                // 反比例
                candidates.add(size);
            }
        }
        if (!candidates.isEmpty()) {
            return Collections.min(candidates, sizeComparator);
        }

        for (Camera.Size size : sizes) {
            if (size.width > width && size.height > height) {
                return size;
            }
        }

        return pictureSize;
    }

    private Comparator<Camera.Size> sizeComparator = new Comparator<Camera.Size>() {
        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            return Long.signum((long) lhs.width * lhs.height - (long) rhs.width * rhs.height);
        }
    };

    private void updateFlashMode(int flashMode) {
        if (parameters == null) {
            return;
        }
        switch (flashMode) {
            case FLASH_MODE_TORCH:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                break;
            case FLASH_MODE_OFF:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                break;
            case ICameraControl.FLASH_MODE_AUTO:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                break;
            default:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                break;
        }
        try {
            camera.setParameters(parameters);
        } catch (RuntimeException e) {
            e.printStackTrace();
            CMLogUtils.e(TAG, e.getMessage());
        }

    }

    private int getSurfaceOrientation() {
        @ICameraControl.Orientation
        int orientation = displayOrientation;
        switch (orientation) {
            case ICameraControl.ORIENTATION_PORTRAIT:
                return 90;
            case ICameraControl.ORIENTATION_HORIZONTAL:
                return 0;
            case ICameraControl.ORIENTATION_INVERT:
                return 180;
            default:
                return 90;
        }
    }

    /**
     * 有些相机匹配不到完美的比例。比如。我们的layout是4:3的。预览只有16:9
     * 的，如果直接显示图片会拉伸，变形。缩放的话，又有黑边。所以我们采取的策略
     * 是，等比例放大。这样预览的有一部分会超出屏幕。拍照后再进行裁剪处理。
     */
    private class PreviewView extends FrameLayout {

        private TextureView textureView;

        private float ratio = 0.75f;

        void setTextureView(TextureView textureView) {
            this.textureView = textureView;
            removeAllViews();
            addView(textureView);
        }

        void setRatio(float ratio) {
            this.ratio = ratio;
            requestLayout();
            relayout(getWidth(), getHeight());
        }

        public PreviewView(Context context) {
            super(context);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            relayout(w, h);
        }

        private void relayout(int w, int h) {
            int width = w;
            int height = h;
            if (w < h) {
                // 垂直模式，高度固定。
//                height = (int) (width * ratio);
                width = (int) (width * ratio);
            } else {
                // 水平模式，宽度固定。
//                width = (int) (height * ratio);
                height = (int) (height * ratio);
            }

            int l = (getWidth() - width) / 2;
            int t = (getHeight() - height) / 2;

            previewFrame.left = l;
            previewFrame.top = t;
            previewFrame.right = l + width;
            previewFrame.bottom = t + height;
            CMLogUtils.d(TAG, "Preview position:" + previewFrame.left + "-" + previewFrame.top + "-" + previewFrame.right + "-" + previewFrame.bottom);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            textureView.layout(previewFrame.left, previewFrame.top, previewFrame.right, previewFrame.bottom);
        }
    }

    @Override
    public Rect getPreviewFrame() {
        return previewFrame;
    }
}
