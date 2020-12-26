package com.emp.yjy.baselib.net.tcp.server;

import android.os.SystemClock;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author linruihang
 * @description:
 * @date :2020/12/26 19:55
 */
public class AcceptRunnable implements Runnable {
    private boolean isExit = false;
    private ServerSocket mServerSocket;
    //缓存
    private byte[] mBuffer;
    private AcceptCallBack mAcceptCallBack;

    public AcceptRunnable(boolean isExit, ServerSocket serverSocket, AcceptCallBack callBack) throws IOException {
        this.isExit = isExit;
        this.mServerSocket = serverSocket;
        this.mAcceptCallBack = callBack;
    }

    @Override
    public void run() {
        while (!isExit) {
            try {
                Socket accept = mServerSocket.accept();
                if (mAcceptCallBack != null) {
                    mAcceptCallBack.connect(accept);
                }
            } catch (IOException e) {
                e.printStackTrace();
//                if (mAcceptCallBack != null) {
//                    mAcceptCallBack.error(e.getMessage());
//                }
                break;

            }
            SystemClock.sleep(1);
        }
    }

    /**
     * 退出监听
     */
    public void exit() {
        isExit = true;
    }

    /**
     * socket连接监听
     */
    interface AcceptCallBack{
        /**
         * 链接
         * @param socket
         */
        void connect(Socket socket);

        /**
         * 错误
         * @param errMsg
         */
        void error(String errMsg);
    }


}
