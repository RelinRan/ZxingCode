package com.android.zxing.utils;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.TextureView;


import java.util.ArrayList;
import java.util.List;

/**
 * Author: Relin
 * Describe:相机助手
 * Date:2020/12/8 22:57
 */
public class CameraManager implements TextureView.SurfaceTextureListener, Camera.PreviewCallback {

    private final static String TAG = CameraManager.class.getSimpleName();
    /**
     * 竖屏
     */
    public final int PORTRAIT = 1;
    /**
     * 横屏
     */
    public final int LANDSCAPE = 2;

    private Camera camera;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int degrees = 90;
    private int orientation = PORTRAIT;
    private int requireWidth = 1080;
    private int requireHeight = 1920;
    private Camera.Size size;
    private TextureView textureView;

    public CameraManager() {
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public int getDegrees() {
        return degrees;
    }

    public void setDegrees(int degrees) {
        this.degrees = degrees;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getRequireWidth() {
        return requireWidth;
    }

    public void setRequireWidth(int requireWidth) {
        this.requireWidth = requireWidth;
    }

    public int getRequireHeight() {
        return requireHeight;
    }

    public void setRequireHeight(int requireHeight) {
        this.requireHeight = requireHeight;
    }

    public TextureView getTextureView() {
        return textureView;
    }

    public void startPreview(TextureView textureView) {
        textureView.setSurfaceTextureListener(this);
        this.textureView = textureView;
    }

    public Camera.Size getSize() {
        return size;
    }

    public void setSize(Camera.Size size) {
        this.size = size;
    }

    /**
     * 打开相机
     *
     * @param cameraId {@link Camera.CameraInfo#CAMERA_FACING_FRONT} or {@link Camera.CameraInfo#CAMERA_FACING_BACK}
     */
    public void open(int cameraId) {
        this.cameraId = cameraId;
        if (camera != null) {
            camera.release();
        }
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera == null) {
            Log.e(TAG, "open camera failed");
        }
        analyzingCameraParameters(camera);
        size = setCameraPreviewPictureSize(camera, orientation, requireWidth, requireHeight);

    }

    /**
     * 设置焦点区域
     *
     * @param camera 相机
     * @param rect   区域
     */
    public void autoFocusAreas(Camera camera, Rect rect) {
        if (camera == null) {
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Area> areas = new ArrayList();
        areas.add(new Camera.Area(rect, 100));
        parameters.setFocusAreas(areas);
        try {
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            autoFocus();
        }
    }

    /**
     * 自动聚焦
     */
    public void autoFocus() {
        if (camera == null) {
            return;
        }
        try {
            camera.autoFocus((success, camera) -> {
                if (success) {
                    camera.cancelAutoFocus();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 释放摄像头
     */
    public void release() {
        try {
            if (camera != null) {
                camera.stopPreview();
                camera.setPreviewCallback(null);
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析相机参数
     *
     * @param camera
     */
    public void analyzingCameraParameters(Camera camera) {
        List<Camera.Size> pictureSizes = camera.getParameters().getSupportedPictureSizes();
        List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
        for (int i = 0; i < previewSizes.size(); i++) {
            int previewWidth = previewSizes.get(i).width;
            int previewHeight = previewSizes.get(i).height;
            Log.i(TAG, "previewSizes width = " + previewWidth + " , height = " + previewHeight);
        }
    }

    /**
     * 设置相机图片预览大小
     *
     * @param camera        相机
     * @param orientation   屏幕方向
     * @param requireWidth  需要宽度
     * @param requireHeight 需要高度
     */
    public Camera.Size setCameraPreviewPictureSize(Camera camera, int orientation, int requireWidth, int requireHeight) {
        Camera.Parameters params = camera.getParameters();
        Camera.Size size = getSuitPreviewSize(orientation, requireWidth, requireHeight, params.getSupportedPreviewSizes());
        params.setPreviewSize(size.width, size.height);
        params.setPictureSize(size.width, size.height);
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            camera.setDisplayOrientation(degrees);
        } else {
            camera.setDisplayOrientation(degrees);
        }
        Log.i(TAG, "setCameraPreviewPictureSize width = " + size.width + " , height = " + size.height + ",displayOrientation=" + degrees);
        params.setPictureFormat(ImageFormat.JPEG);
        camera.setParameters(params);
        return size;
    }

    /**
     * 通过对比得到与宽高比最接近的预览尺寸（如果有相同尺寸，优先选择）
     *
     * @param orientation   竖屏方向
     * @param requireWidth  需要的宽度
     * @param requireHeight 需要的高度
     * @param previewSizes  预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    public Camera.Size getSuitPreviewSize(int orientation, int requireWidth, int requireHeight, List<Camera.Size> previewSizes) {
        int width = 0, height = 0;
        switch (orientation) {
            case PORTRAIT:
                width = requireHeight;
                height = requireWidth;
                break;
            case LANDSCAPE:
                width = requireWidth;
                height = requireHeight;
                break;
        }
        for (Camera.Size size : previewSizes) {
            if ((size.width == width) && (size.height == height)) {
                return size;
            }
        }
        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) width) / height;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : previewSizes) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        return retSize;
    }


    /**
     * 开始预览
     *
     * @param surface  预览视图
     * @param callback 回调
     */
    public void startPreview(SurfaceTexture surface, Camera.PreviewCallback callback) {
        try {
            if (camera != null) {
                camera.setPreviewTexture(surface);
                camera.setPreviewCallback(callback);
                camera.startPreview();
            } else {
                Log.d(TAG, "No camera");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        open(cameraId);
        startPreview(surface, this);
        if (onCameraPreviewListener != null) {
            onCameraPreviewListener.onSurfaceTextureAvailable(surface,width,height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        release();
        if (onCameraPreviewListener != null) {
            onCameraPreviewListener.onSurfaceTextureDestroyed(surface);
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (onCameraPreviewListener != null) {
            onCameraPreviewListener.onPreviewFrame(data, camera);
        }
    }

    private OnCameraPreviewListener onCameraPreviewListener;

    public void setOnCameraPreviewListener(OnCameraPreviewListener onCameraPreviewListener) {
        this.onCameraPreviewListener = onCameraPreviewListener;
    }

    public interface OnCameraPreviewListener {

        void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height);

        void onPreviewFrame(byte[] data, Camera camera);

        void onSurfaceTextureDestroyed(SurfaceTexture surface);

    }

}
