package com.android.zxing.coder;

import com.android.zxing.utils.DecoderFormat;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * zxing解码
 */
public class ZxingDecoder {

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
    public static MultiFormatReader createMultiFormatReader() {
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
     * 处理相机预览帧
     *
     * @param yuv420            相机数据
     * @param width             宽度
     * @param height            高度
     * @param borderLeft        目标区域 - 左边间距
     * @param borderTop         目标区域 - 上边间距
     * @param borderRight       目标区域 - 右边间距
     * @param borderBottom      目标区域 - 底边间距
     * @param reverseHorizontal 水平反转
     */
    public static void onPreviewFrame(byte[] yuv420, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom, boolean reverseHorizontal, OnScanDecodeListener listener) {
        byte[] data = correctYuv420(yuv420, width, height);
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, height, width, borderLeft, borderTop, borderRight, borderBottom, reverseHorizontal);
        MultiFormatReader multiFormatReader = createMultiFormatReader();
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                Result result = multiFormatReader.decodeWithState(bitmap);
                if (listener != null) {
                    listener.onScanDecodeSucceed(result);
                }
            } catch (ReaderException e) {
                if (listener != null) {
                    listener.onScanDecodeFailed(e);
                }
            } finally {
                multiFormatReader.reset();
            }
        }
    }

    public interface OnScanDecodeListener {

        /**
         * 扫描解析成功
         *
         * @param result 条码数据
         */
        void onScanDecodeSucceed(Result result);

        /**
         * 扫描解析失败
         *
         * @param exception 异常信息
         */
        void onScanDecodeFailed(ReaderException exception);

    }

}
