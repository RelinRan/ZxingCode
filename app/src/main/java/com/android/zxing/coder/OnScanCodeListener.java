package com.android.zxing.coder;

import com.google.zxing.ReaderException;
import com.google.zxing.Result;

public interface OnScanCodeListener {

        /**
         * 扫描解析成功
         *
         * @param result 条码数据
         */
        void onScanCodeSucceed(Result result);

        /**
         * 扫描解析失败
         *
         * @param exception 异常信息
         */
        void onScanCodeFailed(Exception exception);

    }