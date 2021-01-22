package com.emp.yjy.baselib.media.audio;

import android.media.AudioFormat;
import android.media.MediaRecorder;

import androidx.annotation.IntDef;

/**
 * @author Created by LRH
 * @description 音频录制参数信息
 * @date 2021/1/20 9:44
 */
public class AudioRecordConfig {
    public static final int AUDIO_FILE_TYPE_WAV = 0;
    public static final int AUDIO_FILE_TYPE_PCM = 1;

    @IntDef({AUDIO_FILE_TYPE_WAV,AUDIO_FILE_TYPE_PCM})
    public @interface AudioFileType{

    }

    //音频采集的输入源
    private int audioSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
    //目前44100Hz是唯一可以保证兼容所有Android手机的采样率
    private int sampleRateInHz = 44100;
    //通道数(双通道)
    private int channel = AudioFormat.CHANNEL_IN_MONO;
    //
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    //录制时长
    private long recordTime;
    //
    @AudioFileType
    private int audioFileType = AUDIO_FILE_TYPE_WAV;

    public int getAudioFileType() {
        return audioFileType;
    }

    public void setAudioFileType(@AudioFileType int audioFileType) {
        this.audioFileType = audioFileType;
    }

    public AudioRecordConfig() {
    }

    public int getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(int audioSource) {
        this.audioSource = audioSource;
    }

    public int getSampleRateInHz() {
        return sampleRateInHz;
    }

    public void setSampleRateInHz(int sampleRateInHz) {
        this.sampleRateInHz = sampleRateInHz;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getAudioEncoding() {
        return audioEncoding;
    }

    public void setAudioEncoding(int audioEncoding) {
        this.audioEncoding = audioEncoding;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    /**
     * 当前的声道数
     *
     * @return 声道数： 0：error
     */
    public int getChannelCount() {
        if (channel == AudioFormat.CHANNEL_IN_MONO) {
            return 1;
        } else if (channel == AudioFormat.CHANNEL_IN_STEREO) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * 获取当前录音的采样位宽 单位bit
     *
     * @return 采样位宽 0: error
     */
    public int getEncodingBitLen() {
        if (audioEncoding == AudioFormat.ENCODING_PCM_8BIT) {
            return 8;
        } else if (audioEncoding == AudioFormat.ENCODING_PCM_16BIT) {
            return 16;
        } else {
            return 0;
        }
    }
}
