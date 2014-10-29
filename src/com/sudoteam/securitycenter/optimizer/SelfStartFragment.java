package com.sudoteam.securitycenter.optimizer;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;
import com.sudoteam.securitycenter.optimizer.SelfStartAdapter.AppSelfStart;

import java.util.List;

public class SelfStartFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<AppSelfStart>>, AdapterView.OnItemClickListener {

    Activity mActivity;
    SelfStartAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = View.inflate(mActivity, R.layout.self_start_fragment, null);
        ListView lv = (ListView) v.findViewById(R.id.self_start_list);
        mAdapter = new SelfStartAdapter(mActivity);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);
        getLoaderManager().initLoader(11, null, this);
        return v;
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // for  LoaderManager.LoaderCallbacks
    @Override
    public Loader<List<AppSelfStart>> onCreateLoader(int id, Bundle args) {
        return new SelfStartAdapter.SelfStartLoader(mActivity);
    }

    @Override
    public void onLoadFinished(Loader<List<AppSelfStart>> loader,
                               List<AppSelfStart> data) {
        mAdapter.setData(data);
        if (data.size() == 0)
            Toast.makeText(mActivity, "load completed! but no app self start.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(Loader<List<AppSelfStart>> loader) {
        mAdapter.setData(null);
    }

    // for  android.widget.AdapterView.OnItemClickListener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        String packageName = mAdapter.getItem(position).pkgName;
        Util.toAppDetail(mActivity, packageName);
    }


}
