package com.sudoteam.securitycenter.netstat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.sudoteam.securitycenter.R;

/**
 * setting for net usage. or set phone number dialog.
 */
public class NetSettingFragment extends DialogFragment {
    private static final String NUMBER_KEY = "phone-number";

    @Override
    public Dialog onCreateDialog(Bundle b) {
        final Activity act = getActivity();

        final EditText et = new EditText(act);
        et.setHint(R.string.input_phone_number);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        String num = getNumber(act), title;
        if (!TextUtils.isEmpty(num)) {
            et.setText(num);
            title = act.getString(R.string.number_change);
        } else {
            title = act.getString(R.string.number_set);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(act)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveNumber(act, et.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setView(et)
                .setTitle(title);
        return builder.create();
    }

    private static String getNumber(Context c) {
        SharedPreferences sp = c.getSharedPreferences(c.getPackageName(), 0);
        return sp.getString(NUMBER_KEY, "");
    }

    private static void saveNumber(Context c, String number) {
        if (number.length() != 11 && !TextUtils.isEmpty(number)) { // check number. Empty to cancel number
            Toast.makeText(c, R.string.number_error, Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = c.getSharedPreferences(c.getPackageName(), 0).edit();
        editor.putString(NUMBER_KEY, number);
        editor.commit();
    }
}
