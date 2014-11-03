package com.sudoteam.securitycenter.mac;
import com.sudoteam.securitycenter.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AppOpsManager;
import android.app.AppOpsManager.PackageOps;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class OpsListFragment extends Fragment {
	ListView listView = null;
	private List<Map<String,Object>> opList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View contextView = inflater.inflate(R.layout.activity_perms_list, container, false);
		listView = (ListView) contextView.findViewById(R.id.listView);		
		
		opList = getOps();
		listView.setAdapter(new DataAdapter(getActivity()));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String,Object> data = opList.get(position);
				Intent intent = new Intent(getActivity(),AppsOfPermission.class);
				intent.putExtra("op", (Integer)data.get("op"));
				startActivity(intent);
			}
		});
		
		return contextView;
	}
	
	private List<Map<String,Object>> getOps(){
		AppOpsManager mAppOps = (AppOpsManager) getActivity().getSystemService(Context.APP_OPS_SERVICE);
		List<Map<String,Object>> opList = new ArrayList<Map<String,Object>>();
		Map<String,Object> op;
		int ops[] = Constants.template.ops;
		for(int i=0; i<ops.length; i++){
			List<PackageOps> packages = mAppOps.getPackagesForOps(new int[]{ops[i]});
			if(null == packages || packages.size() == 0) continue;
			op = new HashMap<String, Object>();
			op.put("name", AppOpsManager.opToName(ops[i]));
			op.put("op", ops[i]);
			opList.add(op);
		}
		return opList;
	}
	
	private class DataAdapter extends BaseAdapter{
		 private LayoutInflater mInflater = null;
	     private DataAdapter(Context context){
	            this.mInflater = LayoutInflater.from(context);
	     }

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return opList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(null == convertView){
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_of_perm_list_activity, null);
				viewHolder.opName = (TextView)convertView.findViewById(R.id.op_name);
				convertView.setTag(viewHolder);
			}else{
				viewHolder =  (ViewHolder)convertView.getTag();
			}
			viewHolder.opName.setText((String)opList.get(position).get("name"));
			return convertView;
		}
	}
	
    static class ViewHolder{
        public TextView opName;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
