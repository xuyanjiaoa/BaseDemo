package com.emp.yjy.baselib.net.tcp.server;

import android.os.SystemClock;

import com.emp.yjy.baselib.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @author linruihang
 * @description:sockect处理任务
 * @date :2020/12/26 20:08
 */
class SocketHandleRunnable implements Runnable {
    private static final String TAG = "SocketHandleRunnable";
    //默认缓存大小为1M
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;
    //缓存
    private byte[] mBuffer;
    //缓存大小
    private int mBufferSize = DEFAULT_BUFFER_SIZE;
    //默认连接维持时间(两分钟)
    private static final long DEFAULT_SURVIVAL_TIME = 120 * 1000;
    //存活时间
    private long mSurvivalTime = DEFAULT_SURVIVAL_TIME;
    //是否存活
    private boolean isAlive;
    //socket
    private Socket mSocket;
    //监听器
    private AcceptMsgCallBack mAcceptCallBack;
    //socket id
    private int mid;
    //最后一次接收数据的时间
    private long mLastAcceptDataTime;

    public SocketHandleRunnable(boolean isAlive, Socket socket, int id, AcceptMsgCallBack callBack) {
        this.isAlive = isAlive;
        mSocket = socket;
        mAcceptCallBack = callBack;
        mid = id;
        mBuffer = new byte[mBufferSize];
    }

    @Override
    public void run() {
        mLastAcceptDataTime = System.currentTimeMillis();
        while (isAlive) {
            try {
                if (System.currentTimeMillis() - mLastAcceptDataTime > mSurvivalTime) {
                    break;
                }
                InputStream inputStream = mSocket.getInputStream();
                if (inputStream == null) {
                    break;
                }
                if (inputStream.available() > 0) {
                    mLastAcceptDataTime = System.currentTimeMillis();
                    int readNum = inputStream.read(mBuffer);
                    if (readNum > 0) {
                        byte[] data = new byte[readNum];
                        System.arraycopy(mBuffer, 0, data, 0, readNum);

                        if (mAcceptCallBack != null) {
                            mAcceptCallBack.accept(mid, data);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (mAcceptCallBack != null) {
                    mAcceptCallBack.error(e.getMessage());
                }
            }
            SystemClock.sleep(1);
        }
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtils.i(TAG, "SocketHandleRunnable exit!");
        if (mAcceptCallBack != null) {
            mAcceptCallBack.exit(mid);
        }

    }

    public void exit() {
        isAlive = false;
    }

    public long getSurvivalTime() {
        return mSurvivalTime;
    }

    public void setSurvivalTime(long survivalTime) {
        mSurvivalTime = survivalTime;
    }

    public interface AcceptMsgCallBack {
        /**
         * 接收到数据
         *
         * @param data
         */
        void accept(int id, byte[] data);

        /**
         * 错误
         *
         * @param errMsg
         */
        void error(String errMsg);

        /**
         * 退出
         *
         * @param id
         */
        void exit(int id);

    }
}

