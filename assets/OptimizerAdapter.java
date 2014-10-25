package com.sudoteam.securitycenter.optimizer;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sudoteam.securitycenter.R;

public abstract class OptimizerAdapter extends BaseAdapter implements IScan {
/*
	ArrayList<Optimizer> mList;
	public static class Optimizer{
		boolean selected = true;
	}
	
	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Optimizer getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	boolean isSelectAll() {
		if (mList != null)
			for (Optimizer item : mList)
				if (!item.selected)
					return false;
		return true;
	}

	void select(final boolean check){
		if(mList == null) return;
		for(Optimizer item : mList){
			item.selected = check;
		}
		notifyDataSetChanged();
	}
	*/
	/***/
//	void refresh(){
//		if(mList != null)
//			mList.clear();
//		mList = (List<Optimizer>)getItems();
//		notifyDataSetChanged();
//	}

	/**
	 * @id 点击view的id
	 * @now 当前的全选框是否勾选了
	 * */
	void whenClick(int id, boolean now) {
		switch (id) {
		case R.id.clear_cb:		//全选框
			select(now);
			break;
		case R.id.clear_all_button: // 一键清理
			optimizeSelect(now);
			break;
		}
	}
	
	// 下面时子类需要 覆盖的 方法
	/**子类需要实现它 实现具体该怎么优化 如 删除缓存 或 杀死进程*/
	abstract int optimizeSelect(boolean all);
	abstract void clickItem(int pos);

	public abstract View getView(int position, View convertView, ViewGroup parent);

	public abstract int doCheck(Handler h) ;

	
	public abstract int getCurrentCount() ;

	@Override
	public void destoryResult() {
		if (mList != null)
			mList.clear();
	}

	@Override
	public void doClear() {
		optimizeSelect(true);
	}

	AsyncTask<Void, Void, Void> task;
	public void doCheckOnUI() {
		if (task != null)
			return;
		task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute(){
				if(mList!=null) mList.clear();
				notifyDataSetChanged();
			}
			protected Void doInBackground(Void... params) {
				doCheck(null);
				return null;
			}

			@Override
			protected void onPostExecute(Void v) {
				notifyDataSetChanged();
				task = null;
			}
		};
		task.execute();
	}
}
