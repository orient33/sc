package com.sudoteam.securitycenter.mac;

import java.util.List;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

public class AppOpsDetails extends Fragment {
    static final String TAG = "AppOpsDetails";

    public static final String ARG_PACKAGE_NAME = "package";

    private AppOpsState mState;
    private PackageManager mPm;
    private AppOpsManager mAppOps;
    private PackageInfo mPackageInfo;
    private LayoutInflater mInflater;
    private View mRootView;
    private TextView mAppVersion;
    private LinearLayout mOperationsSection;

    private final int MODE_ALLOWED = 0;
    private final int MODE_IGNORED = 1;
    private final int MODE_ASK     = 2;
    private final int MODE_ERRORED = 3;

    private final String mPkgName;
    public AppOpsDetails(String n){
    	mPkgName = n;
    }
    
    private int modeToPosition(int mode) {
        switch (mode) {
        case AppOpsManager.MODE_ALLOWED:
            return MODE_ALLOWED;
        case AppOpsManager.MODE_IGNORED:
            return MODE_IGNORED;
        case AppOpsManager.MODE_ASK:
            return MODE_ASK;
        case AppOpsManager.MODE_ERRORED:
        	return MODE_ERRORED;
        };

        return MODE_IGNORED;
    }

    private int positionToMode(int position) {
        switch (position) {
        case MODE_ALLOWED:
            return AppOpsManager.MODE_ALLOWED;
        case MODE_IGNORED:
            return AppOpsManager.MODE_IGNORED;
        case MODE_ASK:
            return AppOpsManager.MODE_ASK;
        case MODE_ERRORED:
            return AppOpsManager.MODE_ERRORED;
        };

        return AppOpsManager.MODE_IGNORED;
    }

    // Utility method to set application label and icon.
    private void setAppLabelAndIcon(PackageInfo pkgInfo) {
        final View appSnippet = mRootView.findViewById(R.id.app_snippet);
        appSnippet.setPaddingRelative(0, appSnippet.getPaddingTop(), 0, appSnippet.getPaddingBottom());

        ImageView icon = (ImageView) appSnippet.findViewById(R.id.app_icon);
        icon.setImageDrawable(mPm.getApplicationIcon(pkgInfo.applicationInfo));
        // Set application name.
        TextView label = (TextView) appSnippet.findViewById(R.id.app_name);
        label.setText(mPm.getApplicationLabel(pkgInfo.applicationInfo));
        // Version number of application
        mAppVersion = (TextView) appSnippet.findViewById(R.id.app_size);

        if (pkgInfo.versionName != null) {
            mAppVersion.setVisibility(View.VISIBLE);
            mAppVersion.setText(getActivity().getString(R.string.version_text,
                    String.valueOf(pkgInfo.versionName)));
        } else {
            mAppVersion.setVisibility(View.INVISIBLE);
        }
    }

    /**根据packagename得到 PackageInfo, 保存在成员变量 mPackageInfo, 并返回packagename*/
    private String retrieveAppEntry() {
        final Bundle args = getArguments();
        String packageName = (args != null) ? args.getString(ARG_PACKAGE_NAME) : null;
        if (packageName == null) {
//            Intent intent = (args == null) ?
//                    getActivity().getIntent() : (Intent) args.getParcelable("intent");
//            if (intent != null) {
//                packageName = intent.getData().getSchemeSpecificPart();
//            }
        	packageName = mPkgName;
        }
        try {
            mPackageInfo = mPm.getPackageInfo(packageName,
                    PackageManager.GET_DISABLED_COMPONENTS |
                    PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Exception when retrieving package:" + packageName, e);
            mPackageInfo = null;
        }

        return packageName;
    }

    private boolean refreshUi() {
        if (mPackageInfo == null) {
            return false;
        }

        setAppLabelAndIcon(mPackageInfo);

        Resources res = getActivity().getResources();

        mOperationsSection.removeAllViews();
        String lastPermGroup = "";
        for (AppOpsState.OpsTemplate tpl : AppOpsState.ALL_TEMPLATES) {
            List<AppOpsState.AppOpEntry> entries = mState.buildState(tpl,
                    mPackageInfo.applicationInfo.uid, mPackageInfo.packageName);
            for (final AppOpsState.AppOpEntry entry : entries) {
                final AppOpsManager.OpEntry firstOp = entry.getOpEntry(0);
                final View view = mInflater.inflate(R.layout.app_ops_details_item,
                        mOperationsSection, false);
                mOperationsSection.addView(view);
                String perm = AppOpsManager.opToPermission(firstOp.getOp());
                if (perm != null) {
                    try {
                        PermissionInfo pi = mPm.getPermissionInfo(perm, 0);
                        if (pi.group != null && !lastPermGroup.equals(pi.group)) {
                            lastPermGroup = pi.group;
                            PermissionGroupInfo pgi = mPm.getPermissionGroupInfo(pi.group, 0);
                            if (pgi.icon != 0) {
                                ((ImageView)view.findViewById(R.id.op_icon)).setImageDrawable(
                                        pgi.loadIcon(mPm));
                            }
                        }
                    } catch (NameNotFoundException e) {
                    }
                }
                ((TextView)view.findViewById(R.id.op_name)).setText(
                        entry.getSwitchText(mState));
                ((TextView)view.findViewById(R.id.op_counts)).setText(
                        entry.getCountsText(res));
                ((TextView)view.findViewById(R.id.op_time)).setText(
                        entry.getTimeText(res, true));
                Spinner sw = (Spinner)view.findViewById(R.id.spinnerWidget);
                final int switchOp = AppOpsManager.opToSwitch(firstOp.getOp());
                int mode = mAppOps.checkOpNoThrow(switchOp, entry.getPackageOps().getUid(),
                        entry.getPackageOps().getPackageName());
                sw.setSelection(modeToPosition(mode));
                sw.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    boolean firstMode = true;

                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        if(firstMode) {
                            firstMode = false;
                            return;
                         }
                        mAppOps.setMode(switchOp, entry.getPackageOps().getUid(),
                                entry.getPackageOps().getPackageName(), positionToMode(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Do nothing
                    }
                });
            }
        }

        return true;
    }

    private void setIntentAndFinish(boolean finish, boolean appChanged) {
        Intent intent = new Intent();
//        intent.putExtra(ManageApplications.APP_CHG, appChanged);
        PreferenceActivity pa = (PreferenceActivity)getActivity();
        pa.finishPreferencePanel(this, Activity.RESULT_OK, intent);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mState = new AppOpsState(getActivity());
        mPm = getActivity().getPackageManager();
        mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAppOps = (AppOpsManager)getActivity().getSystemService(Context.APP_OPS_SERVICE);

        retrieveAppEntry();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.app_ops_details, container, false);
        Util.prepareCustomPreferencesList(container, view, view, true);

        String title = getString(R.string.module_mac);
        Util.setActionBar(getActivity(), true, title, null);//:TODO

        mRootView = view;
        mOperationsSection = (LinearLayout)view.findViewById(R.id.operations_section);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!refreshUi()) {
            setIntentAndFinish(true, true);
        }
    }
}
