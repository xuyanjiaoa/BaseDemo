package com.emp.yjy.baselib.net.tcp.server;

import com.emp.yjy.baselib.utils.LogUtils;
import com.emp.yjy.baselib.utils.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
    private ConcurrentHashMap<Integer, SocketHandleRunnable> mMap;
    //最大连接数
    private int mMaxConnect = 1024;
    //可用id数组
    private List<Integer> mAvailableIdList;
    //监听器
    private SocketServerListener mSocketServerListener;
    //读写锁
    private Object mLock = new Object();
    //连接存活时间（单位ms）
    private long mSurvivalTime = 120 * 1000;


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
                mMap = new ConcurrentHashMap<>((int) (mMaxConnect / 0.75));
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
            if (getAvailableConnectNum() <= 0) {
                LogUtils.e(TAG, "连接数量已经超过最大值");
                error("连接数量已经超过最大值");
                return;
            }
            Integer id = getSocketIdFromCache();
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

                @Override
                public void exit(int id) {
                    SocketHandleRunnable remove = mMap.remove(id);
                    if (remove != null) {
                        addSocketIdToCache(id);
                        remove = null;
                    }
                    LogUtils.i(TAG, "释放id为" + id + "连接");

                }
            });
            handleRunnable.setSurvivalTime(mSurvivalTime);
            deleteSockIdFromCache();
            mThreadPool.execute(handleRunnable);
            mMap.put(id, handleRunnable);
            LogUtils.i(TAG, "新连接：" + id);
            LogUtils.i(TAG, "当前连接数量：" + mMap.size());
        }
    }

    /**
     * 从缓存中获取id，分配给连接
     *
     * @return
     */
    private int getSocketIdFromCache() {
        synchronized (mLock) {
            return mAvailableIdList.get(mAvailableIdList.size() - 1);
        }

    }

    /**
     * 将id添加到缓存中，因为有些连接已经释放
     *
     * @param id
     */
    private void addSocketIdToCache(int id) {
        synchronized (mLock) {
            mAvailableIdList.add(id);
        }
    }

    /**
     * 从缓存中移除id，id已经分配给连接
     *
     * @return
     */
    private int deleteSockIdFromCache() {
        synchronized (mLock) {
            return mAvailableIdList.remove(mAvailableIdList.size() - 1);
        }
    }

    /**
     * 获取可用连接个数
     *
     * @return
     */
    private int getAvailableConnectNum() {
        synchronized (mLock) {
            return mAvailableIdList.size();
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

    public long getSurvivalTime() {
        return mSurvivalTime;
    }

    public void setSurvivalTime(long survivalTime) {
        mSurvivalTime = survivalTime;
    }
}
