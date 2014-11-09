
package com.sudoteam.securitycenter.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.sudoteam.securitycenter.Adapter.*;
import com.sudoteam.securitycenter.Entity.BlockNumbers;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.sudoteam.securitycenter.Entity.*;
import com.lidroid.xutils.db.sqlite.Selector;
import com.sudoteam.securitycenter.R;

import com.sudoteam.securitycenter.Adapter.*;
import com.sudoteam.securitycenter.Entity.ScanLog;
import com.sudoteam.securitycenter.Entity.ScanLogResult;
import com.sudoteam.securitycenter.Util;
import com.sudoteam.securitycenter.Views.*;
import com.sudoteam.securitycenter.Activity.*;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.ViewConfiguration;
import android.view.Window;

import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

public class AnnoyInterceptActivity extends FragmentActivity implements View.OnClickListener{

    private ListView smsList;

    private TrashSmsAdapter adapter;

    private List<TrashSms> smsDatas = new ArrayList<TrashSms>();

    private SmsBlockFragment chatFragment;

    private TeleBlockFragment contactsFragment;

    /**
     * PagerSlidingTabStrip的实例
     */
    private PagerSlidingTabStrip tabs;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annoy_intercept);

        Util.setActionBar(this, true, "骚扰拦截",this);

        dm = getResources().getDisplayMetrics();

        ViewPager pager = (ViewPager) findViewById(R.id.pagers);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        List<String> names = new ArrayList<String>();
        names.add("短信拦截");
        names.add("电话拦截");

        pager.setAdapter(new BlockPagerAdapter(getSupportFragmentManager(),names));
        tabs.setViewPager(pager);
        setTabsValue();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.actionbar_setting:

                startActivity(new Intent(this,AnnoyInterruptSettingsActivity.class));
                break;
        }
    }

    /**
     * 对PagerSlidingTabStrip的属性赋值。
     */
    private void setTabsValue() {

        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#8e2921"));
        // 设置选中Tab文字的颜色
        tabs.setSelectedTextColor(Color.parseColor("#8e2921"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);

    }

    public class BlockPagerAdapter extends SlidePagerAdapter {

        public BlockPagerAdapter(FragmentManager fm, List<String> titles) {
            super(fm, titles);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    if (chatFragment == null) {
                        chatFragment = new SmsBlockFragment();
                    }
                    return chatFragment;

                case 1:
                    if (contactsFragment == null) {
                        contactsFragment = new TeleBlockFragment();
                    }
                    return contactsFragment;

                default:
                    return null;
            }

        }
    }




//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_annoy_intercept);
//
////        smsList = (ListView)findViewById(R.id.trash_sms_list);
////
////
////        loadTrashSms();
//    }
    

    
}