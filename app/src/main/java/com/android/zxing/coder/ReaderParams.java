package com.android.zxing.coder;

import android.graphics.Bitmap;

/**
 * 扫描参数
 */
public class ReaderParams {

    /**
     * 扫描图
     */
    private Bitmap bitmap;
    /**
     * 扫描监听
     */
    private OnScanCodeListener listener;
    /**
     * 构造函数
     *
     * @param bitmap 扫描图
     * @param listener 扫描监听
     */
    public ReaderParams(Bitmap bitmap, OnScanCodeListener listener) {
        this.bitmap = bitmap;
        this.listener = listener;
    }

    /**
     * 扫描图
     * @return
     */
    public Bitmap bitmap() {
        return bitmap;
    }

    /**
     * 设置扫描图
     * @param bitmap
     */
    public void bitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * 扫描监听
     * @return
     */
    public OnScanCodeListener listener() {
        return listener;
    }

    /**
     * 设置扫描监听
     * @param listener
     */
    public void listener(OnScanCodeListener listener) {
        this.listener = listener;
    }


}