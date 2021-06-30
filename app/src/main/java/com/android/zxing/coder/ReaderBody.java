package com.android.zxing.coder;

import com.google.zxing.Result;

/**
 * 扫描消息体
 */
public class ReaderBody {

    /**
     * 消息标识
     */
    private int what;
    /**
     * 扫描结果
     */
    private Result result;
    /**
     * 扫描监听
     */
    private OnScanCodeListener listener;

    /**
     * 构造函数
     *
     * @param what     消息标识
     * @param result   扫描结果
     * @param listener 扫描监听
     */
    public ReaderBody(int what, Result result, OnScanCodeListener listener) {
        this.what = what;
        this.result = result;
        this.listener = listener;
    }

    /**
     * 消息标识
     * @return
     */
    public int what() {
        return what;
    }

    /**
     * 设置消息标识
     * @param what
     */
    public void what(int what) {
        this.what = what;
    }

    /**
     * 扫描结果
     * @return
     */
    public Result result() {
        return result;
    }

    /**
     * 设置扫描结果
     * @param result
     */
    public void result(Result result) {
        this.result = result;
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