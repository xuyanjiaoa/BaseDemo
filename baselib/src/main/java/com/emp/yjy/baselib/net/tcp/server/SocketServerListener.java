package com.emp.yjy.baselib.net.tcp.server;

import java.net.Socket;

/**
 * @author linruihang
 * @description: SocketServer数据及状态监听
 * @date :2020/12/26 21:07
 */
public interface SocketServerListener {

    /**
     * 接收到数据
     *
     * @param id
     * @param data
     */
    void dataAccept(int id, byte[] data, Socket socket);

    /**
     * 、
     * 错误
     *
     * @param errMsg
     */
    void error(String errMsg);
}
