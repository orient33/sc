package com.sudoteam.securitycenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.sudoteam.securitycenter.entity.ScanLogResult;

import com.sudoteam.securitycenter.R;
/**
 * Created by huayang on 14-10-29.
 */
public class ScanLogAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<ScanLogResult> datas;

    public ScanLogAdapter(Context context,List<ScanLogResult> datas) {

        inflater = LayoutInflater.from(context);
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas == null ? null : datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView == null){

            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.scan_log_item,null);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.time = (TextView) convertView.findViewById(R.id.time);
        holder.result = (TextView) convertView.findViewById(R.id.result);

        holder.time.setText(datas.get(position).getTime());
        holder.result.setText(datas.get(position).getResult());

        return convertView;
    }

    final class ViewHolder {

        TextView time;
        TextView result;
    }
}
