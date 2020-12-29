package com.emp.yjy.basedemo.activity;

import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import com.emp.yjy.basedemo.R;
import com.emp.yjy.baselib.base.BaseActivity;
import com.emp.yjy.baselib.base.Result;
import com.emp.yjy.baselib.net.tcp.client.SocketClient;
import com.emp.yjy.baselib.net.tcp.server.SocketServer;
import com.emp.yjy.baselib.net.tcp.server.SocketServerListener;
import com.emp.yjy.baselib.utils.LogUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


/**
 * @author SZ02204
 */
public class TcpDemoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "TcpDemoActivity";
    private Button btnOpenSocketServer;
    private Button btnCloseSocketServer;
    private Button btnOpenSocketClient;

    private int mPort = 8080;
    private SocketServer mSocketServer;
    private String mSocketServerIp = "127.0.0.1";
    private int mSocketClientNum = 100;


    @Override
    protected void initData() {
        mSocketServer = new SocketServer(mPort, new SocketServerListener() {
            @Override
            public void dataAccept(int id, byte[] data, Socket socket) {
                LogUtils.i(TAG, "socket server 接收到数据长度：" + data.length);
                mSocketServer.send2Client(id, "测试测试".getBytes());
            }

            @Override
            public void error(String errMsg) {
                LogUtils.e(TAG, "SocketServer error:" + errMsg);
            }
        });
        mSocketServer.setMaxConnect(mSocketClientNum / 2);


    }

    @Override
    protected int layoutId() {
        return R.layout.activity_tcp_demo;
    }

    @Override
    protected void initView() {
        btnOpenSocketServer = findViewById(R.id.btn_open_socket_server);
        btnCloseSocketServer = findViewById(R.id.btn_close_socket_server);
        btnOpenSocketClient = findViewById(R.id.btn_open_socket_client);
    }

    @Override
    protected void initListener() {
        super.initListener();
        btnOpenSocketServer.setOnClickListener(this::onClick);
        btnCloseSocketServer.setOnClickListener(this::onClick);
        btnOpenSocketClient.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_open_socket_server) {
            openSocketServer();
        } else if (id == R.id.btn_close_socket_server) {
            closeSocketServer();
        } else if (id == R.id.btn_open_socket_client) {
            openSocketClient();
        }
    }

    /**
     * 开启SocketServer
     */
    private void openSocketServer() {
        if (mSocketServer.isStart()) {
            return;
        }
        boolean start = mSocketServer.start();
        if (start) {
            LogUtils.i(TAG, "SocketServer 开启成功！");
        } else {
            LogUtils.e(TAG, "SocketServer 开启失败！");
        }
    }


    /**
     * 关闭SocketServer
     */
    private void closeSocketServer() {
        if (mSocketServer != null) {
            if (!mSocketServer.isStart()) {
                return;
            }
            mSocketServer.stop();
            LogUtils.i(TAG, "close socketServer！");
        }
    }

    private void openSocketClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mSocketClientNum; i++) {
                    SocketClient client = new SocketClient(mSocketServerIp, mPort);
                    Result<Object> result = client.connect();
                    if (result.isSuccess()) {
                        LogUtils.i(TAG, "client " + i + " 连接成功");
                        Result<Object> send = client.send("测试测试".getBytes());
                        if (send.isSuccess()) {
                            LogUtils.i(TAG, "数据发送成功！");
                        } else {
                            LogUtils.e(TAG, "数据发送失败：" + send.getMsg());
                        }
//                        Result<byte[]> receive = client.receive(0, 8);
//                        if (receive.isSuccess()) {
//                            LogUtils.i(TAG, "socket client 接收到数据：" + receive.getData().length);
//                        }
                    } else {
                        LogUtils.i(TAG, "client " + i + " 连接失败：" + result.getMsg());
                    }
                    SystemClock.sleep(500);
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeSocketServer();
    }
}