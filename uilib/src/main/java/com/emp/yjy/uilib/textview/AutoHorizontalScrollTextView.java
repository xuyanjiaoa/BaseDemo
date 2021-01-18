package com.emp.yjy.uilib.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author linruihang
 * @description: 横向来回滑动TextView
 * @date :2021/1/15 20:55
 */
public class AutoHorizontalScrollTextView extends AppCompatTextView implements View.OnClickListener {
    public final static String TAG = AutoHorizontalScrollTextView.class.getSimpleName();
    private float textLength = 0f;//文本长度
    private float viewWidth = 0f;
    private float step = 0f;//文字的横坐标
    private float y = 0f;//文字的纵坐标
    public boolean isStarting = false;//是否开始滚动
    private Paint paint = null;//绘图样式
    private String text = "";//文本内容
    private float mStepwidth = 0.1f;

    public AutoHorizontalScrollTextView(Context context) {
        super(context);
        initView();
    }

    public AutoHorizontalScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AutoHorizontalScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        setOnClickListener(this);
        init();
    }

    public void init() {
        paint = getPaint();
        paint.setColor(getCurrentTextColor());
        text = getText().toString();
        textLength = paint.measureText(text);
        viewWidth = getWidth();
        y = getTextSize() + getPaddingTop();
        startScroll();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.step = step;
        ss.isStarting = isStarting;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        step = ss.step;
        isStarting = ss.isStarting;

    }

    public static class SavedState extends BaseSavedState {
        public boolean isStarting = false;
        public float step = 0.0f;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBooleanArray(new boolean[]{isStarting});
            out.writeFloat(step);
        }


        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
        };

        private SavedState(Parcel in) {
            super(in);
            boolean[] b = null;
            in.readBooleanArray(b);
            if (b != null && b.length > 0) {
                isStarting = b[0];
            }
            step = in.readFloat();
        }
    }


    private void startScroll() {
        //如果view宽度小于文字宽度，开启自动滚动
        if (viewWidth < textLength) {
            isStarting = true;
            mStepwidth = 0.2f * textLength / viewWidth;
            if (mStepwidth > 0.5f) {
                mStepwidth = 0.5f;
            }
            invalidate();
        }
    }


    private void stopScroll() {
        isStarting = false;
        invalidate();
    }


    boolean leftSlideflag = false;

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawText(text, step, y, paint);
        if (!isStarting) {
            return;
        }
        if (leftSlideflag) {
            step -= mStepwidth;
        } else {
            step += mStepwidth;
        }
        if (step >= 0) {
            leftSlideflag = true;
        }

        if (step <= viewWidth - textLength) {
            leftSlideflag = false;
        }

        invalidate();
    }


    /**
     * 设置滚动文字内容
     *
     * @param text
     */
    public void setScrollText(String text) {
        if (!TextUtils.isEmpty(text)) {
            setText(text);
            init();
            startScroll();
        }
    }


    @Override
    public void onClick(View v) {
//        if (isStarting) {
//            stopScroll();
//        } else {
//            startScroll();
//        }
    }
}

