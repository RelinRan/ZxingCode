package com.android.zxing.coder;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public class ReaderHandler extends Handler {

    /**
     * 解析成功
     */
    public static final int OK = 200;
    /**
     * 解析失败
     */
    public static final int NOT_FOUND = 404;

    public ReaderHandler(@NonNull Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        ReaderBody result = (ReaderBody) msg.obj;
        switch (msg.what) {
            case OK:
                if (result.listener() != null) {
                    result.listener().onScanCodeSucceed(result.result());
                }
                break;
            case NOT_FOUND:
                if (result.listener() != null) {
                    result.listener().onScanCodeFailed(new Exception(ZXReader.MSG_EXCEPTION));
                }
                break;
        }
    }
}