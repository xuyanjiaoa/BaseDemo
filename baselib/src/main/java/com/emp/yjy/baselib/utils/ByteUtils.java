package com.emp.yjy.baselib.utils;

import android.text.TextUtils;

import java.nio.ByteBuffer;

/**
 * @author Created by LRH
 * @description 字节操作相关工具类
 * @date 2020/12/29 17:17
 */
public class ByteUtils {


    /**
     * 将int转化为byte[],大端模式
     *
     * @param value
     * @return
     */
    public static byte[] int2Byte(int value) {
        return new byte[]{
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 24) & 0xFF)
        };
    }

    /**
     * 将short转化为byte[](大端模式)
     *
     * @param value
     * @return
     */
    public static byte[] short2Byte(short value) {
        return new byte[]{
                (byte) (value & 0xff),
                (byte) ((value >> 8) & 0xff)
        };
    }


    /**
     * 将long转化为byte[]
     *
     * @param value
     * @return
     */
    public static byte[] long2Byte(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, value);
        return buffer.array();
    }


    /**
     * 大端模式字节数组中转整型数值，本方法适用于(低位在前，高位在后)的顺序。
     *
     * @param b byte数组，长度为4
     * @return int数值
     */
    public static int bytes2Int(byte[] b) {
        return b[0] & 0xFF |
                (b[1] & 0xFF) << 8 |
                (b[2] & 0xFF) << 16 |
                (b[3] & 0xFF) << 24;

    }


    /**
     * 大端模式byte[]到short的转换
     *
     * @param bytes 字节数组，长度必须为两个字节
     * @return
     */
    public static short bytes2Short(byte[] bytes) {
        short s0 = (short) ((bytes[0] & 0xff));
        short s1 = (short) ((bytes[1] & 0xff)<<8);
        return (short) (s0 | s1);

    }

    /**
     * 字节数组转化为long
     *
     * @param bytes 字节数组，数组长度必须为8
     * @return
     */
    public static long bytes2Long(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getLong();
    }


    /**
     * 数组转hex字符串
     *
     * @param byteArray 字节数组
     * @return 字符串
     */

    public static String bytes2Hex(byte[] byteArray) {
        final StringBuilder hexString = new StringBuilder("");
        if (byteArray == null || byteArray.length <= 0) {
            return null;
        }
        for (int i = 0; i < byteArray.length; i++) {
            int v = byteArray[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                hexString.append(0);
            }
            hexString.append(hv);
        }
        return hexString.toString();
    }

    /**
     * hex字符串转字节数组
     *
     * @param hexString the hex string
     * @return
     */
    public static byte[] hex2Byte(String hexString) {
        if (TextUtils.isEmpty(hexString)) {
            return null;
        }
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() >> 1];
        int index = 0;
        for (int i = 0; i < hexString.length(); i++) {
            if (index > hexString.length() - 1) {
                return byteArray;
            }
            byte highDit = (byte) (Character.digit(hexString.charAt(index), 16) & 0xFF);
            byte lowDit = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xFF);
            byteArray[i] = (byte) (highDit << 4 | lowDit);
            index += 2;
        }
        return byteArray;
    }

    /**
     * 多个byte[]数组合成一个
     *
     * @param values 数组列表
     * @return
     */
    public static byte[] byteMerger(byte[]... values) {
        int allByteLen = 0;
        for (int i = 0; i < values.length; i++) {
            allByteLen += values[i].length;
        }
        byte[] allByte = new byte[allByteLen];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, allByte, countLength, b.length);
            countLength += b.length;
        }
        return allByte;
    }
}
