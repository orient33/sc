package com.sudoteam.securitycenter.datacell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.sudoteam.securitycenter.database.PowerManagerDbHelper;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;

@Table(name = "BatteryChargeCell")
public class BatteryChargeCell implements Serializable{

	@Column(column = "id")
	@NoAutoIncrement
	public int id;
	@Column(column = "startTime")
	public long startTime;
	@Column(column = "endTime")
	public long endTime;
	@Column(column = "startLevel")
	public int startLevel;
	@Column(column = "endLevel")
	public int endLevel;
	@Column(column = "chargeSpeed")
	public int chargeSpeed;
	@Column(column = "chargeStatus")
	public int chargeStatus;//充电方式

	public void insertSelfToDb(Context context) {
		PowerManagerDbHelper<BatteryChargeCell> batteryDbMgr = new PowerManagerDbHelper<BatteryChargeCell>(
				context);
		batteryDbMgr.insert(this);
	}

	public void deleteSelfToDb(Context context) {
		PowerManagerDbHelper<BatteryChargeCell> batteryDbMgr = new PowerManagerDbHelper<BatteryChargeCell>(
				context);
		batteryDbMgr.delete(this);
	}

	public void updateSelfToDb(Context context) {
		PowerManagerDbHelper<BatteryChargeCell> batteryDbMgr = new PowerManagerDbHelper<BatteryChargeCell>(
				context);
		batteryDbMgr.update(this);
	}

	public void querySelfToDb(Context context) {
		PowerManagerDbHelper<BatteryChargeCell> batteryDbMgr = new PowerManagerDbHelper<BatteryChargeCell>(
				context);
		batteryDbMgr.queryById(BatteryChargeCell.class, id);
	}

	public static ArrayList<BatteryChargeCell> queryAllToDb(Context context) {
		PowerManagerDbHelper<BatteryChargeCell> batteryDbMgr = new PowerManagerDbHelper<BatteryChargeCell>(
				context);
		ArrayList<BatteryChargeCell> batteryArrList = null;
		List<BatteryChargeCell> batteryList = batteryDbMgr
				.queryAll(BatteryChargeCell.class);
		if (batteryList != null) {
			batteryArrList = new ArrayList<BatteryChargeCell>();
			for (BatteryChargeCell cell : batteryList) {
				batteryArrList.add(cell);
			}
		}
		return batteryArrList;
	}

	public static int createUniqueId() {
		Long uniqueId = System.currentTimeMillis();
		return Math.abs(uniqueId.hashCode());
	}

	@Override
	public String toString() {
		return "BatteryChargeCell{" + "id=" + id + ", startTime=" + startTime
				+ ", endTime='" + endTime + '\'' + ", startLevel='"
				+ startLevel + '\'' + ", endLevel=" + endLevel
				+ ", chargeSpeed=" + chargeSpeed + ", chargeStatus="
				+ chargeStatus + '}';
	}

}
