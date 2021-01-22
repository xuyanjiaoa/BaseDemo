package com.emp.yjy.basedemo.activity;

import android.widget.Button;

import com.emp.yjy.basedemo.R;
import com.emp.yjy.basedemo.base.CusBaseActivity;
import com.emp.yjy.uilib.view.SignatureView;

/**
 * 签名控件使用demo界面
 * @author SZ02204
 */
public class SignatureViewDemoActivity extends CusBaseActivity {
    private SignatureView mSignatureView;
    private Button mBtnClearSign;


    @Override
    protected void initData() {

    }

    @Override
    protected int layoutId() {
        return R.layout.activity_signature_view_demo;
    }

    @Override
    protected void initView() {
        mSignatureView = findViewById(R.id.sign_view);
        mBtnClearSign = findViewById(R.id.btn_clear_sign);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBtnClearSign.setOnClickListener(v -> {
            mSignatureView.clear();
        });
    }
}