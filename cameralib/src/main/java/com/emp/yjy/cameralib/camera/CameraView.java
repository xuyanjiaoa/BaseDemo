package com.emp.yjy.cameralib.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.emp.yjy.cameralib.Utils.ImageUtils;
import com.emp.yjy.cameralib.Utils.CMLogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.emp.yjy.cameralib.camera.ICameraControl.DEFAULT_FOCUS_INTERVAL;


/**
 * 负责，相机的管理
 *
 * @author SZ02204
 */
public class CameraView extends FrameLayout {

    private final int MAX_IMAGE_SIZE = 2560;

    /**
     * UI线程的handler
     */
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    /**
     * 拍照
     */
    private AtomicBoolean mTakingPicture = new AtomicBoolean(false);

    /**
     * 照相回调
     */
    public interface OnTakePictureCallback {
        /**
         * 获取拍照图片
         *
         * @param data
         */
        void onPictureTaken(byte[] data);

        /**
         * 拍照并保存
         *
         * @param bitmap
         */
        void onPictureSave(File file, Bitmap bitmap);

        /**
         * 拍照出错
         *
         * @param errMsg
         */
        void error(String errMsg);
    }


    private CameraViewTakePictureCallback cameraViewTakePictureCallback = new CameraViewTakePictureCallback();

    private ICameraControl cameraControl;

    /**
     * 相机预览View
     */
    private View displayView;

    public ICameraControl getCameraControl() {
        return cameraControl;
    }

    public void setOrientation(@ICameraControl.Orientation int orientation) {
        cameraControl.setDisplayOrientation(orientation);
    }

    public CameraView(Context context) {
        super(context);
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void start() {
        cameraControl.start();
        setKeepScreenOn(true);
    }

    public void stop() {
        cameraControl.stop();
        setKeepScreenOn(false);
    }

    public void takePicture(final OnTakePictureCallback callback) {
        cameraViewTakePictureCallback.callback = callback;
        cameraControl.takePicture(cameraViewTakePictureCallback);
    }


    public void takePicture(File file, int quality, OnTakePictureCallback callback) {
        if (mTakingPicture.get()) {
            return;
        }
        mTakingPicture.set(true);
        cameraViewTakePictureCallback.mFile = file;
        cameraViewTakePictureCallback.callback = callback;
        cameraViewTakePictureCallback.mQuality = quality;
        cameraControl.takePicture(cameraViewTakePictureCallback);
    }


    private void init() {
        cameraControl = new Camera1Control(getContext());
        displayView = cameraControl.getDisplayView();
        addView(displayView);
    }

    public void setFlashMode(@ICameraControl.FlashMode int flashMode) {
        cameraControl.setFlashMode(flashMode);
    }


    public void autoFocus(boolean autoFocus, long interval) {
        long l = interval <= 0 ? DEFAULT_FOCUS_INTERVAL : interval;
        cameraControl.autoFocus(autoFocus, l);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        displayView.layout(left, 0, right, bottom - top);
    }

    private Bitmap crop(File outputFile, byte[] data, int rotation, int quality) throws Exception {
        Rect previewFrame = cameraControl.getPreviewFrame();

        if (previewFrame.width() == 0 || previewFrame.height() == 0) {
            return null;
        }

        // BitmapRegionDecoder不会将整个图片加载到内存。
        BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(data, 0, data.length, true);

        int width = rotation % 180 == 0 ? decoder.getWidth() : decoder.getHeight();
        int height = rotation % 180 == 0 ? decoder.getHeight() : decoder.getWidth();

        int left = width * previewFrame.left / previewFrame.width();
        int top = height * previewFrame.top / previewFrame.height();
        int right = width * previewFrame.right / previewFrame.width();
        int bottom = height * previewFrame.bottom / previewFrame.height();
        // 高度大于图片
        if (previewFrame.top < 0) {
            // 宽度对齐。
            int adjustedPreviewHeight = previewFrame.height() * getWidth() / previewFrame.width();
            int topInFrame = ((adjustedPreviewHeight - previewFrame.height()) / 2)
                    * getWidth() / previewFrame.width();
            int bottomInFrame = ((adjustedPreviewHeight + previewFrame.height()) / 2) * getWidth()
                    / previewFrame.width();

            // 等比例投射到照片当中。
            top = topInFrame * height / previewFrame.height();
            bottom = bottomInFrame * height / previewFrame.height();
        } else {
            // 宽度大于图片
            if (previewFrame.left < 0) {
                // 高度对齐
                int adjustedPreviewWidth = previewFrame.width() * getHeight() / previewFrame.height();
                int leftInFrame = ((adjustedPreviewWidth - previewFrame.width()) / 2) * getHeight()
                        / previewFrame.height();
                int rightInFrame = ((adjustedPreviewWidth + previewFrame.width()) / 2) * getHeight()
                        / previewFrame.height();

                // 等比例投射到照片当中。
                left = leftInFrame * width / previewFrame.width();
                right = rightInFrame * width / previewFrame.width();
            }
        }

        Rect region = new Rect();
        region.left = left;
        region.top = top;
        region.right = right;
        region.bottom = bottom;

        // 90度或者270度旋转
        if (rotation % 180 == 90) {
            int x = decoder.getWidth() / 2;
            int y = decoder.getHeight() / 2;

            int rotatedWidth = region.height();
            int rotated = region.width();

            // 计算，裁剪框旋转后的坐标
            region.left = x - rotatedWidth / 2;
            region.top = y - rotated / 2;
            region.right = x + rotatedWidth / 2;
            region.bottom = y + rotated / 2;
            region.sort();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();

        // 最大图片大小。
        int size = Math.min(decoder.getWidth(), decoder.getHeight());
        size = Math.min(size, MAX_IMAGE_SIZE);

        options.inSampleSize = ImageUtils.calculateInSampleSize(options, size, size);
        options.inScaled = true;
        options.inDensity = Math.max(options.outWidth, options.outHeight);
        options.inTargetDensity = size;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = decoder.decodeRegion(region, options);

        if (rotation != 0) {
            // 只能是裁剪完之后再旋转了。有没有别的更好的方案呢？
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedBitmap = Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            if (bitmap != rotatedBitmap) {
                // 有时候 createBitmap会复用对象
                bitmap.recycle();
            }
            bitmap = rotatedBitmap;
        }

        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        if (quality > 0 && quality < 100) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        }
        fileOutputStream.flush();
        fileOutputStream.close();
        return bitmap;

    }

    public void setPreviewCallBack(@NonNull ICameraControl.PreviewCallback callBack) {
        cameraControl.setPreviewCallback(callBack);
    }


    private class CameraViewTakePictureCallback implements ICameraControl.OnTakePictureCallback {
        private OnTakePictureCallback callback;
        private File mFile;
        private int mQuality = 100;

        @Override
        public void onPictureTaken(byte[] data) {
            if (mFile != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int orientation = ImageUtils.getOrientation(data);
                        Bitmap bitmap = null;
                        try {
                            bitmap = crop(mFile, data, orientation, mQuality);
                            Bitmap finalBitmap = bitmap;
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTakingPicture.set(false);
                                    callback.onPictureSave(mFile, finalBitmap);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.error(e.getMessage());
                                }
                            });
                        }


                    }
                }).start();
            } else {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onPictureTaken(data);
                    }
                });

            }
        }
    }

    public void setLog(boolean open) {
        if (open) {
            CMLogUtils.setLogLev(CMLogUtils.LEV_VERBOSE);
        } else {
            CMLogUtils.setLogLev(CMLogUtils.LEV_WTF);
        }

    }
}
