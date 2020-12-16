package com.emp.yjy.baselib.multidisplay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Created by LRH
 * @date 2020/11/18 11:32
 * 双屏异显
 */
public class MultiDisplay extends Presentation {
    private Activity mActivity;
    private MultiDisplayStatus mStatus = MultiDisplayStatus.DISMISS;

    public int getDisplayMode() {
        return mDisplayMode;
    }

    private int mDisplayMode;

    public void setDisplayMode(DisplayMode displayMode) {
        this.mDisplayMode = displayMode.getMode();
        this.getWindow().setType(displayMode.getMode());
    }

    public MultiDisplay(Activity activity, Display display) {
        super(activity, display);
        this.mActivity = activity;
    }

    @SuppressLint("WrongConstant")
    public static Display getDisplay(Context context, DisplayType displayType) {
        Display[] displays = ((DisplayManager) context.getSystemService("display")).getDisplays();
        if (displays != null && displays.length > displayType.getType()) {
            return displays[displayType.getType()];
        }
        return null;
    }

    @Override
    public void hide() {
        super.hide();
        onHide();
        mStatus = MultiDisplayStatus.HIDE;
    }

    @Override
    public void show() {
        super.show();
        onShow();
        mStatus = MultiDisplayStatus.SHOW;
    }

//    @Override
//    public void cancel() {
//        super.cancel();
//        onCancel();
//    }


    @Override
    public void dismiss() {
        super.dismiss();
        onDismiss();
        mStatus = MultiDisplayStatus.DISMISS;
    }

    protected void onDismiss() {

    }

    protected void onShow() {

    }


    protected void onHide() {

    }

    /**
     * 获取ActivityDisplay当前显示状态
     *
     * @return
     */
    public MultiDisplayStatus getStatus() {
        return mStatus;
    }


}
