package com.sudoteam.securitycenter.optimizer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
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
import com.sudoteam.securitycenter.Util;
import com.sudoteam.securitycenter.optimizer.ClearCacheAdapter.AppCacheInfo;

public class ClearCacheFragment extends MyFragment implements View.OnClickListener,
	AdapterView.OnItemClickListener {
	static final int UPDATE_CHECKBOX = 110;
	static final int NO_CACHE= 0,
			CLEAR_OK = 1;

	ClearCacheFragment(){}
	
	Activity mActivity;
	ListView mListView;
	Button mClearAll;
	CheckBox mCheckboxAll;
	ClearCacheAdapter mAdapter;
	final Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case NO_CACHE:
				showToast(getString(R.string.find_no_cache));
				mActivity.onBackPressed();
				break;
			case CLEAR_OK:
				showToast(msg.obj.toString());
				mActivity.onBackPressed();
				break;
			case UPDATE_CHECKBOX:
				boolean checked = mAdapter.isSelectAll();
				mCheckboxAll.setChecked(checked);
			}
		}
	};
	private ISelectCallback callback = new ISelectCallback(){
		@Override
		public void selectChanged() {
			mHandler.sendEmptyMessage(UPDATE_CHECKBOX);
		}};

	@Override
	public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b){
		mActivity = getActivity();
		View v = li.inflate(R.layout.clear_cache, null);
		mClearAll = (Button)v.findViewById(R.id.clear_all_button);
		mCheckboxAll = (CheckBox)v.findViewById(R.id.clear_cb);
		mListView = (ListView)v.findViewById(R.id.clear_list);
		mAdapter = ClearCacheAdapter.get(mActivity, callback);
		mAdapter.setCallback(callback);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mClearAll.setOnClickListener(this);
		mCheckboxAll.setOnClickListener(this);
		mCheckboxAll.setFocusable(false);
		return v;
	}
	@Override
	public void onResume(){
		super.onResume();
		mHandler.sendEmptyMessageDelayed(UPDATE_CHECKBOX, 10);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.clear_all_button:
			int c = mAdapter.optimizeSelect(mCheckboxAll.isChecked());
			Message m = mHandler.obtainMessage(CLEAR_OK,
					getString(R.string.clear_ok) + Formatter.formatFileSize(mActivity, c));
			m.sendToTarget();
			break;
		case R.id.clear_cb:
			boolean now = mCheckboxAll.isChecked();
			((ClearCacheAdapter)mAdapter).select(now);
			break;
		}
	}

	void showToast(String msg){
		Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final AppCacheInfo aci = mAdapter.getItem(position);
		if (aci != null)
			Util.toAppDetail(mActivity, aci.info.packageName);
	}
}
