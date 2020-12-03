package com.emp.yjy.basedemo;

import android.widget.RadioGroup;

import com.emp.yjy.baselib.base.BaseActivity;
import com.emp.yjy.baselib.utils.DensityUtils;
import com.emp.yjy.baselib.utils.ScreenUtils;
import com.emp.yjy.uilib.scan.CameraLensView;
import com.emp.yjy.uilib.scan.CameraScannerMaskView;

/**
 * 扫描空间示例
 *
 * @author Created by LRH
 * @date 2020/12/3 9:55
 */
public class ScanViewDemoActivity extends BaseActivity {
    private CameraScannerMaskView mCameraScannerMaskView;
    private RadioGroup mRadioGroup;

    @Override
    protected void initData() {

    }

    @Override
    protected int layoutId() {
        return R.layout.activity_scan_view;
    }

    @Override
    protected void initView() {
        mCameraScannerMaskView = findViewById(R.id.scan_view);
        mRadioGroup = findViewById(R.id.radio_group);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int appViewWidth = ScreenUtils.getAppViewWidth(ScanViewDemoActivity.this);
                switch (checkedId) {
                    case R.id.rb_qrcode:
                        mCameraScannerMaskView.getCameraLensView().setCameraLensShape(CameraLensView.RECTANGLE);
                        mCameraScannerMaskView.getCameraLensView().setCameraLensSize(appViewWidth * 3 >> 2, appViewWidth * 3 >> 2);
                        mCameraScannerMaskView.getCameraLensView().setMaskColor(0x99FFFFFF);
                        mCameraScannerMaskView.getCameraLensView().setShowBoxAngle(true);
                        break;
                    case R.id.rb_face:
                        mCameraScannerMaskView.getCameraLensView().setCameraLensShape(CameraLensView.CIRCULAR);
                        mCameraScannerMaskView.getCameraLensView().setCameraLensSize(appViewWidth * 2 / 5,appViewWidth * 2 / 5);
                        mCameraScannerMaskView.getCameraLensView().setMaskColor(getResources().getColor(R.color.white));
                        mCameraScannerMaskView.getCameraLensView().setShowBoxAngle(false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraScannerMaskView.start();
    }
}
