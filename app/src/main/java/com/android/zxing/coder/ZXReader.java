package com.android.zxing.coder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.android.zxing.utils.DecoderFormat;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * zxing解码
 */
public class ZXReader {

    /**
     * 纠正YUV420方向
     *
     * @param yuv420 相机数据
     * @param width  宽度
     * @param height 高度
     * @return
     */
    public static byte[] correctYuv420(byte[] yuv420, int width, int height) {
        byte[] data = new byte[yuv420.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                data[x * height + height - y - 1] = yuv420[x + y * width];
        }
        return data;
    }

    /**
     * 创建多类型格式
     *
     * @return
     */
    public static MultiFormatReader getMultiFormatReader() {
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        Collection<BarcodeFormat> decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
        decodeFormats.addAll(DecoderFormat.PRODUCT_FORMATS);
        decodeFormats.addAll(DecoderFormat.INDUSTRIAL_FORMATS);
        decodeFormats.addAll(DecoderFormat.QR_CODE_FORMATS);
        decodeFormats.addAll(DecoderFormat.DATA_MATRIX_FORMATS);
        decodeFormats.addAll(DecoderFormat.AZTEC_FORMATS);
        decodeFormats.addAll(DecoderFormat.PDF417_FORMATS);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        multiFormatReader.setHints(hints);
        return multiFormatReader;
    }

    /**
     * 解析图片数据
     *
     * @param bitmap   图片Bitmap
     * @param listener 解析监听
     */
    public static void fromBitmap(Bitmap bitmap, OnScanCodeListener listener) {
        Result result = decodeBitmap(bitmap);
        if (result != null) {
            if (listener != null) {
                listener.onScanCodeSucceed(result);
            }
        } else {
            if (listener != null) {
                listener.onScanCodeFailed(new Exception("Not found"));
            }
        }
    }

    private static Result decodeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(width, height, pixels);
        MultiFormatReader multiFormatReader = getMultiFormatReader();
        if (rgbLuminanceSource != null) {
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
            try {
                return multiFormatReader.decodeWithState(binaryBitmap);
            } catch (ReaderException e) {
                if (rgbLuminanceSource != null) {
                    try {
                        return new MultiFormatReader().decode(new BinaryBitmap(new GlobalHistogramBinarizer(rgbLuminanceSource)));
                    } catch (Throwable e2) {
                        e2.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 解析文件
     *
     * @param file     文件
     * @param listener 监听
     */
    public static void fromFile(File file, OnScanCodeListener listener) {
        if (file == null) {
            new RuntimeException("decode file failed , file is not exist.");
            return;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        int sampleSize = options.outHeight / 400;
        if (sampleSize <= 0) {
            sampleSize = 1;
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        fromBitmap(bitmap, listener);
    }

    /**
     * 获取像素数据
     *
     * @param bitmap 图像数据
     * @return
     */
    public static int[] getPixels(Bitmap bitmap) {
        int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return data;
    }

    /**
     * 解析像素数据
     *
     * @param pixels     像素数据
     * @param dataWidth  数据宽度
     * @param dataHeight 数据高度
     * @param listener   监听
     */
    public static void fromPixels(int[] pixels, int dataWidth, int dataHeight, OnScanCodeListener listener) {
        RGBLuminanceSource source = new RGBLuminanceSource(dataWidth, dataHeight, pixels);
        MultiFormatReader multiFormatReader = getMultiFormatReader();
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                Result result = multiFormatReader.decodeWithState(bitmap);
                if (listener != null) {
                    listener.onScanCodeSucceed(result);
                }
            } catch (ReaderException e) {
                if (listener != null) {
                    listener.onScanCodeFailed(e);
                }
            } finally {
                multiFormatReader.reset();
            }
        }
    }


    /**
     * 处理相机预览帧
     *
     * @param yuv420            相机数据
     * @param width             宽度
     * @param height            高度
     * @param left              目标区域 - 左边间距
     * @param top               目标区域 - 上边间距
     * @param width             目标区域 - 宽度
     * @param height            目标区域 - 高度
     * @param reverseHorizontal 水平反转
     */
    public static void fromYuv420(byte[] yuv420, int dataWidth, int dataHeight, int left, int top, int width, int height, boolean reverseHorizontal, OnScanCodeListener listener) {
        if (width < 0 || height < 0) {
            return;
        }
        byte[] data = correctYuv420(yuv420, dataWidth, dataHeight);
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, dataHeight, dataWidth, left, top, width, height, reverseHorizontal);
        MultiFormatReader multiFormatReader = getMultiFormatReader();
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                Result result = multiFormatReader.decodeWithState(bitmap);
                if (listener != null) {
                    listener.onScanCodeSucceed(result);
                }
            } catch (ReaderException e) {
                if (listener != null) {
                    listener.onScanCodeFailed(e);
                }
            } finally {
                multiFormatReader.reset();
            }
        }
    }

}
