package com.emp.yjy.basedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.emp.yjy.basedemo.base.CusBaseActivity;
import com.emp.yjy.baselib.base.BaseActivity;
import com.emp.yjy.baselib.media.SoundPlayer;

/**
 * 语音播放演示界面
 */
public class SoundPlayDemoActivity extends CusBaseActivity {
    private SoundPlayer mSoundPlayer;


    @Override
    protected void initData() {
        //初始化方式一
        mSoundPlayer = new SoundPlayer(this);
        mSoundPlayer.loadSoundById(R.raw.look_camera);
        mSoundPlayer.setVolume(0.5f).setRate(1.0f).setSoundPriority(1);

        //初始化方式二
//        mSoundPlayer = new SoundPlayer(this, 16, 0);
//        mSoundPlayer.loadSoundById(R.raw.look_camera);

    }

    @Override
    protected int layoutId() {
        return R.layout.activity_sound_play_demo;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //释放资源
        if (mSoundPlayer != null) {
            mSoundPlayer.release();
        }
    }

    public void playSound(View view) {
        //播放声音
        if (mSoundPlayer != null) {
            mSoundPlayer.play(R.raw.look_camera);
        }
    }
}