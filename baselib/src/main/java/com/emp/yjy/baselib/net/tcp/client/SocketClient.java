package com.emp.yjy.baselib.net.tcp.client;

import android.text.TextUtils;

import com.emp.yjy.baselib.base.Result;
import com.emp.yjy.baselib.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *SocketClient封装
 * @author Created by LRH
 * @date 2020/12/25 15:00
 */
public class SocketClient {
    private static final String TAG = "SocketClient";
    private static final int DEFAULT_BUFFER_SIZE = 2 * 1024 * 1024;
    private static final String DEFAULT_IP = "127.0.0.1";
    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_TIME_OUT = 10_000;
    private Socket mSocket;
    private String mIp;
    private int mPort;
    private int mConnectTimeOut = DEFAULT_TIME_OUT;
    private int mBufferSize = DEFAULT_BUFFER_SIZE;
    private byte[] mBuffer;
    private int mRevTimeOut = DEFAULT_TIME_OUT;


    /**
     * 初始化
     *
     * @param ip
     * @param port
     */
    public SocketClient(String ip, int port) {
        if (TextUtils.isEmpty(ip)) {
            throw new IllegalStateException("ip address not to be empty!");
        }
        if (port < 0) {
            throw new IllegalStateException("port must more than 0！");
        }
        mIp = ip;
        mPort = port;
    }

    /**
     * 连接
     *
     * @return
     */
    public Result<Object> connect() {
        if (mSocket == null) {
            mSocket = new Socket();
        }
        Result<Object> result = new Result<>();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(this.mIp, this.mPort);
        try {
            mSocket.connect(inetSocketAddress, mConnectTimeOut);
            // 防止服务端无效时，长时间处于连接状态
            mSocket.setTcpNoDelay(true);
        } catch (IOException e) {
            e.printStackTrace();
            releaseSocket();
            result.setCode(ResultCode.CONNECT_FAIL);
            result.setMsg(e.getMessage());
            return result;
        }
        result.setCode(ResultCode.RESULT_OK);
        result.setMsg("成功");
        return result;
    }

    /**
     * 释放socket
     */
    private void releaseSocket() {
        if (mSocket != null) {
            try {
                if (mSocket.isConnected()) {
                    mSocket.shutdownInput();
                    mSocket.shutdownOutput();
                }
                mSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                LogUtils.e(TAG, ioException.getMessage());
            }
            mSocket = null;
        }
    }


    /**
     * 重连
     *
     * @return
     */
    public Result<Object> reConnect() {
        if (mSocket == null) {
            return connect();
        }
        releaseSocket();
        return connect();
    }


    /**
     * 发送
     *
     * @param bytes
     * @return
     */
    public Result<Object> send(byte[] bytes) {
        Result<Object> result = new Result<>();
        if (bytes == null) {
            result.setCode(ResultCode.PARAMETER_ERROR);
            result.setMsg(ResultCode.PARAMETER_ERROR_MSG);
            return result;
        }
        if (mSocket == null) {
            result.setCode(ResultCode.SOCKET_NULL);
            result.setMsg(ResultCode.SOCKET_NULL_MSG);
            return result;
        }
        synchronized (this) {
            try {
                OutputStream outputStream = mSocket.getOutputStream();
                if (outputStream == null) {
                    result.setCode(ResultCode.OUTPUT_STREAM_NULL);
                    result.setMsg(ResultCode.OUTPUT_STREAM_NULL_MSG);
                    return result;
                }

                outputStream.write(bytes);
                outputStream.flush();

            } catch (IOException e) {
                e.printStackTrace();
                result.setCode(ResultCode.SEND_FAIL);
                result.setMsg(e.getMessage());
                return result;
            }
        }
        result.setCode(ResultCode.RESULT_OK);
        result.setMsg(ResultCode.RESULT_OK_MSG);
        return result;
    }

