package com.sudoteam.securitycenter.mac;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * This class holds the per-item data in our Loader.
 */
class AppEntry {

	private final OpsSummary.AppListLoader mLoader;
	private final ApplicationInfo mInfo;
	private final File mApkFile;
	private String mLabel;
	private Drawable mIcon;
	private boolean mMounted;

	public AppEntry(OpsSummary.AppListLoader loader, ApplicationInfo info) {
		mLoader = loader;
		mInfo = info;
		mApkFile = new File(info.sourceDir);
	}

	public ApplicationInfo getApplicationInfo() {
		return mInfo;
	}

	public String getLabel() {
		return mLabel;
	}

	public Drawable getIcon() {
		if (mIcon == null) {
			if (mApkFile.exists()) {
				mIcon = mInfo.loadIcon(mLoader.mPm);
				return mIcon;
			} else {
				mMounted = false;
			}
		} else if (!mMounted) {
			// If the app wasn't mounted but is now mounted, reload
			// its icon.
			if (mApkFile.exists()) {
				mMounted = true;
				mIcon = mInfo.loadIcon(mLoader.mPm);
				return mIcon;
			}
		} else {
			return mIcon;
		}

		return mLoader.getContext().getResources()
				.getDrawable(android.R.drawable.sym_def_app_icon);
	}

	void loadLabel(Context context) {
		if (mLabel == null || !mMounted) {
			if (!mApkFile.exists()) {
				mMounted = false;
				mLabel = mInfo.packageName;
			} else {
				mMounted = true;
				CharSequence label = mInfo.loadLabel(context
						.getPackageManager());
				mLabel = label != null ? label.toString()
						: mInfo.packageName;
			}
		}
	}

	@Override
	public String toString() {
		return mLabel;
	}
}

public class OpsSummary extends ListFragment implements LoaderManager.LoaderCallbacks<List<AppEntry>> {

//    private static PackageManager mPackageManager;
	public OpsSummary(){}
	
	/**
	 * Perform alphabetical comparison of application entry objects.
	 */
	public static final Comparator<AppEntry> ALPHA_COMPARATOR = new Comparator<AppEntry>() {
	    private final Collator sCollator = Collator.getInstance();
	    @Override
	    public int compare(AppEntry object1, AppEntry object2) {
	        return sCollator.compare(object1.getLabel(), object2.getLabel());
	    }
	};
	
	/**
	 * Helper for determining if the configuration has changed in an interesting
	 * way so we need to rebuild the app list.
	 */
	public static class InterestingConfigChanges {
	    final Configuration mLastConfiguration = new Configuration();
	    int mLastDensity;

	    boolean applyNewConfig(Resources res) {
	        int configChanges = mLastConfiguration.updateFrom(res.getConfiguration());
	        boolean densityChanged = mLastDensity != res.getDisplayMetrics().densityDpi;
	        if (densityChanged || (configChanges&(ActivityInfo.CONFIG_LOCALE
	                |ActivityInfo.CONFIG_UI_MODE|ActivityInfo.CONFIG_SCREEN_LAYOUT)) != 0) {
	            mLastDensity = res.getDisplayMetrics().densityDpi;
	            return true;
	        }
	        return false;
	    }
	}

	static InterestingConfigChanges mLastConfig;
	static PackageIntentReceiver mPackageObserver; 
	/**
	 * Helper class to look for interesting changes to the installed apps
	 * so that the loader can be updated.
	 */
	public static class PackageIntentReceiver extends BroadcastReceiver {
	    final AppListLoader mLoader;

	    public PackageIntentReceiver(AppListLoader loader) {
	        mLoader = loader;
	        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
	        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
	        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
	        filter.addDataScheme("package");
	        mLoader.getContext().registerReceiver(this, filter);
	        // Register for events related to sdcard installation.
	        IntentFilter sdFilter = new IntentFilter();
	        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
	        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
	        mLoader.getContext().registerReceiver(this, sdFilter);
	    }

	    @Override public void onReceive(Context context, Intent intent) {
	        // Tell the loader about the change.
	        mLoader.onContentChanged();
	    }
	}

	
	public static class AppListAdapter extends BaseAdapter {
//        private final Resources mResources;
        private final LayoutInflater mInflater;
        List<AppEntry> mAps;
        Context mContext;

        public AppListAdapter(Context context) {
//            mResources = context.getResources();
        	mContext = context;
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<AppEntry> data) {
        	mAps = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mAps != null ? mAps.size() : 0;
        }

        @Override
        public AppEntry getItem(int position) {
            return mAps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Populate new items in the list.
         */
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = mInflater.inflate(R.layout.app_ops_item, parent, false);
            } else {
                view = convertView;
            }
            i("getView() pos = " + position);

            AppEntry item = getItem(position);
            
//            PackageStats ps = ClearUtil.getPackageStats(mContext, item.getApplicationInfo().packageName);
            ((ImageView)view.findViewById(R.id.app_icon)).setImageDrawable(
                    item.getIcon());
            ((TextView)view.findViewById(R.id.app_name)).setText(item.getLabel());
//            ((TextView)view.findViewById(R.id.op_name)).setText("cache:"+ (ps==null?"null":f(mContext,ps.cacheSize)));
//            ((TextView)view.findViewById(R.id.op_time)).setText("code:"+ (ps==null?"null":f(mContext,ps.codeSize)) +
//            		", data:"+(ps==null?"null":f(mContext,ps.dataSize)));

