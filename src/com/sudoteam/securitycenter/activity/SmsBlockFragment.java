package com.sudoteam.securitycenter.Activity;

import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.Fragment;
//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Adapter.TrashSmsAdapter;
import com.sudoteam.securitycenter.Views.BlurDialog;
import com.sudoteam.securitycenter.Entity.*;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.db.sqlite.Selector;

public class SmsBlockFragment extends Fragment {

    private ListView smsList;

    private TrashSmsAdapter adapter;

    private List<TrashSms> smsDatas = new ArrayList<TrashSms>();

    private ListView smsTextList;
    private ImageView image;
    private Handler handler ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sms_block_list, null);

        handler = new Handler();

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        smsList = (ListView)view.findViewById(R.id.trash_sms_list);
        loadTrashSms();

    }


    public void loadTrashSms(){

        DbUtils dbUtils = DbUtils.create(getActivity(),"trash_sms.db");

        try {

            smsDatas = dbUtils.findAll(Selector.from(TrashSms.class)
                    .orderBy("id"));

        } catch (DbException e) {
            e.printStackTrace();
        }

        if(smsDatas != null){
            for (TrashSms trashSms2 : smsDatas) {

                Log.i("huayang", "content = " + trashSms2.getContent());
            }
        }else {
            Log.i("huayang", "no result");
        }

        adapter = new TrashSmsAdapter(getActivity(),smsDatas);

        smsList.setAdapter(adapter);
    }


}

