package com.android.zxing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Author: Relin
 * Describe:相机帧工具
 * Date:2020/12/8 22:57
 */
public class CameraFrame {

    /**
     * 解析帧
     *
     * @param data   帧数据
     * @param width  宽度
     * @param height 高度
     * @param degree 旋转角度
     * @param mirror 是否镜像
     * @return
     */
    public static Bitmap decodeFrame(byte[] data, int width, int height, int degree, boolean mirror) {
        try {
            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, bos);
            byte[] imageBytes = bos.toByteArray();
            if (imageBytes != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                InputStream is = new ByteArrayInputStream(imageBytes);
                WeakReference<Bitmap> weakReference = new WeakReference(BitmapFactory.decodeStream(is, null, options));
                Bitmap bitmap = (Bitmap) weakReference.get();
                if (degree != 0) {
                    Matrix matrix = new Matrix();
                    if (mirror) {
                        Camera camera = new Camera();
                        camera.save();
                        camera.rotateX(180);
                        camera.getMatrix(matrix);
                        camera.restore();
                    }
                    matrix.postRotate(degree);
                    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
                return bitmap;
            }
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 照相机帧转文件路径
     *
     * @param context  上下文
     * @param data     数据帧
     * @param width    宽度
     * @param height   高度
     * @param degree   旋转角度
     * @param fileName 文件名称
     * @param mirror   是否镜像处理
     * @return
     */
    public static File decodeFrame(Context context, byte[] data, int width, int height, int degree, String fileName, boolean mirror) {
        File file = new File(context.getExternalCacheDir(), fileName);
        if (file.exists()) {
            file.delete();
        }
        Bitmap bitmap = decodeFrame(data, width, height, degree,mirror);
        try {
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 文件解析字节数组
     *
     * @param file 文件
     * @return
     */
    public static byte[] decodeFile(File file) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

}
