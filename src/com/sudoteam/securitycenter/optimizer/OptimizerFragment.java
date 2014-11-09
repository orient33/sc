package com.sudoteam.securitycenter.optimizer;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sudoteam.securitycenter.MyFragment;
import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

import java.util.ArrayList;
import java.util.List;

public class OptimizerFragment extends MyFragment implements View.OnClickListener {

    public static final int MSG_UPDATE_UI = 10, MSG_UPDATE_PROGRESS = 11;
    public static final String MSG_OVER ="msg-complete";
    private static final int iconIds[] = {R.drawable.opt_garbage, R.drawable.opt_selfon, R.drawable.opt_running_app};
    private static final int names[] = {R.string.app_cache, R.string.opt_self_on, R.string.kill_app};
    private static final int summarys[] = {R.string.app_cache_summary, R.string.selfon_app_summary, R.string.kill_app_summary};
    private final List<OneCheckItem> list = new ArrayList<OneCheckItem>(names.length);
    private Activity mmActivity;
    private TextView mmSizeInfo, mmSizeUnit, mmProgress;
    private Button mmButton;
    private String mScaningString, mScanCompleted;
    private MyAdapter mAdapter;
    private Handler mmHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_UI:
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_UPDATE_PROGRESS:
                    updateProgress(msg.obj.toString());
                    break;
            }
        }
    };

    public OptimizerFragment() {
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        mmActivity = getActivity();
        mScaningString = mmActivity.getString(R.string.scanning);
        mScanCompleted = mmActivity.getString(R.string.scan_complete);
        mAdapter = new MyAdapter();
        mAdapter.bind();
        new AAsyncTask(null, true).execute(mmHandler);
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = li.inflate(R.layout.fragment_optimizer, null);
        mmSizeInfo = (TextView) v.findViewById(R.id.size_info);
        mmSizeUnit = (TextView) v.findViewById(R.id.size_unit);
        mmProgress = (TextView) v.findViewById(R.id.opt_progress);
        mmButton = (Button) v.findViewById(R.id.check_clear);
        ListView mmListView = (ListView) v.findViewById(R.id.opt_list);
        mmButton.setOnClickListener(this);
        mmListView.setAdapter(mAdapter);

        setSizeInfo(mmSizeInfo, mmSizeUnit);
        return v;
    }

    @Override
    public void onResume() {
        refreshCacheInfo();
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.unbind();
        mmHandler.removeMessages(MSG_UPDATE_UI);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_clear:
                boolean scan = !mAdapter.canClear();
                new AAsyncTask(v, scan).execute(mmHandler);
                break;
        }
    }

    private void updateProgress(String s) {
        if (MSG_OVER.equals(s))
            mmProgress.setText(mScanCompleted);
        else
            mmProgress.setText(mScaningString + s);
    }

    private void refreshCacheInfo() {
        for (OneCheckItem is : list) {
            if (is.task == null) continue;
            int rs = is.task.getCurrentCount();
            is.useResult(rs);
        }
    }

    private void setSizeInfo(TextView tv, TextView unit) {
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bs = sf.getBlockSizeLong();
        long t = sf.getBlockCountLong() * bs, f = sf.getFreeBlocksLong() * bs;
//        String total = Formatter.formatFileSize(mmActivity, t);
        String free = Formatter.formatFileSize(mmActivity, f);
        int firstA = 0;
        for (; firstA < free.length(); ++firstA)
            if (Character.isUpperCase(free.charAt(firstA)))
                break;
        unit.setText(free.substring(firstA));
        tv.setText(free.substring(0, firstA));
    }

    class MyAdapter extends BaseAdapter {
        void bind() {
            if (list.size() > 0)
                return;
            String title[] = {getString(R.string.app_cache), getString(R.string.sdcard_cache), getString(R.string.kill_app)};
            Fragment ff[] = {new ClearCacheFragment(), new SelfStartFragment(), new KillProcessFragment()};
            IScan scans[] = {ClearCacheAdapter.get(mmActivity, null), SelfStartAdapter.get(mmActivity), KillProcessAdapter.get(mmActivity, null)};
            for (int ii = 0; ii < names.length; ++ii) {
                OneCheckItem oci = new OneCheckItem(iconIds[ii], names[ii], summarys[ii], ff[ii], scans[ii], title[ii]);
                list.add(oci);
            }
        }

        void unbind() {
            for (OneCheckItem scan : list) {
                if (scan.task != null)
                    scan.task.destoryResult();
            }
            list.clear();
        }

        boolean canClear() {
            for (OneCheckItem item : list)
                if (item.canClear())
                    return true;
            return false;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public OneCheckItem getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View cv, ViewGroup parent) {
            final OneCheckItem oci = getItem(position);
            ViewHold vh;
            if (cv == null) {
                cv = View.inflate(mmActivity, R.layout.clear_fragment_item, null);
                vh = new ViewHold(cv, oci.nameId, oci.iconId);
                cv.setTag(vh);
            } else
                vh = (ViewHold) cv.getTag();

            if (oci.scaned)
                vh.setEnable(true);
            else
                vh.setEnable(false);

            if (!TextUtils.isEmpty(oci.sizeInfo)) {
                vh.summary.setText(cv.getContext().getString(oci.summaryId, oci.sizeInfo));
                vh.go.setVisibility(View.VISIBLE);
                cv.setOnClickListener(oci);
            } else {
                vh.summary.setText("");
                cv.setOnClickListener(null);
                vh.go.setVisibility(View.GONE);
            }
            return cv;
        }

        class ViewHold {
            final TextView name, summary;
            final ImageView icon, go;

            ViewHold(View root, int nameId, int iconId) {
                name = (TextView) root.findViewById(R.id.opt_item_name);
                summary = (TextView) root.findViewById(R.id.opt_item_summary);
                go = (ImageView) root.findViewById(R.id.opt_item_arrow);
                icon = (ImageView) root.findViewById(R.id.opt_item_icon);
                icon.setImageResource(iconId);
                name.setText(nameId);
            }
            void setEnable(boolean enable){
                name.setEnabled(enable);
                summary.setEnabled(enable);
                icon.setEnabled(enable);
                go.setEnabled(enable);
            }
        }
    }

    ;

    /**
     * an scanned item, eg : app cache, app progress,etc.
     */
    class OneCheckItem implements View.OnClickListener {
        /**
         * the fragment will show once click this item.
         */
        final Fragment fragment;
        /**
         * name's id for this item
         */
        final int nameId;
        /**
         * scan result's summary id for this scan.
         */
        final int summaryId;
        /**
         * title fo fragment
         */
        final String title;
        final IScan task;
        final int iconId;
        /**
         * result for scan, eg: size of app cache or count of running app
         */
        String sizeInfo;
        /**
         * */
        boolean scaned;

        OneCheckItem(int iconId, int name, int summary, Fragment f, IScan scan, String t) {
            nameId = name;
            summaryId = summary;
            fragment = f;
            task = scan;
            title = t;
            this.iconId = iconId;
            scaned = false;
        }

        public void useResult(int rs) {
            if (rs <= 0)
                sizeInfo = null;
            else if (rs <= 1024)    //
                sizeInfo = "" + rs;
            else {
                sizeInfo = Formatter.formatFileSize(mmActivity, rs);
            }
        }

        public boolean canClear() {
            return !TextUtils.isEmpty(sizeInfo);
        }

        @Override
        public void onClick(View v) {
            Util.replaceNewFragment(mmActivity, R.id.container, fragment);
        }

    }

    class AAsyncTask extends AsyncTask<Handler, String, Object> {
        final View v;
        final boolean scan; // scan or do-clear

        AAsyncTask(View vv, boolean s) {
            v = vv;
            scan = s;
        }

        @Override
        protected void onPreExecute() {
            if (v != null)
                v.setEnabled(false);
        }

        protected Object doInBackground(Handler... h) {

            for (int ii = 0; ii < mAdapter.getCount(); ++ii) {

                final OneCheckItem oci = mAdapter.getItem(ii);
                if (oci.task == null)
                    continue;
                int rs = 0;
                if (scan) {
                    rs = oci.task.doCheck(h[0], MSG_UPDATE_PROGRESS);
                    oci.scaned = true;
                }else
                    oci.task.optimizeSelect(true);
                Util.i("task " + ii + " result == " + rs);
                oci.useResult(rs);
                h[0].post(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                SystemClock.sleep(1000);
            }
            Util.i("task complete !.");
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (v != null)
                v.setEnabled(true);
        }
    }
}