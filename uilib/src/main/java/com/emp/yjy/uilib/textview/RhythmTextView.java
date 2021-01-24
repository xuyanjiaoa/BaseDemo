package com.emp.yjy.uilib.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.emp.yjy.baselib.utils.LogUtils;
import com.emp.yjy.uilib.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author linruihang
 * @description: 律动的TextView
 * @date :2021/1/24 20:01
 */
public class RhythmTextView extends AppCompatTextView {
    private static final String TAG = "RhythmTextView";
    /**
     * 律动的字符
     */
    private String mRhythmText;
    /**
     * 律动间隔时间
     */
    private int mRhythmTextDuration;
    /**
     * 字符数组
     */
    private char[] mTextChars;

    /**
     * 律动开始/结束位置
     */
    private int mRhythmTextStartPos;
    private int mRhythmTextEndPos;
    /**
     * 画笔
     */
    private TextPaint mPaint;

    /**
     * 文字纵坐标
     */
    private float mTextY;

    /**
     * 当前绘制的最后一个字符的位置
     */
    private int mCurLastTextPos;


    /**
     * 文字文本个数
     */
    private int mTextSize;

    /**
     * 定时刷新
     */
    private Timer mTimer;
    /**
     * textView文本
     */
    private String mText;


    public RhythmTextView(@NonNull Context context) {
        this(context, null);
    }

    public RhythmTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RhythmTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        if (TextUtils.isEmpty(mRhythmText)) {
            return;
        }
        mText = getText().toString();
        if (!TextUtils.isEmpty(mText)) {
            if (!mText.contains(mRhythmText)) {
                return;
            }
            initPaint();

            mTextY = getTextSize() + getPaddingTop();
            mRhythmTextStartPos = mText.indexOf(mRhythmText);
            mRhythmTextEndPos = mRhythmTextStartPos + mRhythmText.length();
            mTextChars = mText.toCharArray();
            mTextSize = mTextChars.length;
            mCurLastTextPos = mRhythmTextStartPos;
            setText(mText.substring(0, mRhythmTextStartPos));
        }
    }

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RhythmTextView, defStyleAttr, 0);
        mRhythmText = typedArray.getString(R.styleable.RhythmTextView_rhythmText);
        mRhythmTextDuration = typedArray.getInt(R.styleable.RhythmTextView_rhythmDuration, 200);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTextChars == null) {
            return;
        }

        if (mCurLastTextPos > mRhythmTextEndPos) {
            mCurLastTextPos = mRhythmTextStartPos;
        }
        // TODO: 2021/1/24 暂时先考虑律动的文字只能在最后
        canvas.drawText(mTextChars, 0, mCurLastTextPos, 0, mTextY, mPaint);
//        canvas.drawText(mText.substring(0, mCurLastTextPos), 0, mCurLastTextPos, 0, mTextY, mPaint);
        mCurLastTextPos++;


    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = getPaint();
        mPaint.setColor(getCurrentTextColor());
//        mPaint.setAntiAlias(true);
//        mPaint.setTextSize(getTextSize());
    }


    /**
     * 开始律动
     */
    public void startRhythm() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LogUtils.d(TAG, "刷新控件");
                    invalidate();
                }
            }, 0, mRhythmTextDuration);
        }
    }

    /**
     * 停止律动
     */
    public void stopRhythm() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }


}
