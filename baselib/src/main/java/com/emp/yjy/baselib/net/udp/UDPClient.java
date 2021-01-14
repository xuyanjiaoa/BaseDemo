package com.emp.yjy.baselib.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Created by LRH
 * @description UDP客户端
 * @date 2021/1/13 19:30
 */
public class UDPClient {
    private InetAddress mInetAddress;
    private DatagramSocket mSender;
    private int mPort;
    private String host;

    public UDPClient(String host, int port) throws SocketException, UnknownHostException {
        mInetAddress = InetAddress.getByName(host);
        this.mPort = port;
    }


    /**
     * 发送数据
     * @param data
     * @throws IOException
     */
    public void send(byte[] data) throws IOException {
        mSender = new DatagramSocket();
        DatagramPacket outPacket = new DatagramPacket(data, data.length, mInetAddress, mPort);
        mSender.send(outPacket);
        mSender.close();
    }

    /**
     * 关闭
     */
    public void close() {
        if (mSender != null) {
            mSender.close();
        }
    }
}
