package com.sudoteam.securitycenter.views;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sudoteam.securitycenter.R;

/**
 * @author Administrator
 * 
 */
public class BatteryWaterWaveView extends View {
	
	private static final boolean DEBUG = true;
	private static final String TAG = "WaterWaveView";
	
	private Paint mPaintWater = null;
	private Paint mTextStatusPaint = null;
	private Paint mTextSummaryPaint = null;
	private Paint mTextTimePaint = null;
	private int  mWaterColor;
	private int mWaterBgColor;
	private Bitmap mBatteryDisChargeBg = null;
	private Bitmap mBatteryChargeBg = null;
	
	int mProgress = 10;
	int  mMaxProgress = 100;
    private int mFontSize;
    private int mTextColor;
	private boolean mIsCharging;
	private int mHour;
	private int mMinute;
	
	private Point mCenterPoint;
	//浪峰个数
	float  crestCount = 1.5f;
	/** 产生波浪效果的因子 */
	private long mWaveFactor = 0L;
	/** 正在执行波浪动画 */
	private boolean isWaving = false;
	/** 振幅 */
	private float mAmplitude = 30.0F; // 20F
	/** 波浪的速度 */
	private float mWaveSpeed = 0.040F; // 0.020F
	/** 水的透明度 */
	private int mWaterAlpha = 100; // 255
	WaterWaveAttrInit attrInit;
	
	private MyHandler mHandler = null;
	private static class MyHandler extends Handler {
		private WeakReference<BatteryWaterWaveView> mWeakRef = null;
		private int refreshPeriod = 100;

		public MyHandler(BatteryWaterWaveView host) {
			mWeakRef = new WeakReference<BatteryWaterWaveView>(host);
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

	public BatteryWaterWaveView(Context paramContext) {
		super(paramContext);
	}

	public BatteryWaterWaveView(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, 0);
	}

	public BatteryWaterWaveView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		attrInit = new WaterWaveAttrInit(context, attrs, defStyleAttr);
		init(context);
	}

	@SuppressLint("NewApi")
	private void init(Context context) {
		
		if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
			setLayerType(View.LAYER_TYPE_HARDWARE, null);
		}
		mCenterPoint = new Point();//
		mWaterColor = attrInit.getWaterWaveColor();
		mWaterBgColor = attrInit.getWaterWaveBgColor();
		mProgress = attrInit.getProgress();
		mMaxProgress = attrInit.getMaxProgress();
	    mFontSize = attrInit.getFontSize();
	    mTextColor = attrInit.getTextColor();

		mPaintWater = new Paint();
		mPaintWater.setStrokeWidth(1.0F);
		mPaintWater.setColor(mWaterColor);
		mPaintWater.setAlpha(mWaterAlpha);
		
		mTextStatusPaint = new Paint();
		mTextStatusPaint.setAntiAlias(true);
		mTextStatusPaint.setColor(mTextColor);
		mTextStatusPaint.setStyle(Paint.Style.FILL);
		mTextStatusPaint.setTextSize(mFontSize);
		
//		mTextSummaryPaint = new Paint(mTextStatusPaint);
//		mTextSummaryPaint.setTextSize(textSize);
//		mTextTimePaint = new Paint(mTextStatusPaint);
		

