package com.sudoteam.securitycenter.optimizer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sudoteam.securitycenter.MyFragment;
import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

public class ClearFragment extends MyFragment implements View.OnClickListener{

	public static final int MSG_UPDATE_UI = 10;
	Activity mmActivity;
	TextView mmSD;
	Button mmButton;
	public ClearFragment() { }
	private Handler mmHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case MSG_UPDATE_UI:
				updateButton();
				break;
			}
		}
	};
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		mmActivity = getActivity();
	}
	static final int names[] = {R.string.app_cache,R.string.sdcard_cache/*,R.string.kill_app*/};
	static final int summarys[] = {R.string.app_cache_summary, R.string.sdcard_cache_summary/*,R.string.kill_app_summary*/};
	MyAdapter mAdapter;
	@Override
	public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b){
		View v = li.inflate(R.layout.clear_fragment, null);
		mmSD = (TextView)v.findViewById(R.id.sd_info);
		mmButton = (Button)v.findViewById(R.id.check_clear);
		ListView mmListView = (ListView)v.findViewById(R.id.opt_list);
		mmButton.setOnClickListener(this);//开始扫描 or 一键清理
		mAdapter= new MyAdapter();
		mAdapter.bind();
		mmListView.setAdapter(mAdapter);
		
		setSDcardInfo(mmSD);
		return v;
	}
	@Override
	public void onResume(){
		refreshCacheInfo();
		updateButton();
		mAdapter.notifyDataSetChanged();
		super.onResume();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mAdapter.unbind();
		mmHandler.removeMessages(MSG_UPDATE_UI);
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.check_clear:
			boolean scan = !mAdapter.canClear();
			new AAsyncTask(v, scan).execute(mmHandler);
			break;
		}
	}
	private void updateButton(){
		if(mAdapter.canClear()){
			mmButton.setText(R.string.clear_all);
		}else{
			mmButton.setText(R.string.start_scan);
		}
	}

	private void refreshCacheInfo(){
		for(OneCheckItem is : list){
			if(is.task == null) continue;
			int rs = is.task.getCurrentCount();
			is.useResult(rs);
		}
	}
	
	private void setSDcardInfo(TextView tv){
		StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long bs = sf.getBlockSizeLong();
		long t = sf.getBlockCountLong() * bs, f = sf.getFreeBlocksLong() * bs;
		String total = Formatter.formatFileSize(mmActivity, t);
		String free = Formatter.formatFileSize(mmActivity, f);
		String text = getString(R.string.sd_info, free, total); 
		tv.setText(text);
	}

	final List<OneCheckItem> list = new ArrayList<OneCheckItem>(names.length);

	class MyAdapter extends BaseAdapter{
		void bind(){
			if (list.size() > 0)
				return;
			Fragment ff[] = { new ClearCacheFragment(), null/*, KillProcessFragment.get()*/ };
			IScan scans[] = { ClearCacheAdapter.get(mmActivity, mmHandler), null /*,KillProcessAdapter.get(mmActivity, mmHandler)*/};
			for (int ii = 0; ii < names.length; ++ii) {
				OneCheckItem oci = new OneCheckItem(names[ii], summarys[ii], ff[ii], scans[ii]);
				list.add(oci);
			}
		}
		void unbind(){
			for(OneCheckItem scan : list){
				if(scan.task != null)
					scan.task.destoryResult();
			}
			list.clear();
		}
		boolean canClear(){
			for(OneCheckItem item : list)
				if(item.canClear())
					return true;
			return false;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public OneCheckItem getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View cv, ViewGroup parent) {
			final OneCheckItem oci = getItem(position);
			ViewHold vh ;
			if(cv == null){
				cv = View.inflate(mmActivity, R.layout.opt_fragment_item, null);
				vh = new ViewHold(cv, oci.nameId);
				cv.setTag(vh);
			}else
				vh = (ViewHold)cv.getTag();

			if(!TextUtils.isEmpty(oci.sizeInfo)){
				vh.summary.setText(cv.getContext().getString(oci.summaryId, oci.sizeInfo));
				vh.image.setVisibility(View.VISIBLE);
				cv.setOnClickListener(oci);
			}else{
				vh.summary.setText("");
				cv.setOnClickListener(null);
				vh.image.setVisibility(View.GONE);
			}
			return cv;
		}
		class ViewHold{
			final TextView name, summary;
			final ImageView image;
			ViewHold(View root, int nameId){
				name = (TextView)root.findViewById(R.id.opt_item_name);
				summary = (TextView)root.findViewById(R.id.opt_item_summary);
				image =  (ImageView)root.findViewById(R.id.opt_item_arrow);;
				name.setText(nameId);
			}
		}
	};
	
	/** 一个 可扫描的条目， 如 应用缓存 垃圾缓存 进程清理 等*/
	class OneCheckItem implements View.OnClickListener{
		/** 点击item需要转到的 fragment <br>*/
		final Fragment fragment;
		/** item名称的string的id <br>*/
		final int nameId;
		/** item的扫描结果描述 带参数的string id <br>*/
		final int summaryId;
		/** item的扫描结果 <br>*/
		String sizeInfo;//可清理进程数/垃圾大小
		
		final IScan task;
		
		OneCheckItem(int name, int summary, Fragment f, IScan scan){
			nameId = name;
			summaryId = summary;
			fragment = f;
			task = scan;
			
		}
		public void useResult(int rs){
			if(rs == 0)
				sizeInfo = null;
			else{
				sizeInfo = Formatter.formatFileSize(mmActivity, rs);
			}
		}
		public boolean canClear(){
			return !TextUtils.isEmpty(sizeInfo);
		}

		@Override
		public void onClick(View v) {
			Util.replaceNewFragment(mmActivity, R.id.container, fragment);
		}

	}
	
	class AAsyncTask extends AsyncTask<Handler, String, Object>{
		final View v ;
		final boolean scan ; // scan or doclear

		AAsyncTask(View vv, boolean s) {
			v = vv;
			scan = s;
		}
		@Override
		protected void onPreExecute(){
			v.setEnabled(false);
		}
		
		protected Object doInBackground(Handler... h) {
			
			for(int ii = 0 ; ii < mAdapter.getCount() ; ++ii){
				final OneCheckItem oci = mAdapter.getItem(ii);
				if(oci.task == null)
					continue;
				SystemClock.sleep(500);
				int rs =0;
				if (scan )
					rs = oci.task.doCheck(h[0]);
				else
					oci.task.optimizeSelect(true);
				Util.i("scan task , result == "+ rs);
				oci.useResult(rs);
				h[0].post(new Runnable(){
					public void run(){
						mAdapter.notifyDataSetChanged();
					}
				});
			}
			Util.i("scan task complete !.");
			h[0].sendEmptyMessage(MSG_UPDATE_UI);
			return null;
		}
		@Override
		protected void onPostExecute(Object o){
			v.setEnabled(true);
		}
	}
}