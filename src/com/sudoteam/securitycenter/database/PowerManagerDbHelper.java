package com.sudoteam.securitycenter.database;

import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import android.content.Context;
import android.util.Log;

public class PowerManagerDbHelper<T> {
	private static final boolean DEBUG = true;
	private static final String TAG = "PowerPlanDBManager";
	
	public static final String DB_NAME = "power_manager.db";
	public static final String DB_OPERATE = "power_plan_db_manager";
	
	private Context mContext;
	private static DbUtils sDbUtils;
	
	public PowerManagerDbHelper(Context context){
		mContext = context;
		if(sDbUtils == null){
			synchronized (PowerManagerDbHelper.class) {
				if(sDbUtils == null){
					createDbUtils(context);
				}
			}
		}
	}
	
	private static void createDbUtils(Context context){
		sDbUtils = DbUtils.create(context,DB_NAME);
		sDbUtils.configAllowTransaction(true);
	}
	
	
	public void insert(T insertEntity){
		try {  
			sDbUtils.save(insertEntity);  
        } catch (DbException e) {  
        	if(DEBUG)Log.e(TAG,"insert Exception...");
        }  
		
	}
	
	public void delete(T deleteEntity){
		try {  
			sDbUtils.delete(deleteEntity);  
        } catch (DbException e) {  
        	if(DEBUG)Log.e(TAG,"delete Exception...");
        }  
	}
	
	public void update(T updateEntity){
		try {  
			sDbUtils.update(updateEntity);  
        } catch (DbException e) {  
        	if(DEBUG)Log.e(TAG,"update Exception...");
        }  
	}
	
	public T queryById(Class<T> queryClazz,int queryId){
		T entity = null;
		try {  
			entity = sDbUtils.findById(queryClazz,queryId);
        } catch (DbException e) {  
        	if(DEBUG)Log.e(TAG,"queryById DbException...");
        }
		return entity;
	}
	
	public List<T> queryAll(Class<T> queryClass){
		List<T> listEntity = null;
		try {  
			listEntity = sDbUtils.findAll(queryClass);//通过类型查找
		} catch (DbException e) {  
			if(DEBUG)Log.e(TAG,"queryAll DbException...");
		}  
		return listEntity;
	}
	
	public void close(){
		sDbUtils.close();
	}
	

}