		mBatteryDisChargeBg = BitmapFactory.decodeResource(getResources(), R.drawable.power_battery_discharge);
		mBatteryChargeBg = BitmapFactory.decodeResource(getResources(), R.drawable.power_battery_charging);
		mHandler = new MyHandler(this);
	}
	
	public void animateWave() {
		if (!isWaving) {
			mWaveFactor = 0L;
			isWaving = true;
			mHandler.sendEmptyMessage(0);
		}
	}
	
	public void stopAnimateWaving(){
		isWaving = true;
	}
	
	@SuppressLint({ "DrawAllocation", "NewApi" })
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int viewWidth = getWidth();
		int viewHeight = getHeight();
		int waveMaxWidth = mBatteryDisChargeBg.getWidth()-8;
		int waveMaxHeight = mBatteryDisChargeBg.getHeight();
		
		mCenterPoint.x = viewWidth / 2;
		mCenterPoint.y = viewHeight / 2;
		mAmplitude = waveMaxWidth / 30f;//振幅计算
		
		// 如果没有执行波浪动画，或者也没有指定容器宽高，就画个简单的矩形
		if ((waveMaxWidth == 0) || (waveMaxHeight == 0) || isInEditMode()) {
			canvas.drawRect(0, 0, waveMaxWidth, waveMaxHeight, mPaintWater);
			return;
		}
		mWaveFactor++;
		if (mWaveFactor >= Integer.MAX_VALUE) {
			mWaveFactor = 0L;
		}
		
		Paint batteryPaint = new Paint();
		if (mIsCharging) {
			canvas.drawBitmap(mBatteryChargeBg, (viewWidth-mBatteryDisChargeBg.getWidth())/2, (viewHeight-mBatteryDisChargeBg.getHeight())/2, batteryPaint);
		}else{
			canvas.drawBitmap(mBatteryDisChargeBg, (viewWidth-mBatteryDisChargeBg.getWidth())/2, (viewHeight-mBatteryDisChargeBg.getHeight())/2, batteryPaint);
		}
		
		int waterLeft = (viewWidth-waveMaxWidth)/2;
		int waterTop = (viewHeight-waveMaxHeight)/2;
		int waterRight = (viewWidth+waveMaxWidth)/2;
		int waterBottom = (viewHeight+waveMaxHeight)/2;
		
		int waterPaddingX = (viewWidth-waveMaxWidth)/2;
		int waterPaddingY = (viewHeight-waveMaxHeight)/2;
		
		RectF batteryRectF = new RectF(waterLeft,waterTop,waterRight,waterBottom);
	
		// 计算出水的高度
		float waterCurrHeight = waveMaxHeight * (1 - (mProgress*1f) / mMaxProgress)+waterPaddingY+64;
		int staticHeight = (int) (waterCurrHeight + mAmplitude);
		
		RectF bfRect = new RectF(waterLeft,waterTop+64,waterRight,waterBottom-4);
		Path mPath = new Path();
		mPath.reset();
		mPath.addRoundRect(bfRect, 30, 30,Direction.CCW);
		// canvas添加限制,让接下来的绘制都在园内
		canvas.clipPath(mPath, Op.REPLACE);
		
		Paint bgPaint = new Paint();
		bgPaint.setColor(mWaterBgColor);
		// 绘制背景
		canvas.drawRect(batteryRectF, bgPaint);
		// 绘制静止的水
		canvas.drawRect(waterLeft, staticHeight, waterRight, waveMaxHeight+waterPaddingY, mPaintWater);
		// 待绘制的波浪线的x坐标
		int xToBeDrawed = waterPaddingX;
		int waveCurrHeight = (int) (waterCurrHeight - mAmplitude
				* Math.sin(Math.PI* (2.0F * (xToBeDrawed + (mWaveFactor * waveMaxWidth)* mWaveSpeed)) / waveMaxWidth));
		// 波浪线新的高度
		int newWaveHeight = waveCurrHeight;
		while (true) {
			if (xToBeDrawed >= waveMaxWidth+waterPaddingX) {
				break;
			}
			// 根据当前x值计算波浪线新的高度
			newWaveHeight = (int) (waterCurrHeight - mAmplitude
					* Math.sin(Math.PI * (crestCount * (xToBeDrawed + (mWaveFactor * waveMaxWidth)* mWaveSpeed)) / waveMaxWidth));

			// 先画出梯形的顶边
			canvas.drawLine(xToBeDrawed, waveCurrHeight, xToBeDrawed + 1,newWaveHeight, mPaintWater);
			// 画出动态变化的柱子部分
			canvas.drawLine(xToBeDrawed, newWaveHeight, xToBeDrawed + 1,staticHeight, mPaintWater);
			xToBeDrawed++;
			waveCurrHeight = newWaveHeight;
		}
		
		if (mIsCharging) {
			String batteryStatusTxt = "电池充电时间";
			float statusTxtWidth = mTextStatusPaint.measureText(batteryStatusTxt, 0,batteryStatusTxt.length());
			canvas.drawText(batteryStatusTxt, mCenterPoint.x - statusTxtWidth / 2,mCenterPoint.y * 1.5f - mFontSize / 2, mTextStatusPaint);
		}else{
			String batteryStatusTxt = "电池可用时间";
			float statusTxtWidth = mTextStatusPaint.measureText(batteryStatusTxt, 0,batteryStatusTxt.length());
			canvas.drawText(batteryStatusTxt, mCenterPoint.x - statusTxtWidth / 2,mCenterPoint.y * 1.5f - mFontSize / 2, mTextStatusPaint);
		}
		
		String batterySummaryTxt = mHour+"时"+mMinute+"分";
		float summaryTxtWidth = mTextStatusPaint.measureText(batterySummaryTxt, 0,batterySummaryTxt.length());
		canvas.drawText(batterySummaryTxt, mCenterPoint.x - summaryTxtWidth / 2,mCenterPoint.y * 1.5f - mFontSize*2 , mTextStatusPaint);
		
	}
	
	public void setBatteryCharging(boolean isCharging){
		mIsCharging = isCharging;
		invalidate();
	}
	
	/**
	 * 设置当前进度
	 */
	public void setBatteryLevel(int progress) {
		progress = progress > 100 ? 100 : progress < 0 ? 0 : progress;
		mProgress = progress;
		invalidate();
	}
	
	public void setBatteryHour(int hour){
		mHour = hour;
		invalidate();
	}
	
	public void setBatteryMinute(int mimute){
		mMinute = mimute;
		invalidate();
	}

	private static void log_e(String msg){
		if(DEBUG)
			Log.e(TAG, msg);
	}
	

}


