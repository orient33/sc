package com.sudoteam.securitycenter.netstat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.sudoteam.securitycenter.R;
import java.lang.ref.WeakReference;

/**
 * animator of water wave.
 */
public class WaterWaveProgress extends View {

    private Paint mPaintWater = null;
    private Paint mRingPaint = null;
    private Paint mTextPaint = null;
    private Point mCenterPoint;

    WaterWaveAttrInit attrInit;
    private int mRingColor;
    private int mRingBgColor;
    private int mWaterColor;
    private int mWaterBgColor;
    private int mFontSize;
    private int mTextColor;
    private float mRingWidth;
    private float mProgress2WaterWidth;
    private boolean mShowProgress = false;
    private boolean mShowNumerical = true;
    int mProgress = 10;
    int mMaxProgress = 100;

    float crestCount = 1.5f;
    private long mWaveFactor = 0L;
    private boolean isWaving = false;
    private float mAmplitude = 10.0F; // 30F
    private float mWaveSpeed = 0.020F; // 0.070F
    private int mWaterAlpha = 255; // 255

    private MyHandler mHandler = null;

    private static class MyHandler extends Handler {
        private WeakReference<WaterWaveProgress> mWeakRef = null;

        private int refreshPeriod = 100;

        public MyHandler(WaterWaveProgress host) {
            mWeakRef = new WeakReference<WaterWaveProgress>(host);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mWeakRef.get() != null) {
                mWeakRef.get().invalidate();
                sendEmptyMessageDelayed(0, refreshPeriod);
            }
        }
    }

    public WaterWaveProgress(Context paramContext) {
        super(paramContext);
    }

    public WaterWaveProgress(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WaterWaveProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attrInit = new WaterWaveAttrInit(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("NewApi")
    private void init(Context context) {
        mCenterPoint = new Point();

        mRingColor = attrInit.getProgressColor();
        mRingBgColor = attrInit.getProgressBgColor();
        mWaterColor = attrInit.getWaterWaveColor();
        mWaterBgColor = attrInit.getWaterWaveBgColor();
        mFontSize = attrInit.getFontSize();
        mTextColor = attrInit.getTextColor();
        mRingWidth = attrInit.getProgressWidth();
        mProgress2WaterWidth = attrInit.getProgress2WaterWidth();
        mShowProgress = attrInit.isShowProgress();
        mShowNumerical = attrInit.isShowNumerical();
        mProgress = attrInit.getProgress();
        mMaxProgress = attrInit.getMaxProgress();

        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setColor(mRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);

        mRingPaint.setStrokeWidth(mRingWidth);

        mPaintWater = new Paint();
        mPaintWater.setStrokeWidth(1.0F);
        mPaintWater.setColor(mWaterColor);
        // mPaintWater.setColor(getResources().getColor(mWaterColor));
        mPaintWater.setAlpha(mWaterAlpha);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mFontSize);

        mHandler = new MyHandler(this);

    }

    public void animateWave() {
        if (!isWaving) {
            mWaveFactor = 0L;
            isWaving = true;
            mHandler.sendEmptyMessage(0);
        }
    }

    @SuppressLint({"DrawAllocation", "NewApi"})
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        width = height = (width < height) ? width : height;
        mAmplitude = width / 20f;

        mCenterPoint.x = width / 2;
        mCenterPoint.y = height / 2;
        {
            mRingWidth = mRingWidth == 0 ? width / 20 : mRingWidth;
            mProgress2WaterWidth = mProgress2WaterWidth == 0 ? mRingWidth * 0.6f
                    : mProgress2WaterWidth;
            mRingPaint.setStrokeWidth(mRingWidth);
            mTextPaint.setTextSize(mFontSize == 0 ? width / 5 : mFontSize);
            if (VERSION.SDK_INT == VERSION_CODES.JELLY_BEAN) {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            } else {
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
        }

        RectF oval = new RectF();
        oval.left = mRingWidth / 2;
        oval.top = mRingWidth / 2;
        oval.right = width - mRingWidth / 2;
        oval.bottom = height - mRingWidth / 2;

        if (isInEditMode()) {
            mRingPaint.setColor(mRingBgColor);
            canvas.drawArc(oval, -90, 360, false, mRingPaint);
            mRingPaint.setColor(mRingColor);
            canvas.drawArc(oval, -90, 90, false, mRingPaint);
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mCenterPoint.x
                    - mRingWidth - mProgress2WaterWidth, mPaintWater);
            return;
        }

        if ((width == 0) || (height == 0) || isInEditMode()) {
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, width / 2
                    - mProgress2WaterWidth - mRingWidth, mPaintWater);
            return;
        }

        float waterPadding = mShowProgress ? mRingWidth + mProgress2WaterWidth
                : 0;
        int waterHeightCount = mShowProgress ? (int) (height - waterPadding * 2)
                : height;

        mWaveFactor++;
        if (mWaveFactor >= Integer.MAX_VALUE) {
            mWaveFactor = 0L;
        }

        mRingPaint.setColor(mRingBgColor);
        canvas.drawCircle(width / 2, width / 2, waterHeightCount / 2
                + waterPadding - mRingWidth / 2, mRingPaint);
        mRingPaint.setColor(mRingColor);
        canvas.drawArc(oval, -90, (mProgress * 1f) / mMaxProgress * 360f, false,
                mRingPaint);

        float waterHeight = waterHeightCount * (1 - (mProgress * 1f) / mMaxProgress)
                + waterPadding;
        int staticHeight = (int) (waterHeight + mAmplitude);
        Path mPath = new Path();
        mPath.reset();
        if (mShowProgress) {
            mPath.addCircle(width / 2, width / 2, waterHeightCount / 2,
                    Direction.CCW);
        } else {
            mPath.addCircle(width / 2, width / 2, waterHeightCount / 2,
                    Direction.CCW);
        }
        canvas.clipPath(mPath, Op.REPLACE);
        Paint bgPaint = new Paint();
        bgPaint.setColor(mWaterBgColor);
        canvas.drawRect(waterPadding, waterPadding, waterHeightCount
                + waterPadding, waterHeightCount + waterPadding, bgPaint);
        canvas.drawRect(waterPadding, staticHeight, waterHeightCount
                + waterPadding, waterHeightCount + waterPadding, mPaintWater);

        int xToBeDrawed = (int) waterPadding;
        int waveHeight = (int) (waterHeight - mAmplitude * Math.sin(Math.PI * (2.0F * (xToBeDrawed + (mWaveFactor * width) * mWaveSpeed)) / width));
        int newWaveHeight = waveHeight;
        while (true) {
            if (xToBeDrawed >= waterHeightCount + waterPadding) {
                break;
            }
            newWaveHeight = (int) (waterHeight - mAmplitude
                    * Math.sin(Math.PI
                    * (crestCount * (xToBeDrawed + (mWaveFactor * waterHeightCount)
                    * mWaveSpeed)) / waterHeightCount));

            canvas.drawLine(xToBeDrawed, waveHeight, xToBeDrawed + 1,
                    newWaveHeight, mPaintWater);

            canvas.drawLine(xToBeDrawed, newWaveHeight, xToBeDrawed + 1,
                    staticHeight, mPaintWater);
            xToBeDrawed++;
            waveHeight = newWaveHeight;
        }
        if (mShowNumerical) {
            String progressTxt = String.format("%.0f", (mProgress * 1f) / mMaxProgress
                    * 100f)
                    + "%";
            float mTxtWidth = mTextPaint.measureText(progressTxt, 0,
                    progressTxt.length());
            canvas.drawText(progressTxt, mCenterPoint.x - mTxtWidth / 2,
                    mCenterPoint.x * 1.5f - mFontSize / 2, mTextPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = widthMeasureSpec;
        int height = heightMeasureSpec;
        width = height = (width < height) ? width : height;
        setMeasuredDimension(width, height);
    }

    public void setAmplitude(float amplitude) {
        mAmplitude = amplitude;
    }

    public void setWaterAlpha(float alpha) {
        mWaterAlpha = (int) (255.0F * alpha);
        mPaintWater.setAlpha(mWaterAlpha);
    }

    public void setWaterColor(int color) {
        mWaterColor = color;
    }

    /**
     * set current progress
     */
    public void setProgress(int progress) {
        progress = progress > 100 ? 100 : progress < 0 ? 0 : progress;
        mProgress = progress;
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setWaveSpeed(float speed) {
        mWaveSpeed = speed;
    }

    public void setShowProgress(boolean b) {
        mShowProgress = b;
    }

    public void setShowNumerical(boolean b) {
        mShowNumerical = b;
    }

    /**
     * @param mRingColor
     */
    public void setmRingColor(int mRingColor) {
        this.mRingColor = mRingColor;
    }

    /**
     * @param mRingBgColor
     */
    public void setmRingBgColor(int mRingBgColor) {
        this.mRingBgColor = mRingBgColor;
    }

    /**
     * @param mWaterColor
     */
    public void setmWaterColor(int mWaterColor) {
        this.mWaterColor = mWaterColor;
    }

    /**
     * @param mWaterBgColor
     */
    public void setWaterBgColor(int mWaterBgColor) {
        this.mWaterBgColor = mWaterBgColor;
    }

    /**
     * @param mFontSize
     */
    public void setFontSize(int mFontSize) {
        this.mFontSize = mFontSize;
    }

    /**
     * @param mTextColor
     */
    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    /**
     * @param mMaxProgress
     */
    public void setMaxProgress(int mMaxProgress) {
        this.mMaxProgress = mMaxProgress;
    }

    /**
     * @param crestCount
     */
    public void setCrestCount(float crestCount) {
        this.crestCount = crestCount;
    }

    /**
     * @param mRingWidth
     */
    public void setRingWidth(float mRingWidth) {
        this.mRingWidth = mRingWidth;
    }

    /**
     * @param mProgress2WaterWidth
     */
    public void setProgress2WaterWidth(float mProgress2WaterWidth) {
        this.mProgress2WaterWidth = mProgress2WaterWidth;
    }

    public static int Dp2Px(Context context, float dp) {
        final float dpi = context.getResources().getDisplayMetrics().density;
        return (int) (dp * dpi + 0.5f);
    }

    public static int Px2Dp(Context context, float px) {
        final float dpi = context.getResources().getDisplayMetrics().density;
        return (int) (px / dpi + 0.5f);
    }

    static class WaterWaveAttrInit {

        private int progressWidth;
        private int progressColor;
        private int progressBgColor;
        private int waterWaveColor;
        private int waterWaveBgColor;
        private int progress2WaterWidth;
        private boolean showProgress;
        private boolean showNumerical;
        private int fontSize;
        private int textColor;
        private int progress;
        private int maxProgress;

        @SuppressLint("Recycle")
        public WaterWaveAttrInit(Context context, AttributeSet attrs, int defStyle) {
            progressWidth = 5;//typedArray.getDimensionPixelOffset(android.R.styleable.WaterWaveProgress_progressWidth, 0);
            progressColor =  0xFF33B5E5;
            progressBgColor = 0xFFBEBEBE;//typedArray.getColor(R.styleable.WaterWaveProgress_progressBgColor, 0xFFBEBEBE);
            waterWaveColor = 0XFFc06d60;//typedArray.getColor(R.styleable.WaterWaveProgress_waterWaveColor, 0XFF4BBDFE);
            waterWaveBgColor = 0xFFae3b30;//typedArray.getColor(R.styleable.WaterWaveProgress_waterWaveBgColor, );
            progress2WaterWidth =0;// typedArray.getDimensionPixelOffset(R.styleable.WaterWaveProgress_progress2WaterWidth, 0);
            showProgress = true;//typedArray.getBoolean(R.styleable.WaterWaveProgress_showProgress, true);
            showNumerical = true;//typedArray.getBoolean(R.styleable.WaterWaveProgress_showNumerical, true);
            fontSize = 0;//typedArray.getDimensionPixelOffset(R.styleable.WaterWaveProgress_fontSize, 0);
            textColor = 0xFFFFFFFF;//typedArray.getColor(R.styleable.WaterWaveProgress_textColor, 0xFFFFFFFF);
            progress = 50;//typedArray.getInteger(R.styleable.WaterWaveProgress_progress, 50);
            maxProgress = 100;//typedArray.getInteger(R.styleable.WaterWaveProgress_maxProgress, 100);
        }

        public int getProgressWidth() {
            return progressWidth;
        }

        public int getProgressColor() {
            return progressColor;
        }

        public int getProgressBgColor() {
            return progressBgColor;
        }

        public int getWaterWaveColor() {
            return waterWaveColor;
        }

        public int getWaterWaveBgColor() {
            return waterWaveBgColor;
        }

        public int getProgress2WaterWidth() {
            return progress2WaterWidth;
        }

        public boolean isShowProgress() {
            return showProgress;
        }

        public boolean isShowNumerical() {
            return showNumerical;
        }

        public int getFontSize() {
            return fontSize;
        }

        public int getTextColor() {
            return textColor;
        }

        public int getProgress() {
            return progress;
        }

        public int getMaxProgress() {
            return maxProgress;
        }
    }
}