            return view;
        }
    }
	
	static String f(Context context, long size){
		return Formatter.formatFileSize(context, size);
	}
	
	public static class AppListLoader extends AsyncTaskLoader<List<AppEntry>>{
		final PackageManager mPm;
		List<AppEntry> mApps;
		public AppListLoader(Context context) {
			super(context);
			mPm =getContext().getPackageManager();
		}

		@Override
		public List<AppEntry> loadInBackground() {
			i(" [AppListLoader] loadInBackground()");
			   // Retrieve all known applications.
	        List<ApplicationInfo> apps = mPm.getInstalledApplications(
	        		1);
	        if (apps == null) {
	            apps = new ArrayList<ApplicationInfo>();
	        }

	        final Context context = getContext();

	        // Create corresponding array of entries and load their labels.
	        List<AppEntry> entries = new ArrayList<AppEntry>(apps.size());
	        for (ApplicationInfo ai : apps) {
				if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					AppEntry entry = new AppEntry(this, ai);
					entry.loadLabel(context);
					entries.add(entry);
				}
	        }
	        // Sort the list.
	        Collections.sort(entries, ALPHA_COMPARATOR);
	        
	        i("load  app : " + entries.size());
	        // Done!
	        return entries;
		}
		 /**
	     * Called when there is new data to deliver to the client.  The
	     * super class will take care of delivering it; the implementation
	     * here just adds a little more logic.
	     */
	    @Override public void deliverResult(List<AppEntry> apps) {
	        if (isReset()) {
	            // An async query came in while the loader is stopped.  We
	            // don't need the result.
	            if (apps != null) {
	                onReleaseResources(apps);
	            }
	        }
	        List<AppEntry> oldApps = apps;
	        mApps = apps;

	        if (isStarted()) {
	            // If the Loader is currently started, we can immediately
	            // deliver its results.
	            super.deliverResult(apps);
	        }

	        // At this point we can release the resources associated with
	        // 'oldApps' if needed; now that the new result is delivered we
	        // know that it is no longer in use.
	        if (oldApps != null) {
	            onReleaseResources(oldApps);
	        }
	    }

	    /**
	     * Handles a request to start the Loader.
	     */
	    @Override protected void onStartLoading() {
	        if (mApps != null) {
	            // If we currently have a result available, deliver it
	            // immediately.
	            deliverResult(mApps);
	        }

	        // Start watching for changes in the app data.
	        if (mPackageObserver == null) {
	            mPackageObserver = new PackageIntentReceiver(this);
	        }

	        // Has something interesting in the configuration changed since we
	        // last built the app list?
//	        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

	        if (takeContentChanged() || mApps == null/* || configChange*/) {
	            // If the data has changed since the last time it was loaded
	            // or is not currently available, start a load.
	            forceLoad();
	        }
	    }

	    /**
	     * Handles a request to stop the Loader.
	     */
	    @Override protected void onStopLoading() {
	        // Attempt to cancel the current load task if possible.
	        cancelLoad();
	    }

	    /**
	     * Handles a request to cancel a load.
	     */
	    @Override public void onCanceled(List<AppEntry> apps) {
	        super.onCanceled(apps);

	        // At this point we can release the resources associated with 'apps'
	        // if needed.
	        onReleaseResources(apps);
	    }

	    /**
	     * Handles a request to completely reset the Loader.
	     */
	    @Override protected void onReset() {
	        super.onReset();

	        // Ensure the loader is stopped
	        onStopLoading();

	        // At this point we can release the resources associated with 'apps'
	        // if needed.
	        if (mApps != null) {
	            onReleaseResources(mApps);
	            mApps = null;
	        }

	        // Stop monitoring for changes.
	        if (mPackageObserver != null) {
	            getContext().unregisterReceiver(mPackageObserver);
	            mPackageObserver = null;
	        }
	    }

	    /**
	     * Helper function to take care of releasing resources associated
	     * with an actively loaded data set.
	     */
	    protected void onReleaseResources(List<AppEntry> apps) {
	        // For a simple List<> there is nothing to do.  For something
	        // like a Cursor, we would close it here.
	    }
	}
	AppListAdapter mAdapter;
	Activity mActivity;
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
	}
	
	@Override
	public void onActivityCreated(Bundle b){
		super.onActivityCreated(b);
		mActivity = getActivity();
		i("onActivityCreated() fragment ..");
		setEmptyText("No Apps");
		mAdapter = new AppListAdapter(mActivity);
		setListAdapter(mAdapter);
		setListShown(false);
		getLoaderManager().initLoader(0, null, this);
		setHasOptionsMenu(false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
		final AppEntry ae = mAdapter.getItem(pos);
		String pkg = ae.getApplicationInfo().packageName;
		final Activity act = getActivity();
        Intent intent = new Intent(act, DetailActivity.class);
        intent.putExtra(DetailActivity.KEY,pkg);
        act.startActivity(intent);
	}
	
	@Override
	public Loader<List<AppEntry>> onCreateLoader(int id, Bundle args) {
		return new AppListLoader(mActivity);
	}

	@Override
	public void onLoadFinished(Loader<List<AppEntry>> arg0, List<AppEntry> data) {
		i("onLoadFinished()" + data.size());
		// Set the new data in the adapter.
        mAdapter.setData(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
            i("onLoadFinished() setListShown()" );
        } else {
            setListShownNoAnimation(true);
        }
	}

	@Override
	public void onLoaderReset(Loader<List<AppEntry>> arg0) {
		i("onLoaderReset()");
		mAdapter.setData(null);
	}
	
	public static void i(String s){
		Log.i("sudo", "[OpsSummary] "+s);
	}

}
