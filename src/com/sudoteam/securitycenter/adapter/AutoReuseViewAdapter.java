package com.sudoteam.securitycenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by huhuajun on 14-10-30.
 */
public abstract class AutoReuseViewAdapter extends BaseAdapter{

    protected Context mContext;
    private int mLayoutRes;
    private int[] mChildViewIds;
    private LayoutInflater mInflater;

    public AutoReuseViewAdapter(Context context,int layoutRes,int ... childViewIds){
        mContext = context;
        mLayoutRes = layoutRes;
        mChildViewIds = childViewIds;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        View view = null;
        ViewHolder holder = null;

        if (convertView != null){
            view = convertView;
            holder = (ViewHolder)convertView.getTag();

        }else{

            view = mInflater.inflate(mLayoutRes,null);
            holder = new ViewHolder();

            for(int childId : mChildViewIds){
                holder.cacheView(view.findViewById(childId));
            }
            view.setTag(holder);

        }

        onBoundDataAndEventToViews(position,view,holder.getCachedViews());
        return view;
    }

    public abstract void onBoundDataAndEventToViews(int position,View itemView,ArrayList<View> childViews);

    private static class ViewHolder{
        private ArrayList<View> cachedViews = new ArrayList<View>();

        public void cacheView(View v){
            cachedViews.add(v);
        }

        public ArrayList<View> getCachedViews(){
            return cachedViews;
        }


    }

}
