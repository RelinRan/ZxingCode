package com.android.zxing.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.zxing.R;

/**
 * 扫描区域
 */
public class ScanAreaView extends View {

    /**
     * 左边间距
     */
    private float borderLeft = 0;
    /**
     * 上方间距
     */
    private float borderTop = 0;
    /**
     * 宽度
     */
    private float borderRight = 0;
    /**
     * 高度
     */
    private float borderBottom = 0;

    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 宽度
     */
    private float width;
    /**
     * 高度
     */
    private float height;
    /**
     * 中心X
     */
    private float centerX = 0;
    /**
     * 中心Y
     */
    private float centerY = 0;
    /**
     * 扫描宽度
     */
    private float areaWidth = 0;
    /**
     * 扫描高度
     */
    private float areaHeight = 0;
    /**
     * 背景颜色
     */
    private int backgroundColor = Color.parseColor("#222222");
    /**
     * 线条是否可见
     */
    private boolean cornerVisible = true;
    /**
     * 线条宽度
     */
    private int cornerLineColor = Color.parseColor("#274F8A");
    /**
     * 线条间距
     */
    private float cornerLineMargin = 20;
    /**
     * 线条长度
     */
    private float cornerLineLength = 40;
    /**
     * 线条宽度
     */
    private float cornerLineWidth = 6;
    /**
     * 持续时间
     */
    private int duration = 1000;
    /**
     * 扫描线资源id
     */
    private int lineDrawable = R.mipmap.ic_scan_code_line;
    /**
     * 扫描震动
     */
    private boolean vibrator = true;
    /**
     * 动画
     */
    private ValueAnimator animator;
    /**
     * 线条移动Y
     */
    private float lineMoveY = 0;


