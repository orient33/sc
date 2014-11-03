package com.sudoteam.securitycenter.mac;
import com.sudoteam.securitycenter.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AppOpsManager;
import android.app.AppOpsManager.PackageOps;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AppsUsingCertainOpFragment extends Fragment {
	private int op;
	private List<Map<String,Object>> apps;
	private ListView listView;
	public AppsUsingCertainOpFragment(int op) {
		super();
		this.op = op;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		return inflater.inflate(R.layout.fragment_apps_using_certain_op, container, false);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();		
		
		ListView listView = (ListView) getActivity().findViewById(R.id.listView);
		apps = getApps();
		listView.setAdapter(new DataAdapter(getActivity()));
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				String mCurrentPkgName =  (String)apps.get(position).get("packagename");
		        Bundle args = new Bundle();
		        args.putString(AppOpsDetails.ARG_PACKAGE_NAME, mCurrentPkgName);
		    	Intent intent = new Intent(getActivity(), PermissionsOfApp.class);
		    	intent.putExtra("packname", args);
		    	getActivity().startActivity(intent);
			}
		});
	}
	
	private List<Map<String,Object>> getApps(){	
		AppOpsManager mAppOps = (AppOpsManager) getActivity().getSystemService(Context.APP_OPS_SERVICE);
		PackageManager pm = getActivity().getPackageManager();
		List<PackageOps> packages = mAppOps.getPackagesForOps(new int[]{op});
		
		List<Map<String,Object>> mApps = new ArrayList<Map<String,Object>>();
		Map<String,Object> app;
		for(int i=0; i<packages.size(); i++){
			String packageName = packages.get(i).getPackageName();
			Drawable icon = null;
			try {
				icon = pm.getApplicationIcon(packageName);
			} catch (NameNotFoundException e) {
				
			}
			app = new HashMap<String,Object>();
			app.put("packagename", packageName);
			app.put("icon", icon);
			mApps.add(app);
		}
		return mApps;
	}
	
	private class DataAdapter extends BaseAdapter{
		private LayoutInflater mInflator;
		public DataAdapter(Context cxt){
			this.mInflator = LayoutInflater.from(cxt);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return apps.size();
		}

		@Override
		public Object getItem(int position) {
			return apps.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(null == convertView){
				holder = new ViewHolder();
				convertView = mInflator.inflate(R.layout.item_of_activity_appsofpermission, null);
				holder.packageName = (TextView) convertView.findViewById(R.id.packname);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.packageName.setText((String)apps.get(position).get("packagename"));
			holder.icon.setImageDrawable((Drawable)apps.get(position).get("icon"));
			return convertView;
		}
		
	}
	
	static class ViewHolder{
		private TextView packageName;
		private ImageView icon;
	}
}
