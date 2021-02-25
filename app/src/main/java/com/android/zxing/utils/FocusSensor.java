package com.android.zxing.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 焦点传感器
 */
public class FocusSensor implements SensorEventListener {

    private Context context;
    private float x = 0;
    private float y = 0;
    private float z = 0;
    private Sensor sensor;
    private SensorManager sensorManager;
    private long focusSensorTime = 0;
    private long frequency = 1000;
    private float scope = 1.0F;
    private OnFocusSensorChangedListener onFocusSensorChangedListener;

    public FocusSensor(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * 设置检查频率
     *
     * @param frequency
     */
    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    /**
     * 获取检查频率
     *
     * @return
     */
    public long getFrequency() {
        return frequency;
    }

    /**
     * 设置移动范围判断
     *
     * @param scope
     */
    public void setScope(float scope) {
        this.scope = scope;
    }

    /**
     * 获取移动范围判断
     *
     * @return
     */
    public float getScope() {
        return scope;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            boolean stay = isFocusStay(event);
            if (System.currentTimeMillis() - focusSensorTime >= frequency) {
                if (onFocusSensorChangedListener != null) {
                    onFocusSensorChangedListener.onFocusSensorChanged(stay);
                }
                focusSensorTime = System.currentTimeMillis();
            }
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * 焦点停留
     *
     * @param event 传感器事件
     * @return
     */
    public boolean isFocusStay(SensorEvent event) {
        float x_value = event.values[0];
        float y_value = event.values[1];
        float z_value = event.values[2];
        if (Math.abs(x - x_value) < scope && Math.abs(y - y_value) < scope && Math.abs(z - z_value) < scope) {
            return true;
        }
        return false;
    }

    /**
     * 设置焦点传感器监听
     *
     * @param onFocusSensorChangedListener
     */
    public void setOnFocusSensorChangedListener(OnFocusSensorChangedListener onFocusSensorChangedListener) {
        this.onFocusSensorChangedListener = onFocusSensorChangedListener;
    }

    public interface OnFocusSensorChangedListener {

        /**
         * 焦点传感器改变
         *
         * @param stay 是否停留
         */
        void onFocusSensorChanged(boolean stay);

    }

}
