package com.emp.yjy.baselib.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Created by LRH
 * @description UDP服务端
 * @date 2021/1/13 19:42
 */
public class UDPServer {
    private DatagramSocket mReceiver;
    private InetAddress mInetAddress;
    private int mServerPort;

    public UDPServer(String ip, int port) throws SocketException, UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ip);
        mReceiver = new DatagramSocket(port, inetAddress);
        this.mInetAddress = inetAddress;
    }

    public UDPServer(int serverPort) throws SocketException {
        mReceiver = new DatagramSocket(serverPort);
        mServerPort = serverPort;
    }

    public void setReceiveTimeout(int timeout) throws SocketException {
        mReceiver.setSoTimeout(timeout);
    }

    /**
     * @param buffer
     * @return
     * @throws IOException
     */
    public int receive(byte[] buffer) throws IOException {
        DatagramPacket pack = createDataPack(buffer, -1, null);
        mReceiver.receive(pack);
        return pack.getLength();
    }

    /**
     * 接收数据
     *
     * @param buffer
     * @param port
     * @param ip
     * @return
     * @throws IOException
     */
    public int receive(byte[] buffer, int port, String ip) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(ip);
        DatagramPacket dataPack = createDataPack(buffer, port, inetAddress);
        mReceiver.receive(dataPack);
        if (!inetAddress.equals(dataPack.getAddress())) {
            return 0;
        }
        return dataPack.getLength();
    }

    /**
     * 关闭
     */
    public void close() {
        if (mReceiver != null) {
            mReceiver.close();
        }
    }

    /**
     * 创建DatagramPacket
     *
     * @param buffer
     * @param port
     * @param inetAddress
     * @return
     * @throws UnknownHostException
     */
    private DatagramPacket createDataPack(byte[] buffer, int port, InetAddress inetAddress) throws UnknownHostException {
        DatagramPacket packet = null;
        if (port > 0) {
            packet = new DatagramPacket(buffer, buffer.length, inetAddress, port);
        } else {
            packet = new DatagramPacket(buffer, buffer.length);
        }
        return packet;
    }
}
