package com.emp.yjy.basedemo;

import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.emp.yjy.basedemo.base.CusBaseActivity;
import com.emp.yjy.baselib.base.BaseActivity;
import com.emp.yjy.baselib.utils.LogUtils;
import com.emp.yjy.baselib.utils.ScreenUtils;
import com.emp.yjy.uilib.scan.CameraLensView;

/**
 * 使用示例
 *
 * @author SZ02204
 */
public class CameraLensViewDemoActivity extends CusBaseActivity implements View.OnClickListener {
    private static final String TAG = "CameraLensViewDemoActiv";
    private CameraLensView mCameraLensView;
    private ImageView mBgImage;


    @Override
    protected void initData() {

    }

    @Override
    protected int layoutId() {
        return R.layout.activity_cameea_lens_view_demo;
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView() {
        mCameraLensView = findViewById(R.id.camera_lens_view);
        mBgImage = findViewById(R.id.iv_background);
        int appViewWidth = ScreenUtils.getAppViewWidth(this);
        //设置中间区域高度和宽度
        mCameraLensView.setCameraLensSize((int) (appViewWidth * 0.6), (int) (appViewWidth * 0.6));
        mCameraLensView.setInitCameraLensCallBack(new CameraLensView.OnInitCameraLensCallBack() {
            @Override
            public void onFinishInitialize(@NonNull Rect rect) {
                LogUtils.d(TAG, "rect:" + rect.left + "-" + rect.bottom + "-" + rect.right + "-" + rect.top);
            }
        });
    }

    @Override
    public void onClick(View v) {
        mBgImage.setOnClickListener(this::onClick);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}