package com.sudoteam.securitycenter.optimizer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.sudoteam.securitycenter.MyFragment;
import com.sudoteam.securitycenter.R;

public class KillProcessFragment extends MyFragment implements
		View.OnClickListener, AdapterView.OnItemClickListener {
	static final int UPDATE_CHECKBOX = 0;
	
	KillProcessFragment(){}
	
	Activity mActivity;
	ListView mListView;
	Button mClearAll;
	CheckBox mCheckboxAll;
	KillProcessAdapter mAdapter;
	final Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case UPDATE_CHECKBOX:
				boolean checked = mAdapter.isSelectAll();
				mCheckboxAll.setChecked(checked);
			}
		}
	};
	void finish(){
		Toast.makeText(mActivity, "how to finish a Fragment! -:(", Toast.LENGTH_SHORT).show();
	}

	@Override
	public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b){
		mActivity = getActivity();
		View v = li.inflate(R.layout.clear_cache, null);
		mClearAll = (Button)v.findViewById(R.id.clear_all_button);
		mCheckboxAll = (CheckBox)v.findViewById(R.id.clear_cb);
		mListView = (ListView)v.findViewById(R.id.clear_list);
		mAdapter = KillProcessAdapter.get(mActivity, mHandler);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mClearAll.setOnClickListener(this);
		mCheckboxAll.setOnClickListener(this);
		mCheckboxAll.setFocusable(false);
		mHandler.sendEmptyMessageDelayed(UPDATE_CHECKBOX, 200);
		return v;
	}
	@Override
	public void onResume(){
		super.onResume();
		new AsyncTask<Object, Object, Object>(){
			@Override
			protected Object doInBackground(Object... params) {
				mAdapter.doCheck(mHandler);
				return null;
			}
			@Override
			protected void onPostExecute(Object l){
				mAdapter.notifyDataSetChanged();
			}
		}.execute("");
	}
	@Override
	public void onClick(View v) {
		boolean now = mCheckboxAll.isChecked();
		int id = v.getId();
			switch (id) {
			case R.id.clear_cb:		//全选框
				mAdapter.select(now);
				break;
			case R.id.clear_all_button: // 一键清理
				mAdapter.optimizeSelect(now);
				break;
		}
	}

	void showToast(String msg){
		Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		KillProcessDialog d = new KillProcessDialog(mAdapter.getItem(position));
		d.show(mActivity.getFragmentManager(),"KillProcessDialog");
	}
}
