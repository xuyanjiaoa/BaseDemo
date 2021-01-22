package com.emp.yjy.baselib.media.audio;

import android.media.AudioRecord;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.emp.yjy.baselib.base.Result;
import com.emp.yjy.baselib.utils.LogUtils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Created by LRH
 * @description 音频录制
 * @date 2021/1/20 9:06
 */
public class AudioRecorder {
    private static final String TAG = "AudioRecorder";
    private RecordRunnable mRecordRunnable;


    public Result<Object> startRecord(String savePath, AudioRecordConfig config, RecordListener listener) {
        Result<Object> result = new Result();
        if (config == null || TextUtils.isEmpty(savePath)) {
            result.setCode(-1);
            result.setMsg("参数错误");
            return result;
        }
        mRecordRunnable = new RecordRunnable(config, savePath, listener);
        new Thread(mRecordRunnable).start();
        result.setCode(0);
        result.setMsg("成功");
        return result;
    }

    public void stopRecord() {
        if (mRecordRunnable != null) {
            mRecordRunnable.exit();
        }
    }


    private static class RecordRunnable implements Runnable {
        private Handler mHandler = new Handler(Looper.getMainLooper());
        private AudioRecordConfig mConfig;
        private String mSaveFilePath;
        private boolean isExit;
        private RecordListener mListener;

        public RecordRunnable(AudioRecordConfig audioRecordConfig, String saveFilePath, RecordListener listener) {
            mConfig = audioRecordConfig;
            mSaveFilePath = saveFilePath;
            mListener = listener;
        }

        /**
         * 加入wav文件头
         */
        private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                         long totalDataLen, long longSampleRate, int channels, long byteRate)
                throws IOException {
            byte[] header = new byte[44];
            // RIFF/WAVE header
            header[0] = 'R';
            header[1] = 'I';
            header[2] = 'F';
            header[3] = 'F';
            header[4] = (byte) (totalDataLen & 0xff);
            header[5] = (byte) ((totalDataLen >> 8) & 0xff);
            header[6] = (byte) ((totalDataLen >> 16) & 0xff);
            header[7] = (byte) ((totalDataLen >> 24) & 0xff);
            //WAVE
            header[8] = 'W';
            header[9] = 'A';
            header[10] = 'V';
            header[11] = 'E';
            // 'fmt ' chunk
            header[12] = 'f';
            header[13] = 'm';
            header[14] = 't';
            header[15] = ' ';
            // 4 bytes: size of 'fmt ' chunk
            header[16] = 16;
            header[17] = 0;
            header[18] = 0;
            header[19] = 0;
            // format = 1
            header[20] = 1;
            header[21] = 0;
            header[22] = (byte) channels;
            header[23] = 0;
            header[24] = (byte) (longSampleRate & 0xff);
            header[25] = (byte) ((longSampleRate >> 8) & 0xff);
            header[26] = (byte) ((longSampleRate >> 16) & 0xff);
            header[27] = (byte) ((longSampleRate >> 24) & 0xff);
            header[28] = (byte) (byteRate & 0xff);
            header[29] = (byte) ((byteRate >> 8) & 0xff);
            header[30] = (byte) ((byteRate >> 16) & 0xff);
            header[31] = (byte) ((byteRate >> 24) & 0xff);
            // block align
            header[32] = (byte) (2 * 16 / 8);
            header[33] = 0;
            // bits per sample
            header[34] = 16;
            header[35] = 0;
            //data
            header[36] = 'd';
            header[37] = 'a';
            header[38] = 't';
            header[39] = 'a';
            header[40] = (byte) (totalAudioLen & 0xff);
            header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
            header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
            header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
            out.write(header, 0, 44);
        }


        @Override
        public void run() {
            isExit = false;
            try {
                //检查文件时候存在
                File file = new File(mSaveFilePath);
                if (!file.exists()) {
                    if (file.getParentFile() != null && !file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    } else {
                        file.createNewFile();
                    }
                } else {
                    file.delete();
                }

                // 开通输出流到指定的文件
                DataOutputStream dos = new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(mSaveFilePath)));
                //计算缓冲区大小
                int bufferSize = AudioRecord.getMinBufferSize(mConfig.getSampleRateInHz(),
                        mConfig.getChannel(), mConfig.getAudioEncoding());

                //实例化AudioRecord
                AudioRecord record = new AudioRecord(
                        mConfig.getAudioSource(), mConfig.getSampleRateInHz(),
                        mConfig.getChannel(), mConfig.getAudioEncoding(), bufferSize);
                // 定义缓冲
                short[] buffer = new short[bufferSize];
                // 开始录制
                record.startRecording();
                if (mListener != null) {
                    mHandler.post(() -> mListener.start());
                }

                //定义录制进度
                double progress = 0;
                long startTime = System.currentTimeMillis();
                long recordTime = 0;
                while (!isExit && recordTime < mConfig.getRecordTime()) {
                    // 从bufferSize中读取字节，返回读取的short个数
                    int bufferReadResult = record
                            .read(buffer, 0, buffer.length);
                    // 循环将buffer中的音频数据写入到OutputStream中
                    for (int i = 0; i < bufferReadResult; i++) {
                        //大小端问题，不能直接写入，否则全是杂音
                        dos.writeShort(Short.reverseBytes(buffer[i]));
                    }
                    recordTime = System.currentTimeMillis() - startTime;
                    progress = (double) recordTime / mConfig.getRecordTime();
                    if (mListener != null) {
                        double finalProgress = progress;
                        mHandler.post(() -> {
                            mListener.progress(finalProgress);
                        });
                    }
                }
                // 录制结束
                record.stop();
                Log.i("slack", "::" + file.length());
                dos.close();
                if (mConfig.getAudioFileType() == AudioRecordConfig.AUDIO_FILE_TYPE_WAV) {
                    byte[] header = WavUtils.generateWavFileHeader((int) file.length(), mConfig.getSampleRateInHz(), mConfig.getChannelCount(), mConfig.getEncodingBitLen());
                    WavUtils.writeHeader(file, header);
                }
                if (mListener != null) {
                    mHandler.post(() -> mListener.finish());
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (mListener != null) {
                    mHandler.post(() -> {
                        mListener.error(e.getMessage());
                    });
                }
            }


        }

        public void exit() {
            isExit = true;
        }
    }


    public interface RecordListener {
        /**
         * 开始录制
         */
        void start();

        /**
         * 录制进度
         *
         * @param progress
         */
        void progress(double progress);


        /**
         * 录制结束
         */
        void finish();

        /**
         * 录制出错
         */
        void error(String errMsg);

    }


    /***********************测试代码****************************/
    /**
     * 音频录制测试代码
     */
    private void recordAudio() {
        AudioRecorder audioRecorder = new AudioRecorder();
        String path = "test.wav";
        AudioRecordConfig config = new AudioRecordConfig();
        config.setRecordTime(15_000);
        config.setAudioFileType(AudioRecordConfig.AUDIO_FILE_TYPE_WAV);
        audioRecorder.startRecord(path, config, new RecordListener() {
            @Override
            public void start() {
                LogUtils.e(TAG, "开始录制音频");
            }

            @Override
            public void progress(double progress) {
                LogUtils.d(TAG, "录制音频进度：" + progress);
            }

            @Override
            public void finish() {
                LogUtils.e(TAG, "录制音频结束");
            }

            @Override
            public void error(String errMsg) {
                LogUtils.e(TAG, "录制音频出错：" + errMsg);
            }
        });
    }
}
