package com.sudoteam.securitycenter.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Activity.ScanVirusActivity_v2;

public class TeleBlockFragment extends Fragment {



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.telephony_block_list,null);


		return view;
	}
}
