package com.emp.yjy.uilib.scan;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.emp.yjy.uilib.R;

/**
 * 扫描控件
 *
 * @author Created by LRH
 * @date 2020/11/25 14:13
 */
public class ScannerBarView extends ViewGroup {
    //默认扫描周期
    private static final int DEFAULT_SCAN_PERIOD = 2_000;
    //扫描条资源
    private ImageView mScanBar;
    //扫描动画
    private ObjectAnimator mAnimator = null;
    //扫描周期
    private int mScanPeriod;

    private int mBarHeight;


    public ScannerBarView(Context context) {
        this(context, null);
    }

    public ScannerBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScannerBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttr(context, attrs, defStyleAttr);
    }

    /**
     * 获取属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    void initAttr(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScannerBarView, defStyleAttr, 0);
        int scanBarResId = a.getResourceId(R.styleable.ScannerBarView_scan_bar, R.drawable.default_scanner_bar);
        mScanBar.setImageResource(scanBarResId);
        mScanPeriod = a.getInt(R.styleable.ScannerBarView_scan_period, DEFAULT_SCAN_PERIOD);
        a.recycle();
    }


    /**
     * 初始化view
     *
     * @param context
     */
    private void initView(Context context) {
        mScanBar = new ImageView(context);
        mScanBar.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(mScanBar, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }


    /**
     * 设置扫描bar图片
     *
     * @param drawable
     */
    public void setScannerBarImageResource(@DrawableRes int drawable) {
        mScanBar.setImageResource(drawable);
        executeRequestLayout();
    }

    /**
     * 重新计算布局大小
     */
    private void executeRequestLayout() {
        boolean needRestart = isScan();
        stop();
        requestLayout();
        if (needRestart) {
            start();
        }
    }


    /**
     * 扫描动画是否正在进行
     *
     * @return
     */
    public boolean isScan() {
        return mAnimator != null && mAnimator.isRunning();
    }

    /**
     * 停止扫描动画
     */
    public void stop() {
        mScanBar.setTranslationY(0);
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }


    /**
     * 开始扫描动画
     */
    public void start() {
        if (mAnimator == null) {
            //获取扫描bar高度（不能直接用getHeight（）可能还未绘制完成，导致获取到的高度为0）
            mBarHeight = ((BitmapDrawable) mScanBar.getDrawable()).getBitmap().getHeight();
            mAnimator = ObjectAnimator.ofFloat(mScanBar, View.TRANSLATION_Y, 0, mBarHeight + getHeight());
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.setDuration(mScanPeriod);
            mAnimator.setRepeatCount(Animation.INFINITE);
            mAnimator.start();
        }
    }

    /**
     * 暂停扫描动画
     */
    public void pause() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.pause();
        }
    }

    /**
     * 重新开始扫描动画
     */
    public void resume() {
        if (mAnimator != null && mAnimator.isPaused()) {
            mAnimator.resume();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.layout(0, 0 - child.getMeasuredHeight(), child.getMeasuredWidth(), 0);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        executeRequestLayout();
    }
}
