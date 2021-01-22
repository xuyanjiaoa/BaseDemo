package com.emp.yjy.baselib.media.video;

import android.media.MediaRecorder;
import android.os.Build;
import android.view.Surface;

import com.emp.yjy.baselib.utils.LogUtils;

import java.io.IOException;

/**
 * @author Created by LRH
 * @description
 * @date 2021/1/19 13:53
 */
public class VideoRecorder {
    private static final String TAG = "VideoRecord";
    private MediaRecorder mRecorder;

    public VideoRecorder() {

    }

    /**
     * 开始录制
     *
     * @param config
     * @return
     */
    public boolean startRecord(VideoRecorderConfig config, MediaRecorder.OnErrorListener listener) {
        if (config == null || !config.checkParam()) {
            LogUtils.e(TAG, "参数错误");
            return false;
        }
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        mRecorder.reset();
        if (listener != null) {
            mRecorder.setOnErrorListener(listener);
        }

        config.getCamera().unlock();
        mRecorder.setCamera(config.getCamera());

        //设置预览对象
        mRecorder.setPreviewDisplay(new Surface(config.getSurfaceTexture()));
        //声音源
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //视频源
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //设置输出格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        //声音编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //视频编码格式
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置视频的长宽
        mRecorder.setVideoSize(config.getVideoWidth(), config.getVideoHeight());
        //设置取样帧率
        mRecorder.setVideoFrameRate(30);
        //设置比特率（比特率越高质量越高同样也越大）
        mRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
        //这里是调整旋转角度（前置和后置的角度不一样）
        mRecorder.setOrientationHint(config.getCameraRotation());
        //设置记录会话的最大持续时间（毫秒）
//        mRecorder.setMaxDuration(15 * 1000);

        //设置输出的文件路径
        mRecorder.setOutputFile(config.getPath());
        //预处理
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        //开始录制
        mRecorder.start();
        return true;
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
    }

    /**
     * 暂停录制
     *
     * @return
     */
    public boolean pause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mRecorder != null) {
            mRecorder.pause();
            return true;
        }
        return false;
    }

    /**
     * 继续录制
     *
     * @return
     */
    public boolean resume() {
        if (mRecorder != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mRecorder.resume();
            return true;
        }
        return false;
    }
}
