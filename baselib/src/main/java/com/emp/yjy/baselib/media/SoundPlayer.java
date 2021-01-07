package com.emp.yjy.baselib.media;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

/**
 * 语音播放
 *
 * @author Created by LRH
 * @date 2020/12/16 10:07
 */
public final class SoundPlayer {
    private Context mContext;
    private SoundPool mSoundPool;
    //soundID map
    private SparseIntArray mSoundIdMap = new SparseIntArray();
    //maxStreams 同时播放流的最大数量，当播放的流的数目大于此值，则会选择性停止优先级较低的流
    private static final int DEFAULT_MAX_STREAM = 16;
    //srcQuality 采样率转换器质量,目前没有什么作用,默认填充0
    private static final int DEFAULT_SRC_QUALITY = 0;
    //默认音量（范围：0.0-1.0）
    private static final float DEFAULT_VOLUME = 0.5f;
    //默认重复播放次数(0:不重复,-1:一直重复,2:重复播放两次)
    private static final int DEFAULT_PLAY_LOOP = 0;
    //默认播放速率（范围：0.5-2.0）
    private static final float DEFAULT_PLAY_RATE = 1.0f;
    //默认播放质量（0：低质量  1：高质量）
    private static final int DEFAULT_SOUND_PRIORITY = 1;

    //属性
    //音量（范围：0.0-1.0），值越大声音越大
    private float mVolume = DEFAULT_VOLUME;
    //声音质量（0/1）
    private int mSoundPriority = DEFAULT_SOUND_PRIORITY;
    //播放速率（0.5-2.0），越小播放速度越快
    private float mRate = DEFAULT_PLAY_RATE;
    //最后流id
    private int mLastStreamId = 0;


    public SoundPlayer(Context context) {
        this(context, DEFAULT_MAX_STREAM, DEFAULT_SRC_QUALITY);
    }

    public SoundPlayer(Context context, int maxStream, int srcQuality) {
        if (context == null) {
            throw new NullPointerException("context can not be null");
        }
        if (maxStream < 0) {
            throw new IllegalStateException("param maxStream can not smaller than zero!");
        }
        if (srcQuality < 0) {
            throw new IllegalStateException("param srcQuality can noe smaller than zero!");
        }
        createSoundPool(maxStream, srcQuality);
        this.mContext = context;
    }

    /**
     * 加载资源语音
     *
     * @param resId
     * @return
     */
    public int loadSoundById(int resId) {
        //第三个参数暂时无实际意义
        //返回值soundId为语音唯一id,可用于播放/暂停语音等操作
        int soundId = mSoundPool.load(mContext, resId, 1);
        mSoundIdMap.put(resId, soundId);
        return soundId;
    }


    /**
     * 播放声音
     *
     * @param resId 声音资源id
     * @return true:播放成功
     * false：播放失败
     */
    public int play(int resId) {
        int soundId = mSoundIdMap.get(resId);
        int playRet = mSoundPool.play(soundId, mVolume, mVolume, mSoundPriority, DEFAULT_PLAY_LOOP, mRate);
        return playRet;
    }

    /**
     * 停止播放声音
     *
     * @param soundId
     */
    public void stop(int soundId) {

        mSoundPool.stop(soundId);
    }


    /**
     * 资源释放
     *
     * @param resId 声音资源id
     * @return
     */
    public boolean unloadRes(int resId) {
        return mSoundPool.unload(mSoundIdMap.get(resId));
    }


    /**
     * 释放资源
     */
    public void release() {
        mSoundPool.release();
        mSoundIdMap.clear();
        mSoundPool = null;
        mSoundIdMap = null;
    }


    /**
     * 创建soundPool,注意api等级
     *
     * @param maxStream  同时播放流的最大数量
     * @param srcQuality 采样率转换器质量
     */
    private void createSoundPool(int maxStream, int srcQuality) {
        if (mSoundPool == null) {
            // 5.0 及 之后
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes;
                audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();

                mSoundPool = new SoundPool.Builder()
                        .setMaxStreams(maxStream)
                        .setAudioAttributes(audioAttributes)
                        .build();
            } else {
                // 5.0 以前
                mSoundPool = new SoundPool(maxStream, AudioManager.STREAM_MUSIC, srcQuality);  // 创建SoundPool
            }
        }
    }

    /**
     * 音量设置
     *
     * @param volume 音量大小（范围：0.0-1.0），其他值无效
     * @return
     */
    public SoundPlayer setVolume(float volume) {
        if (volume > 0.0f && volume <= 1.0f) {
            mVolume = volume;
        }
        return this;
    }

    /**
     * 声音播放质量设置
     *
     * @param soundPriority 声音质量，传0或者1，其他无效
     * @return
     */
    public SoundPlayer setSoundPriority(int soundPriority) {
        if (soundPriority == 0 || soundPriority == 1) {
            mSoundPriority = soundPriority;
        }
        return this;
    }

    /**
     * 播放速率设置
     *
     * @param rate 播放速率，范围0.5-2.0，其他值无效
     * @return
     */
    public SoundPlayer setRate(float rate) {
        if (rate >= 0.5f && rate <= 2.0f) {
            mRate = rate;
        }
        return this;
    }


}
