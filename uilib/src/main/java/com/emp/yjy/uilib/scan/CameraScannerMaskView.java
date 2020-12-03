package com.emp.yjy.uilib.scan;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 遮罩+扫描组合控件 可用于二维码扫描场景/人脸识别场景
 * @author SZ02204
 * @date 2020/12/03
 */
public class CameraScannerMaskView extends FrameLayout {

    private final String TAG = "CameraScannerMas";
    private ScannerBarView mScannerBarView;
    private CameraLensView mCameraLensView;

    public CameraScannerMaskView(Context context) {
        this(context, null);
    }

    public CameraScannerMaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraScannerMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCameraLensView = new CameraLensView(context);
        mCameraLensView.init(context, attrs, defStyleAttr);
        mScannerBarView = new ScannerBarView(context);
        mScannerBarView.initAttr(context, attrs, defStyleAttr);
        addView(mScannerBarView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mCameraLensView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        reLocationScannerBarView(mCameraLensView.getCameraLensRect());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void reLocationScannerBarView(Rect rect) {
        MarginLayoutParams params = (MarginLayoutParams) mScannerBarView.getLayoutParams();
        params.width = rect.width();
        params.height = rect.width();
        params.leftMargin = rect.left;
        params.topMargin = rect.top;
        mScannerBarView.setLayoutParams(params);
    }

    public void start() {
        mScannerBarView.start();
    }

    public void pause() {
        mScannerBarView.pause();
    }

    public void resume() {
        mScannerBarView.resume();
    }

    public void stop() {
        mScannerBarView.stop();
    }

    public CameraLensView getCameraLensView() {
        return mCameraLensView;
    }
}
