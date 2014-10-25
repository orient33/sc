package com.sudoteam.securitycenter.netstat;

import static android.net.NetworkPolicyManager.POLICY_REJECT_METERED_BACKGROUND;
import static android.net.NetworkTemplate.buildTemplateMobileAll;
import static android.net.NetworkTemplate.buildTemplateWifiWildcard;
import static android.net.TrafficStats.UID_REMOVED;
import static android.net.TrafficStats.UID_TETHERING;
import static com.android.internal.util.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.INetworkStatsService;
import android.net.INetworkStatsSession;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkStats;
import android.net.NetworkStatsHistory;
import android.net.NetworkTemplate;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.INetworkManagementService;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.collect.Lists;
import com.sudoteam.securitycenter.R;
import com.sudoteam.securitycenter.Util;

public class DataUsageList extends Fragment {
	private static final String TAG = "[DataUsageList]";
	
    private static final String TAG_APP_DETAILS = "appDetails";
    private static final String TEST_SUBSCRIBER_PROP = "test.subscriberid";
    
//    private static final String TAB_3G = "3g";
//    private static final String TAB_4G = "4g";
    private static final int TAB_WIFI = 0;//"wifi";
    private static final int TAB_MOBILE = 1;//"mobile";
    private static final int CYCLE_MONTH = 0, CYCLE_DAY = 1;
    private static final int LOADER_SUMMARY = 3;
    
	private INetworkManagementService mNetworkService;
	private INetworkStatsService mStatsService;
	private NetworkPolicyManager mPolicyManager;

	private INetworkStatsSession mStatsSession;

	private ListView mListView;
	private DataUsageAdapter mAdapter;

	private UidDetailProvider mUidDetailProvider;
    private NetworkTemplate mTemplate;
//    private ChartData mChartData;
    private int mCurrentTab = TAB_WIFI;
    private int mCurrentCycle = CYCLE_MONTH;

	public DataUsageList() {
	}

	private AdapterView.OnItemSelectedListener l = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
			int iid = parent.getId(); 
			if (iid == R.id.data_cycle) 
				mCurrentCycle = position;
			else if (iid == R.id.data_type)
				mCurrentTab = position;
			updateBody();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context context = getActivity();

