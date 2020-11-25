package com.emp.yjy.baselib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Bitmap工具类
 *
 * @author Created by LRH
 * @date 2020/11/12 17:59
 */
public class BitmapUtils {

    /**
     * 图片压缩（质量压缩，尺寸不变）
     *
     * @param src       原图
     * @param largeSize 图片最大空间大小
     * @return
     */
    public static byte[] compressBmpToBytes(Bitmap src, int largeSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        //循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > largeSize) {
            //重置baos即清空baos
            baos.reset();
            //这里压缩options%，把压缩后的数据存放到baos中
            src.compress(Bitmap.CompressFormat.JPEG, options, baos);
            //避免出现options<=0
            if (options > 20) {
                //每次都减少10
                options -= 10;
            } else {
                break;
            }
        }
        return baos.toByteArray();
    }

    /**
     * @param src     原图
     * @param degrees 角度
     * @return bitmap
     * @deprecated 图片旋转(图片四周会出现黑边)
     */
    public static Bitmap rotate(@NonNull Bitmap src, float degrees) {
        Bitmap bitmap2 = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas canvas = new Canvas(bitmap2);
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap2.getWidth() >> 1, bitmap2.getHeight() >> 1);
        Paint paint = new Paint();
        //设置抗锯齿
        paint.setAntiAlias(true);
        canvas.drawBitmap(src, matrix, paint);
        return bitmap2;
    }

    /**
     * 选择变换
     *
     * @param origin  原图
     * @param degrees 旋转角度，可正可负
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap origin, float degrees) {
        if (origin == null) {
            return null;
        }
        if (degrees == 0) {
            return origin;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        // 围绕原地进行旋转
        Bitmap newBitmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBitmap.equals(origin)) {
            return newBitmap;
        }
        origin.recycle();
        return newBitmap;
    }


    /**
     * 转换 Bitmap 到 BGR.
     *
     * @param image 图片
     */
    public static byte[] bitmap2BGR(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();
        //从堆空间中分配一块大小为bytes的数组作为缓冲区
        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes);
        //把图片字节数据复制到缓冲区
        image.copyPixelsToBuffer(buffer);
        //获取包含数据的基础数组
        byte[] temp = buffer.array();
        byte[] pixels = new byte[(temp.length / 4) * 3];
        // Copy pixels into place
        for (int i = 0; i < temp.length / 4; i++) {
            //B
            pixels[i * 3] = temp[i * 4 + 2];
            //G
            pixels[i * 3 + 1] = temp[i * 4 + 1];
            //R
            pixels[i * 3 + 2] = temp[i * 4];
        }
        return pixels;
    }


    /**
     * 根据图片路径获取图片的方向信息
     *
     * @param path 图片绝对路径
     * @return 方向信息
     */
    public static int getBitmapDegree(String path) throws IOException {
        int degree = 0;
        // 从指定路径下读取图片，并获取其EXIF信息
        ExifInterface exifInterface = new ExifInterface(path);
        // 获取图片的方向信息
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            default:
                break;
        }
        return degree;
    }

    /**
     * nv21视频流转化为bitmap
     *
     * @param nv21Data nv21数据流
     * @param width    预览宽度
     * @param height   预览高度
     * @param context  上下文
     * @return 图片
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap nv21ToBitmap(@NonNull byte[] nv21Data, int width, int height, @NonNull Context context) {
        RenderScript rs = RenderScript.create(context.getApplicationContext());
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21Data.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        in.copyFrom(nv21Data);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        Bitmap bmpOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bmpOut);
        return bmpOut;
    }
}
