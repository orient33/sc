package com.sudoteam.securitycenter.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.sudoteam.securitycenter.R;

public class WaterWaveAttrInit {

	private int waterWaveColor;
	private int waterWaveBgColor;
	private int progress;
	private int maxProgress;
	private int fontSize;
	private int textColor;

	@SuppressLint("Recycle")
	public WaterWaveAttrInit(Context context, AttributeSet attrs, int defStyle) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.BatteryWaterWaveView, defStyle, 0);
		waterWaveColor = typedArray.getColor(
				R.styleable.BatteryWaterWaveView_bwaterWaveColor, 0XFFc06d60);
		waterWaveBgColor = typedArray.getColor(
				R.styleable.BatteryWaterWaveView_bwaterWaveBgColor, 0xc1372a);
		progress = typedArray.getInteger(
				R.styleable.BatteryWaterWaveView_bprogress, 0);
		maxProgress = typedArray.getInteger(
				R.styleable.BatteryWaterWaveView_bmaxProgress, 100);
		fontSize = typedArray.getDimensionPixelOffset(
				R.styleable.BatteryWaterWaveView_bfontSize, 45);
		textColor = typedArray.getColor(
				R.styleable.BatteryWaterWaveView_btextColor, 0xFFFFFFFF);
		typedArray.recycle();
	}

	public int getWaterWaveColor() {
		return waterWaveColor;
	}

	public int getWaterWaveBgColor() {
		return waterWaveBgColor;
	}

	public int getProgress() {
		return progress;
	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public int getFontSize() {
		return fontSize;
	}

	public int getTextColor() {
		return textColor;
	}

}