    /**
     * 接收
     *
     * @param start
     * @param end
     * @return
     */
    public Result<byte[]> receive(int start, int end) {
        Result<byte[]> result = new Result<>();

        if (mSocket == null) {
            result.setCode(ResultCode.SOCKET_NULL);
            result.setMsg(ResultCode.SOCKET_NULL_MSG);
            return result;
        }

        if (start >= end) {
            result.setCode(ResultCode.PARAMETER_ERROR);
            result.setMsg(ResultCode.PARAMETER_ERROR_MSG);
            return result;
        }

        if (!mSocket.isConnected()) {
            result.setCode(ResultCode.SOCKET_NOT_CONNECTED);
            result.setMsg(ResultCode.SOCKET_NOT_CONNECTED_MSG);
            return result;
        }

        InputStream inputStream = null;
        try {
            inputStream = mSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            //ignore
            LogUtils.e(TAG, e.getMessage());
        }
        if (inputStream == null) {
            result.setCode(ResultCode.INPUT_STREAM_NULL);
            result.setMsg(ResultCode.INPUT_STREAM_NULL_MSG);
            return result;
        }

        byte[] data;
        long startReadTime = System.currentTimeMillis();
        int offset = 0;
        if (mBuffer == null) {
            mBuffer = new byte[mBufferSize];
        }
        do {
            int readByteNum;
            try {
                if (inputStream.available() > 0 && (readByteNum = inputStream.read(mBuffer, offset, end - offset)) > 0) {
                    offset += readByteNum;
                }
                if (offset >= end) {
                    data = new byte[end - start];
                    System.arraycopy(mBuffer, start, data, 0, end - start);
                    result.setCode(ResultCode.RESULT_OK);
                    result.setMsg(ResultCode.RESULT_OK_MSG);
                    result.setData(data);
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
                result.setCode(ResultCode.RESULT_FAIL);
                result.setMsg(e.getMessage());
                break;
            }
        } while (System.currentTimeMillis() - startReadTime >= 0 && System.currentTimeMillis() - startReadTime < mRevTimeOut);
        result.setCode(-1);
        result.setMsg("");
        return result;
    }


    /**
     * 接收
     *
     * @param start
     * @param end
     * @return
     */
    public Result<Integer> receive(byte[] data,int start, int end) {
        Result<Integer> result = new Result<>();


        if (mSocket == null) {
            result.setCode(ResultCode.SOCKET_NULL);
            result.setMsg(ResultCode.SOCKET_NULL_MSG);
            return result;
        }

        if (start >= end) {
            result.setCode(ResultCode.PARAMETER_ERROR);
            result.setMsg(ResultCode.PARAMETER_ERROR_MSG);
            return result;
        }

        if (data == null || data.length < (end - start)) {
            result.setCode(ResultCode.PARAMETER_ERROR);
            result.setMsg(ResultCode.PARAMETER_ERROR_MSG);
            return result;
        }

        if (!mSocket.isConnected()) {
            result.setCode(ResultCode.SOCKET_NOT_CONNECTED);
            result.setMsg(ResultCode.SOCKET_NOT_CONNECTED_MSG);
            return result;
        }

        InputStream inputStream = null;
        try {
            inputStream = mSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            //ignore
            LogUtils.e(TAG, e.getMessage());
        }
        if (inputStream == null) {
            result.setCode(ResultCode.INPUT_STREAM_NULL);
            result.setMsg(ResultCode.INPUT_STREAM_NULL_MSG);
            return result;
        }

        long startReadTime = System.currentTimeMillis();
        int offset = 0;
        if (mBuffer == null) {
            mBuffer = new byte[mBufferSize];
        }
        do {
            int readByteNum;
            try {
                if (inputStream.available() > 0 && (readByteNum = inputStream.read(mBuffer, offset, end - offset)) > 0) {
                    offset += readByteNum;
                }
                if (offset >= end) {
                    System.arraycopy(mBuffer, start, data, 0, end - start);
                    result.setCode(ResultCode.RESULT_OK);
                    result.setMsg(ResultCode.RESULT_OK_MSG);
                    result.setData(offset);
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                result.setCode(ResultCode.RESULT_FAIL);
                result.setMsg(e.getMessage());
                break;
            }
        } while (System.currentTimeMillis() - startReadTime > 0 && System.currentTimeMillis() - startReadTime < mRevTimeOut);
        return result;
    }

    /**
     * 关闭
     */
    public void close() {
        releaseSocket();
    }

    public String getIp() {
        return mIp;
    }

    public void setIp(String ip) {
        mIp = ip;
    }

    public int getPort() {
        return mPort;
    }

    public void setPort(int port) {
        mPort = port;
    }

    public int getConnectTimeOut() {
        return mConnectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        mConnectTimeOut = connectTimeOut;
    }

    public int getBufferSize() {
        return mBufferSize;
    }

    public void setBufferSize(int bufferSize) {
        mBufferSize = bufferSize;
    }

    public byte[] getBuffer() {
        return mBuffer;
    }

    public void setBuffer(byte[] buffer) {
        mBuffer = buffer;
    }

    public int getRevTimeOut() {
        return mRevTimeOut;
    }

    public void setRevTimeOut(int revTimeOut) {
        mRevTimeOut = revTimeOut;
    }

    /**
     * 错误码定义
     */
    private static class ResultCode {
        public static final int RESULT_OK = 0;
        public static final int CONNECT_FAIL = -1;
        public static final int PARAMETER_ERROR = -2;
        public static final int SOCKET_NULL = -3;
        public static final int OUTPUT_STREAM_NULL = -4;
        public static final int SEND_FAIL = -5;
        public static final int SOCKET_NOT_CONNECTED = -6;
        public static final int INPUT_STREAM_NULL = -6;
        public static final int RESULT_FAIL = -7;

        //错误信息
        public static final String PARAMETER_ERROR_MSG = "参数错误";
        public static final String RESULT_OK_MSG = "成功";
        public static final String SOCKET_NULL_MSG = "socket为空";
        public static final String OUTPUT_STREAM_NULL_MSG = "OutputStream为空";
        public static final String SOCKET_NOT_CONNECTED_MSG = "socket未连接";
        public static final String INPUT_STREAM_NULL_MSG = "InputStream为空";

    }
}