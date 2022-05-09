package com.hl.uikit.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.hl.uikit.R;


/**
 * @author 张磊  on  2022/04/13 at 14:59
 * Email: 913305160@qq.com
 */
public class UIKitCircleProgressBar extends View {
    private final Paint mPaint;
    private final RectF mRect;
    private float mProgress;
    private float mMaxProgress;
    private int mColor;
    private int mWidth;
    private int mBgWidth;
    private int mBackground;
    private UIKitCircleProgressBarListener mListener;

    public UIKitCircleProgressBar(Context context) {
        this(context, null, 0);
    }

    public UIKitCircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UIKitCircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mProgress = 0.0F;
        this.mMaxProgress = 100.0F;
        this.mColor = Color.CYAN;
        this.mWidth = 20;
        this.mBgWidth = 20;
        this.mBackground = Color.TRANSPARENT;
        this.mPaint = new Paint();
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setAntiAlias(true);
        this.mRect = new RectF();

        this.init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = this.getContext().obtainStyledAttributes(attrs, R.styleable.UIKitCircleProgressBar);
        this.mWidth = a.getDimensionPixelOffset(R.styleable.UIKitCircleProgressBar_uikit_cpb_width, this.mWidth);
        this.mColor = a.getColor(R.styleable.UIKitCircleProgressBar_uikit_cpb_color, this.mColor);
        this.mBackground = a.getColor(R.styleable.UIKitCircleProgressBar_uikit_cpb_background, this.mBackground);
        this.mProgress = a.getFloat(R.styleable.UIKitCircleProgressBar_uikit_cpb_progress, this.mProgress);
        this.mMaxProgress = a.getFloat(R.styleable.UIKitCircleProgressBar_uikit_cpb_max_progress, this.mMaxProgress);
        this.mBgWidth = a.getDimensionPixelOffset(R.styleable.UIKitCircleProgressBar_uikit_cpb_background_width, this.mBgWidth);
        a.recycle();
    }

    public float getProgress() {
        return this.mProgress;
    }

    public void setProgress(float progress) {
        this.mProgress = Math.max(progress, 0.0F);
        if (this.mListener != null) {
            this.mListener.onProgressChanged(this.mProgress);
        }
        this.invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mRect.left = (float) (this.getPaddingLeft() + this.mWidth / 2);
        this.mRect.top = (float) (this.getPaddingTop() + this.mWidth / 2);
        this.mRect.right = (float) (w - this.getPaddingRight() - this.mWidth / 2);
        this.mRect.bottom = (float) (h - this.getPaddingBottom() - this.mWidth / 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
        int height = resolveSize(MeasureSpec.getSize(heightMeasureSpec), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float degree = 360.0F * this.mProgress / this.mMaxProgress;
        this.mPaint.setStrokeWidth((float) this.mBgWidth);
        this.mPaint.setColor(this.mBackground);
        canvas.drawArc(this.mRect, 0.0F, 360.0F, false, this.mPaint);
        this.mPaint.setStrokeWidth((float) this.mWidth);
        this.mPaint.setColor(this.mColor);
        canvas.drawArc(this.mRect, -90.0F, degree <= 0.0F ? 1.0F : degree, false, this.mPaint);
    }

    public UIKitCircleProgressBarListener getProgressBarListener() {
        return this.mListener;
    }

    public void setProgressBarListener(UIKitCircleProgressBarListener listener) {
        this.mListener = listener;
    }

    public float getMaxProgress() {
        return this.mMaxProgress;
    }

    public void setMaxProgress(float maxProgress) {
        this.mMaxProgress = maxProgress < 0.0F ? 100.0F : this.mMaxProgress;
        this.invalidate();
    }

    public void setCircleWidth(int width) {
        this.mWidth = width;
    }

    public int getBgCircleColor() {
        return this.mBackground;
    }

    public void setBgCircleColor(int background) {
        this.mBackground = background;
    }

    public int getBgCircleWidth() {
        return this.mBgWidth;
    }

    public void setBgCircleWidth(int bgWidth) {
        this.mBgWidth = bgWidth;
    }

    public int getCircleColor() {
        return this.mColor;
    }

    public void setCircleColor(int color) {
        this.mColor = color;
    }

    public int getCirlceWidth() {
        return this.mWidth;
    }

    public interface UIKitCircleProgressBarListener {

        /**
         * 进度改变
         *
         * @param progress 进度
         */
        void onProgressChanged(float progress);
    }
}