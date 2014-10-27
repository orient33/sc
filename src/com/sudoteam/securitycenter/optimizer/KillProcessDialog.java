package com.sudoteam.securitycenter.optimizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.optimizer.KillProcessAdapter.KillItem;

public class KillProcessDialog extends DialogFragment {
	Activity mActivity;
	final KillItem mItem;
	KillProcessDialog(KillItem ki){
		mItem = ki;
	}
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    	mActivity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater in = mActivity.getLayoutInflater();
        View v = in.inflate(R.layout.kill_process_dialog, null);
        builder.setView(v)
               .setPositiveButton(R.string.kill_app_ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       Toast.makeText(mActivity, "kill", Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton(R.string.kill_app_no, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       Toast.makeText(mActivity, "add to ..", Toast.LENGTH_SHORT).show();
                   }
               });
        TextView title = (TextView)v.findViewById(R.id.kill_dialog_title);
        title.setText(mItem.title);
        title.setCompoundDrawables(mItem.icon, null, null, null);
        TextView size = (TextView)v.findViewById(R.id.kill_dialog_size);
        String ss = mActivity.getString(R.string.kill_dialog_summary, mItem.memUse);
        size.setText(ss);
        return builder.create();
    }
}
