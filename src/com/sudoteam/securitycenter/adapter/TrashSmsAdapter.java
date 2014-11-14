package com.sudoteam.securitycenter.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import java.text.SimpleDateFormat;

import com.sudoteam.securitycenter.entity.TrashSms;

public class TrashSmsAdapter extends BaseAdapter {

    private List<TrashSms> sms;
    private LayoutInflater inflater;
    private ViewHolder holder;
    private SimpleDateFormat format;
    public TrashSmsAdapter(Context context,List<TrashSms> sms) {
        this.sms = sms;
        inflater = LayoutInflater.from(context);
        format = new SimpleDateFormat("MM月dd日");
    }

    @Override
    public int getCount() {
        return sms == null ? 0 :sms.size();
    }

    @Override
    public Object getItem(int i) {
        return sms == null ? null : sms.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null){

            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_trash_sms_list,null);
            holder.smsHolder = (TextView)view.findViewById(R.id.sms_text);
            holder.smsNumber = (TextView)view.findViewById(R.id.sms_number);
            holder.smsTime = (TextView)view.findViewById(R.id.sms_time);
            holder.smsSelect = (Button)view.findViewById(R.id.sms_select);
            view.setTag(holder);

        }else {

            holder = (ViewHolder)view.getTag();

        }

        Log.i("huayang","time = " + sms.get(i).getRecvTime());
        holder.smsHolder.setText(sms.get(i).getContent());
        holder.smsNumber.setText(sms.get(i).getAddrFrom());
        holder.smsTime.setText(format.
                        format(Long.valueOf(sms.get(i).getRecvTime())));

        return view;
    }

    final class ViewHolder {

        TextView smsHolder;
        TextView smsNumber;
        TextView smsTime;
        Button smsSelect;
    }
}
