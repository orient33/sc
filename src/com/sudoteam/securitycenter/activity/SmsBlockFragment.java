package com.sudoteam.securitycenter.activity;

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
import com.sudoteam.securitycenter.adapter.TrashSmsAdapter;
import com.sudoteam.securitycenter.views.BlurDialog;
import com.sudoteam.securitycenter.entity.*;
import com.sudoteam.securitycenter.utils.ViewUtils;

import java.lang.Override;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.db.sqlite.Selector;

public class SmsBlockFragment extends Fragment implements View.OnClickListener{

    private ListView smsList;

    private TrashSmsAdapter adapter;

    private List<TrashSms> smsDatas = new ArrayList<TrashSms>();

    private ListView smsTextList;
    private ImageView image;
    private Handler handler ;

    private Button smsBlockClear;

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

        smsBlockClear = (Button)view.findViewById(R.id.sms_block_clear);
        smsBlockClear.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.sms_block_clear){

            BlurDialog dialog = new BlurDialog(getActivity()) {
                @Override
                protected View onDataBind(View layoutView) {

                    View view = ViewUtils.getView(getActivity(),R.layout.test_view);
                    Button b = (Button) view.findViewById(R.id.testbutton);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getActivity(),"buttonclicked",Toast.LENGTH_SHORT).show();
                        }
                    });


                    return view;
                }
            };

            dialog.createDialog();
        }
    }
}

