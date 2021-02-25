package com.android.zxing.utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * 扫描振动器
 */
public class ScanVibrator {

    private Vibrator vibrator;
    private long time = 0;

    public ScanVibrator(Context context) {
        vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
    }

    /**
     * 开始震动
     */
    public void start() {
        if (System.currentTimeMillis() - time < 2000) {
            return;
        }
        cancel();
        if (vibrator.hasVibrator()) {
            long[] pattern = new long[]{0L, 200L, 300L, 600L};
            vibrator.vibrate(pattern, -1);
        }
        time = System.currentTimeMillis();
    }

    /**
     * 取消震动
     */
    public void cancel() {
        if (vibrator.hasVibrator()) {
            vibrator.cancel();
        }
    }

}
