package com.android.zxing.coder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.android.zxing.utils.DecoderFormat;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
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
     * 最小宽度
     */
    public static int MIN_WIDTH = 50;
    /**
     * 最小高度
     */
    public static int MIN_HEIGHT = 50;
    /**
     * 日志标识
     */
    public static final String TAG = ZXReader.class.getSimpleName();
    /**
     * 异常信息
     */
    public static String MSG_EXCEPTION = "Data not parsed.";
    /**
     * 解析消息
     */
    private static ReaderHandler readerHandler;

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
     * @param level    解析等级，初始等级1.
     * @param bitmap   图片Bitmap
     * @param listener 解析监听
     */
    public static void fromBitmap(int level, Bitmap bitmap, OnScanCodeListener listener) {
        bitmap = decodeGray(bitmap);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.i(TAG, "fromBitmap width = " + width + ",height = " + height);
        Result result = decodeBitmap(bitmap);
        if (result != null) {
            Log.i(TAG, "->fromBitmap result = " + result.toString());
            sendReaderMessage(new ReaderBody(ReaderHandler.OK, result, listener));
        } else {
            if (width < MIN_WIDTH || height < MIN_HEIGHT) {
                Log.i(TAG, "fromBitmap " + MSG_EXCEPTION);
                sendReaderMessage(new ReaderBody(ReaderHandler.NOT_FOUND, null, listener));
            } else {
                double scale = Math.pow(0.95F, level);
                int reqWidth = (int) (width * scale);
                int reqHeight = (int) (height * scale);
                Log.i(TAG, "fromBitmap reqWidth = " + reqWidth + ",reqHeight = " + reqHeight + ",level = " + level);
                Bitmap scaleBitmap = scaleBitmap(bitmap, reqWidth, reqHeight);
                level += 1;
                fromBitmap(level, scaleBitmap, listener);
            }
        }
    }

    /**
     * 解析Bitmap
     *
     * @param bitmap   图片
     * @param listener 监听
     */
    public static void fromBitmap(Bitmap bitmap, OnScanCodeListener listener) {
        new Thread(new ReaderTask(new ReaderParams(bitmap, listener))).start();
    }

    /**
     * 获取读取Handler
     *
     * @return
     */
    protected static ReaderHandler getReaderHandler() {
        if (readerHandler == null) {
            readerHandler = new ReaderHandler(Looper.getMainLooper());
        }
        return readerHandler;
    }

    /**
     * 发送解析消息
     *
     * @param body
     */
    protected static void sendReaderMessage(ReaderBody body) {
        Message msg = getReaderHandler().obtainMessage();
        msg.what = body.what();
        msg.obj = body;
        getReaderHandler().sendMessage(msg);
    }

    /**
     * 解析任务
     */
    private static class ReaderTask implements Runnable {

        /**
         * 解析参数
         */
        private ReaderParams params;

        public ReaderTask(ReaderParams params) {
            this.params = params;
        }

        @Override
        public void run() {
            fromBitmap(1, params.bitmap(), params.listener());
        }
    }

    /**
     * 解析图片为二维码
     *
     * @param bitmap 图片资源
     * @return
     */
    private static Result decodeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        MultiFormatReader multiFormatReader = getMultiFormatReader();
        if (source != null) {
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                return multiFormatReader.decodeWithState(binaryBitmap);
            } catch (ReaderException e) {
                return null;
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
        if (file == null || !file.exists()) {
            Log.e(TAG, "fromFile file is not exist.");
            return;
        }
        String path = file.getAbsolutePath();
        Log.i(TAG, "fromFile path = " + path);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        fromBitmap(bitmap, listener);
    }

    /**
     * 缩放图片
     *
     * @param src       原图片
     * @param reqWidth  需要宽度
     * @param reqHeight 需要高度
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap src, int reqWidth, int reqHeight) {
        if (src == null) {
            return null;
        }
        int height = src.getHeight();
        int width = src.getWidth();
        float scaleWidth = ((float) reqWidth) / width;
        float scaleHeight = ((float) reqHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap result = Bitmap.createBitmap(src, 0, 0, width, height, matrix, false);
        if (!src.isRecycled()) {
            src.recycle();
        }
        return result;
    }

    /**
     * 解析为灰色图片
     *
     * @param src
     * @return
     */
    public static Bitmap decodeGray(Bitmap src) {
        int width, height;
        height = src.getHeight();
        width = src.getWidth();
        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(src, 0, 0, paint);
        return grayBitmap;
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
