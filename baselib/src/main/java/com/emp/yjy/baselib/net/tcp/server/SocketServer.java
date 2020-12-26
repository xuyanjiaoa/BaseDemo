package com.emp.yjy.baselib.net.tcp.server;

import com.emp.yjy.baselib.base.Result;
import com.emp.yjy.baselib.utils.LogUtils;
import com.emp.yjy.baselib.utils.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author linruihang
 * @description: SocketServer封装
 * @date :2020/12/26 19:36
 */
public class SocketServer implements AcceptRunnable.AcceptCallBack {
    private static final String TAG = "SocketServer";
    //线程池
    private ThreadPool mThreadPool;
    //是否启动
    private boolean isStart = false;
    //监听任务
    private AcceptRunnable mAcceptRunnable;

    private ServerSocket mServerSocket;

    private int port = 8080;
    //管理连接
    private HashMap<Integer, SocketHandleRunnable> mMap;
    //最大连接数
    private int mMaxConnect = 1024;
    //可用id数组
    private List<Integer> mAvailableIdList;
    //监听器
    private SocketServerListener mSocketServerListener;


    public SocketServer(int port, SocketServerListener listener) {
        this.port = port;
        this.mSocketServerListener = listener;
    }

    private boolean initParam() {
        try {

            if (mServerSocket == null) {
                mServerSocket = new ServerSocket(port);
            }

            if (mThreadPool == null) {
                mThreadPool = new ThreadPool(ThreadPool.CachedThread, 0);
            }

            if (mMap == null) {
                mMap = new HashMap<>((int) (mMaxConnect / 0.75));
            }
            if (mAvailableIdList == null) {
                mAvailableIdList = new ArrayList<>(mMaxConnect);
                for (int i = mMaxConnect; i > 0; i--) {
                    mAvailableIdList.add(i);
                }
            }

            if (mAcceptRunnable == null) {
                mAcceptRunnable = new AcceptRunnable(false, mServerSocket, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
            String error = e.getMessage();
            sendError(error);
            return false;
        }
        return true;
    }

    private void sendError(String error) {
        if (mSocketServerListener != null) {
            mSocketServerListener.error(error);
        }
    }


    /**
     * 启动
     */
    public void start() {
        boolean succ = initParam();
        if (!succ) {
            return;
        }
        mThreadPool.execute(mAcceptRunnable);
        isStart = true;
        LogUtils.d(TAG, "SocketServer start!");
    }

    /**
     * 关闭
     */
    public void stop() {
        if (mAcceptRunnable != null) {
            mAcceptRunnable.exit();
        }
        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mMap != null) {
            for (SocketHandleRunnable runnable : mMap.values()) {
                runnable.exit();
            }
        }
        if (mThreadPool != null) {
            mThreadPool.shutDown();
        }
        isStart = false;
        LogUtils.d(TAG, "SocketServer stop!");

    }


    /**
     * 有客户端连接
     *
     * @param socket
     */
    @Override
    public void connect(Socket socket) {
        if (!isStart) {
            return;
        }
        if (socket != null) {
            if (mAvailableIdList.size() <= 0) {
                LogUtils.e(TAG, "连接数量已经超过最大值");
                error("连接数量已经超过最大值");
                return;
            }
            Integer id = mAvailableIdList.get(mAvailableIdList.size()-1);
            SocketHandleRunnable handleRunnable = new SocketHandleRunnable(true, socket, id, new SocketHandleRunnable.AcceptMsgCallBack() {
                @Override
                public void accept(int id, byte[] data) {
                    if (mSocketServerListener != null) {
                        mSocketServerListener.dataAccept(id, data);
                    }
                }

                @Override
                public void error(String errMsg) {
                    sendError(errMsg);
                }
            });
            mAvailableIdList.remove(mAvailableIdList.size()-1);
            mThreadPool.execute(handleRunnable);
            mMap.put(id, handleRunnable);
            LogUtils.i(TAG, "当前连接数量：" + mMap.size());
        }
    }

    /**
     * 客户端连接错误信息
     *
     * @param errMsg
     */
    @Override
    public void error(String errMsg) {
        sendError(errMsg);
    }


    /**************************************** setter/getter ************************************************/
    public int getMaxConnect() {
        return mMaxConnect;
    }

    public void setMaxConnect(int maxConnect) {
        mMaxConnect = maxConnect;
    }
}
