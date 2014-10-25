package com.sudoteam.securitycenter;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyFragment extends Fragment {

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		i("onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		i("onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		i("onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle b) {
		super.onActivityCreated(b);
		i("onActivityCreated");
	}

	@Override
	public void onStart() {
		super.onStart();
		i("onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		i("onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		i("onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		i("onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		i("onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		i("onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		i("onDetach");
	}

	private void i(String s) {
//		Util.i(this + "----" + s + "( )");
	}
}
