package com.android.zxing.coder;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

/**
 * zxing创建图形码
 */
public class ZxingEncoder {

    /**
     * 创建矩阵
     *
     * @param barcodeFormat 格式
     * @param data          内容
     * @param requireWidth  宽度
     * @param requireHeight 高度
     * @return
     */
    public static BitMatrix createBitMatrix(BarcodeFormat barcodeFormat, String data, int requireWidth, int requireHeight) {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(data, barcodeFormat, requireWidth, requireHeight, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return matrix;
    }

    /**
     * 矩阵转像素
     *
     * @param matrix 矩阵
     * @return
     */
    public static int[] bitMatrix2Pixels(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        //二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }
        return pixels;
    }

    /**
     * 生成QRCode（二维码）
     *
     * @param barcodeFormat 格式
     * @param data          内容
     * @param requireWidth  宽
     * @param requireHeight 搞
     * @return
     */
    public static Bitmap createCode(BarcodeFormat barcodeFormat, String data, int requireWidth, int requireHeight) {
        if (data == null || data.equals("")) {
            return null;
        }
        BitMatrix bitMatrix = createBitMatrix(barcodeFormat, data, requireWidth, requireHeight);
        int[] pixels = bitMatrix2Pixels(bitMatrix);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 生成QRCode（二维码）
     *
     * @param data          内容
     * @param requireWidth  宽
     * @param requireHeight 搞
     * @return
     */
    public static Bitmap createQRCode(String data, int requireWidth, int requireHeight) {
        return createCode(BarcodeFormat.QR_CODE, data, requireWidth, requireHeight);
    }

    /**
     * 创建 300*300 二维码
     *
     * @param data 内容
     * @return
     */
    public static Bitmap createQRCode(String data) {
        return createQRCode(data, 300, 300);
    }

}