    public ScanAreaView(@NonNull Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public ScanAreaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public ScanAreaView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    /**
     * 初始化参数
     *
     * @param context 上下文
     * @param attrs   参数
     */
    protected void initAttributeSet(Context context, AttributeSet attrs) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScanAreaView);
        centerX = array.getDimension(R.styleable.ScanAreaView_areaCenterX, centerX);
        centerY = array.getDimension(R.styleable.ScanAreaView_areaCenterY, centerY);
        areaWidth = array.getDimension(R.styleable.ScanAreaView_areaWidth, areaWidth);
        areaHeight = array.getDimension(R.styleable.ScanAreaView_areaHeight, areaHeight);
        backgroundColor = array.getColor(R.styleable.ScanAreaView_backgroundColor, backgroundColor);
        cornerVisible = array.getBoolean(R.styleable.ScanAreaView_cornerVisible, cornerVisible);
        cornerLineColor = array.getColor(R.styleable.ScanAreaView_cornerLineColor, cornerLineColor);
        cornerLineMargin = array.getDimension(R.styleable.ScanAreaView_cornerLineMargin, cornerLineMargin);
        cornerLineLength = array.getDimension(R.styleable.ScanAreaView_cornerLineLength, cornerLineLength);
        cornerLineWidth = array.getDimension(R.styleable.ScanAreaView_cornerLineWidth, cornerLineWidth);
        duration = array.getInt(R.styleable.ScanAreaView_duration, duration);
        lineDrawable = array.getResourceId(R.styleable.ScanAreaView_lineDrawable, lineDrawable);
        vibrator = array.getBoolean(R.styleable.ScanAreaView_vibrator, vibrator);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        if (centerX == 0 && centerY == 0) {
            centerX = width / 2.0F;
            centerY = height / 2.0F;
        }
        if (areaWidth == 0 && areaHeight == 0) {
            areaWidth = width * 0.5F;
            areaHeight = width * 0.5F;
        }
        if (borderLeft == 0 && borderTop == 0 && borderRight == 0 && borderBottom == 0) {
            borderLeft = centerX - areaWidth / 2;
            borderTop = centerY - areaHeight / 2;
            borderRight = centerX + areaWidth / 2;
            borderBottom = centerY + areaHeight / 2;
        }
        start();
    }

    /**
     * 开始
     */
    public void start() {
        if (animator == null) {
            animator = ValueAnimator.ofFloat(0, areaHeight);
        }
        animator.setDuration(duration);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lineMoveY = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    /**
     * 结束
     */
    public void stop() {
        if (animator != null) {
            animator.cancel();
        }
        lineMoveY = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制扫描区域
        drawArea(canvas);
        //绘制四角
        drawCorners(canvas);
        //绘制扫描线
        drawScanLine(canvas, lineMoveY);
    }

    /**
     * 绘制区域
     *
     * @param canvas 画布
     */
    private void drawArea(Canvas canvas) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(backgroundColor);
        canvas.drawRect(0, 0, width, height, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawRect(borderLeft, borderTop, borderRight, borderBottom, paint);
    }

    /**
     * 绘制路径
     *
     * @param canvas    画布
     * @param lineColor 线条颜色
     * @param startX    开始X
     * @param startY    开始Y
     * @param centerX   中间X
     * @param centerY   中间Y
     * @param endX      结束X
     * @param endY      结束Y
     */
    private void drawPath(Canvas canvas, int lineColor, float startX, float startY, float centerX, float centerY, float endX, float endY) {
        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(centerX, centerY);
        path.lineTo(endX, endY);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(cornerLineWidth);
        paint.setColor(cornerLineColor);
        canvas.drawPath(path, paint);
    }

    /**
     * 绘制角落
     *
     * @param canvas 画布
     */
    private void drawCorners(Canvas canvas) {
        if (!isCornerVisible()) {
            return;
        }
        //左上角
        drawPath(canvas, cornerLineColor,
                borderLeft - cornerLineMargin, borderTop + cornerLineLength,
                borderLeft - cornerLineMargin, borderTop - cornerLineMargin,
                borderLeft + cornerLineLength, borderTop - cornerLineMargin);
        //右上角
        drawPath(canvas, cornerLineColor,
                borderRight - cornerLineLength, borderTop - cornerLineMargin,
                borderRight + cornerLineMargin, borderTop - cornerLineMargin,
                borderRight + cornerLineMargin, borderTop + cornerLineLength);
        //左下角
        drawPath(canvas, cornerLineColor,
                borderLeft - cornerLineMargin, borderBottom - cornerLineLength,
                borderLeft - cornerLineMargin, borderBottom + cornerLineMargin,
                borderLeft + cornerLineLength, borderBottom + cornerLineMargin);
        //右下角
        drawPath(canvas, cornerLineColor,
                borderRight + cornerLineMargin, borderBottom - cornerLineLength,
                borderRight + cornerLineMargin, borderBottom + cornerLineMargin,
                borderRight - cornerLineLength, borderBottom + cornerLineMargin);
    }

    /**
     * 绘制扫描线条
     *
     * @param canvas 画布
     * @param moveY  移动Y
     */
    private void drawScanLine(Canvas canvas, float moveY) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), lineDrawable);
        if (bitmap == null) {
            return;
        }
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dst = new Rect((int) borderLeft, (int) (borderTop + moveY), (int) borderRight, (int) (borderTop + bitmap.getHeight() + moveY));
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, src, dst, paint);
    }

    public float getBorderLeft() {
        return borderLeft;
    }

    public void setBorderLeft(float borderLeft) {
        this.borderLeft = borderLeft;
        invalidate();
    }

    public float getBorderTop() {
        return borderTop;
    }

    public void setBorderTop(float borderTop) {
        this.borderTop = borderTop;
        invalidate();
    }

    public float getBorderRight() {
        return borderRight;
    }

    public void setBorderRight(float borderRight) {
        this.borderRight = borderRight;
        invalidate();
    }

    public float getBorderBottom() {
        return borderBottom;
    }

    public void setBorderBottom(float borderBottom) {
        this.borderBottom = borderBottom;
        invalidate();
    }

    public float getAreaWidth() {
        return areaWidth;
    }

    public void setAreaWidth(float areaWidth) {
        this.areaWidth = areaWidth;
        invalidate();
    }

    public float getAreaHeight() {
        return areaHeight;
    }

    public void setAreaHeight(float areaHeight) {
        this.areaHeight = areaHeight;
        invalidate();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    public int getCornerLineColor() {
        return cornerLineColor;
    }

    public void setCornerLineColor(int cornerLineColor) {
        this.cornerLineColor = cornerLineColor;
        invalidate();
    }

    public float getCornerLineMargin() {
        return cornerLineMargin;
    }

    public void setCornerLineMargin(float cornerLineMargin) {
        this.cornerLineMargin = cornerLineMargin;
        invalidate();
    }

    public float getCornerLineLength() {
        return cornerLineLength;
    }

    public void setCornerLineLength(float cornerLineLength) {
        this.cornerLineLength = cornerLineLength;
        invalidate();
    }

    public float getCornerLineWidth() {
        return cornerLineWidth;
    }

    public void setCornerLineWidth(float cornerLineWidth) {
        this.cornerLineWidth = cornerLineWidth;
        invalidate();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        invalidate();
    }

    public void setLineDrawable(int lineDrawable) {
        this.lineDrawable = lineDrawable;
        invalidate();
    }

    public int getLineDrawable() {
        return lineDrawable;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
        invalidate();
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
        invalidate();
    }

    public void setVibrator(boolean vibrator) {
        this.vibrator = vibrator;
        invalidate();
    }

    public boolean isVibrator() {
        return vibrator;
    }

    public void setCornerVisible(boolean cornerVisible) {
        this.cornerVisible = cornerVisible;
        invalidate();
    }

    public boolean isCornerVisible() {
        return cornerVisible;
    }
}