		mNetworkService = INetworkManagementService.Stub
				.asInterface(ServiceManager
						.getService(Context.NETWORKMANAGEMENT_SERVICE));
		mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager
				.getService(Context.NETWORK_STATS_SERVICE));
		mPolicyManager = NetworkPolicyManager.from(context);

		try {
			if (!mNetworkService.isBandwidthControlEnabled()) {
				Util.w("No bandwidth control; leaving");
				getActivity().finish();
			}
		} catch (RemoteException e) {
			Util.w(TAG + "No bandwidth control; leaving");
			getActivity().finish();
		}

		try {
			mStatsSession = mStatsService.openSession();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		// if (!hasReadyMobileRadio(context)) // :todo
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final Context context = inflater.getContext();
		final View view = inflater.inflate(R.layout.data_usage_summary,
				container, false);
		mUidDetailProvider = new UidDetailProvider(context);
		mListView = (ListView) view.findViewById(android.R.id.list);

		final String cycles[] = context.getResources().getStringArray(R.array.data_usage_cycles);
		final String types[] = context.getResources().getStringArray(R.array.data_type);
		ArrayAdapter<String> cycleAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, cycles);
		ArrayAdapter<String> typeAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, types);
		cycleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		LinearLayout head = (LinearLayout)View.inflate(context, R.layout.data_usage_header, null);
		Spinner cycle = (Spinner)head.findViewById(R.id.data_cycle);
		Spinner type = (Spinner)head.findViewById(R.id.data_type);
		cycle.setAdapter(cycleAdapter);
		type.setAdapter(typeAdapter);
		cycle.setOnItemSelectedListener(l);
		type.setOnItemSelectedListener(l);

		mListView.addHeaderView(head);
		mListView.setItemsCanFocus(true);
		mAdapter = new DataUsageAdapter(mUidDetailProvider, 0);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Context context = view.getContext();
				Toast.makeText(context, "click " + position, Toast.LENGTH_SHORT).show();
			}
		});
		mListView.setAdapter(mAdapter);
		return view;
	}

    @Override
    public void onResume() {
        super.onResume();
        // pick default tab based on incoming intent
//        final Intent intent = getActivity().getIntent();
//        mIntentTab = computeTabFromIntent(intent);
        updateBody();
        // kick off background task to update stats
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // wait a few seconds before kicking off
                    Thread.sleep(3 * DateUtils.SECOND_IN_MILLIS);
                    mStatsService.forceUpdate();
                } catch (InterruptedException e) {
                } catch (RemoteException e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (isAdded()) {
                    updateBody();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDestroy() {

        mUidDetailProvider.clearCache();
        mUidDetailProvider = null;

        TrafficStats.closeQuietly(mStatsSession);

        if (this.isRemoving()) {
            getFragmentManager()
                    .popBackStack(TAG_APP_DETAILS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        super.onDestroy();
    }

    /**
     * Update body content based on current tab. Loads
     * {@link NetworkStatsHistory} and {@link NetworkPolicy} from system, and
     * binds them to visible controls.
     */
    private void updateBody() {
        if (!isAdded()) return;

        mListView.setVisibility(View.VISIBLE);

        if (TAB_WIFI == mCurrentTab) // just mobile type
        	mTemplate = buildTemplateWifiWildcard();
        else// if (TAB_MOBILE.equals(mCurrentTab))
        	mTemplate = buildTemplateMobileAll(getActiveSubscriberId(getActivity()));

        long now = System.currentTimeMillis();
        long start = mCurrentCycle == CYCLE_MONTH ? TimeUtils.getStartForMonth(now) : TimeUtils.getStartForDay(now);
        Util.i("updateBody() start = "+ start +", end = " + now);
        getLoaderManager().restartLoader(LOADER_SUMMARY,
                SummaryForAllUidLoader.buildArgs(mTemplate, start, now), mSummaryCallbacks);
        // detail mode can change visible menus, invalidate
        getActivity().invalidateOptionsMenu();
    }

    private final LoaderCallbacks<NetworkStats> mSummaryCallbacks = new LoaderCallbacks<
            NetworkStats>() {
        @Override
        public Loader<NetworkStats> onCreateLoader(int id, Bundle args) {
            return new SummaryForAllUidLoader(getActivity(), mStatsSession, args);
        }

        @Override
        public void onLoadFinished(Loader<NetworkStats> loader, NetworkStats data) {
            final int[] restrictedUids = mPolicyManager.getUidsWithPolicy(
                    POLICY_REJECT_METERED_BACKGROUND);
            Util.i("SummaryForAllUidLoader-- onLoadFinished()");
            mAdapter.bindStats(data, restrictedUids);
            updateEmptyVisible();
        }

        @Override
        public void onLoaderReset(Loader<NetworkStats> loader) {
            mAdapter.bindStats(null, new int[0]);
            updateEmptyVisible();
        }

        private void updateEmptyVisible() {
//            final boolean isEmpty = mAdapter.isEmpty() ;
//            mEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
    };

    private static String getActiveSubscriberId(Context context) {
        final TelephonyManager tele = TelephonyManager.from(context);
        final String actualSubscriberId = tele.getSubscriberId();
        return SystemProperties.get(TEST_SUBSCRIBER_PROP, actualSubscriberId);
    }

	public static class AppItem implements Comparable<AppItem>, Parcelable {
		public final int key;
		public boolean restricted;
		public SparseBooleanArray uids = new SparseBooleanArray();
		public long total;

		public AppItem(int key) {
			this.key = key;
		}

		public AppItem(Parcel parcel) {
			key = parcel.readInt();
			uids = parcel.readSparseBooleanArray();
			total = parcel.readLong();
		}

		public void addUid(int uid) {
			uids.put(uid, true);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(key);
			dest.writeSparseBooleanArray(uids);
			dest.writeLong(total);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public int compareTo(AppItem another) {
			return Long.compare(another.total, total);
		}

		public static final Creator<AppItem> CREATOR = new Creator<AppItem>() {
			@Override
			public AppItem createFromParcel(Parcel in) {
				return new AppItem(in);
			}

			@Override
			public AppItem[] newArray(int size) {
				return new AppItem[size];
			}
		};
	}

	/**
	 * Adapter of applications, sorted by total usage descending.
	 */
	public static class DataUsageAdapter extends BaseAdapter {
		private final UidDetailProvider mProvider;
		private final int mInsetSide;

		private final ArrayList<AppItem> mItems = Lists.newArrayList();
		private long mLargest;

		public DataUsageAdapter(UidDetailProvider provider, int insetSide) {
			mProvider = checkNotNull(provider);
			mInsetSide = insetSide;
		}

		/**
		 * Bind the given {@link NetworkStats}, or {@code null} to clear list.
		 */
		public void bindStats(NetworkStats stats, int[] restrictedUids) {
			mItems.clear();

			final int currentUserId = ActivityManager.getCurrentUser();
			final SparseArray<AppItem> knownItems = new SparseArray<AppItem>();

			NetworkStats.Entry entry = null;
			final int size = stats != null ? stats.size() : 0;
			for (int i = 0; i < size; i++) {
				entry = stats.getValues(i, entry);

				// Decide how to collapse items together
				final int uid = entry.uid;
				final int collapseKey;
				if (UserHandle.isApp(uid)) {
					if (UserHandle.getUserId(uid) == currentUserId) {
						collapseKey = uid;
					} else {
						collapseKey = UidDetailProvider
								.buildKeyForUser(UserHandle.getUserId(uid));
					}
				} else if (uid == UID_REMOVED || uid == UID_TETHERING) {
					collapseKey = uid;
				} else {
					collapseKey = android.os.Process.SYSTEM_UID;
				}

				AppItem item = knownItems.get(collapseKey);
				if (item == null) {
					item = new AppItem(collapseKey);
					mItems.add(item);
					knownItems.put(item.key, item);
				}
				item.addUid(uid);
				item.total += entry.rxBytes + entry.txBytes;
			}

			for (int uid : restrictedUids) {
				// Only splice in restricted state for current user
				if (UserHandle.getUserId(uid) != currentUserId)
					continue;

				AppItem item = knownItems.get(uid);
				if (item == null) {
					item = new AppItem(uid);
					item.total = -1;
					mItems.add(item);
					knownItems.put(item.key, item);
				}
				item.restricted = true;
			}

			Util.i("DataUsageAdapter- bindStats-size of mItems = "+ mItems.size()+" & uid = "+ size);
			Collections.sort(mItems);
			mLargest = (mItems.size() > 0) ? mItems.get(0).total : 0;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mItems.get(position).key;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.data_usage_item, parent, false);

				if (mInsetSide > 0) {
					convertView.setPaddingRelative(mInsetSide, 0, mInsetSide, 0);
				}
			}

			final Context context = parent.getContext();

			final TextView text1 = (TextView) convertView
					.findViewById(android.R.id.text1);
			final ProgressBar progress = (ProgressBar) convertView
					.findViewById(android.R.id.progress);

			// kick off async load of app details
			final AppItem item = mItems.get(position);
			UidDetailTask.bindView(mProvider, item, convertView);

			if (item.restricted && item.total <= 0) {
				text1.setText(R.string.data_usage_app_restricted);
				progress.setVisibility(View.GONE);
			} else {
				text1.setText(Formatter.formatFileSize(context, item.total));
				progress.setVisibility(View.VISIBLE);
			}

			final int percentTotal = mLargest != 0 ? (int) (item.total * 100 / mLargest)
					: 0;
			progress.setProgress(percentTotal);

			return convertView;
		}
	}

	/**
	 * Background task that loads {@link UidDetail}, binding to
	 * {@link DataUsageAdapter} row item when finished.<br>
	 * 异步加载app的图片，完成后bind给adapter来更新ui
	 */
	private static class UidDetailTask extends AsyncTask<Void, Void, UidDetail> {
		private final UidDetailProvider mProvider;
		private final AppItem mItem;
		private final View mTarget;

		private UidDetailTask(UidDetailProvider provider, AppItem item,
				View target) {
			mProvider = checkNotNull(provider);
			mItem = checkNotNull(item);
			mTarget = checkNotNull(target);
		}

		public static void bindView(UidDetailProvider provider, AppItem item,
				View target) {
			final UidDetailTask existing = (UidDetailTask) target.getTag();
			if (existing != null) {
				existing.cancel(false);
			}

			final UidDetail cachedDetail = provider.getUidDetail(item.key,
					false);
			if (cachedDetail != null) {//有cache的detail时 直接使用detail设置view
				bindView(cachedDetail, target);
			} else {			//无cache的detail时 加载一个async task给view的tag
				target.setTag(new UidDetailTask(provider, item, target)
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
			}
		}

		private static void bindView(UidDetail detail, View target) {
			final ImageView icon = (ImageView) target
					.findViewById(android.R.id.icon);
			final TextView title = (TextView) target
					.findViewById(android.R.id.title);

			if (detail != null) {
				icon.setImageDrawable(detail.icon);
				title.setText(detail.label);
			} else {
				icon.setImageDrawable(null);
				title.setText(null);
			}
		}

		@Override
		protected void onPreExecute() {
			bindView(null, mTarget);
		}

		@Override
		protected UidDetail doInBackground(Void... params) {
			return mProvider.getUidDetail(mItem.key, true);
		}

		@Override
		protected void onPostExecute(UidDetail result) {
			bindView(result, mTarget);
		}
	}

}
