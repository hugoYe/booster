package com.cooeeui.nanobooster.test;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cooeeui.nanobooster.R;
import com.cooeeui.nanobooster.common.util.MemoryUtil;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends Activity {

    public static boolean needDeepClean;


    private ActivityManager mActivityManager;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private ImageView mFloatLayout;

    private TextView mTotalMemory;
    private TextView mAviliableMemory;
    private TextView mUsedMemory;
    private TextView mUsedPercentMemory;

    private TextView mAccessibilityLabel;
    private ListView mListView;
    private ArrayList<String> mPkgNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        initViews();
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mFloatLayout = new ImageView(this);
        mFloatLayout.setBackgroundColor(Color.BLACK);
        mFloatLayout.setAlpha(0.5f);
    }


    @Override
    protected void onResume() {
        super.onResume();

        changeLabelStatus();
        needDeepClean = false;
    }


    private void initViews() {
        mTotalMemory = (TextView) findViewById(R.id.tv_total_memory);
        mAviliableMemory = (TextView) findViewById(R.id.tv_aviliable_memory);
        mUsedMemory = (TextView) findViewById(R.id.tv_used_memory);
        mUsedPercentMemory = (TextView) findViewById(R.id.tv_used_percent_memory);

        mAccessibilityLabel = (TextView) findViewById(R.id.tv_accessibility);
        mListView = (ListView) findViewById(R.id.lv_running_app);
    }

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start_calc:
                float totalMemory = MemoryUtil.getTotalMemory(this) / 1024f / 1024f;
                totalMemory = (float) (Math.round(totalMemory * 100)) / 100;
                mTotalMemory.setText(String.valueOf(totalMemory) + " GB");

                float available = MemoryUtil.getAvailableMemory(this) / 1024f;
                available = (float) (Math.round(available * 100)) / 100;
                mAviliableMemory.setText(String.valueOf(available) + " MB");

                float used = MemoryUtil.getUsedMemory(this) / 1024f / 1024f;
                used = (float) (Math.round(used * 100)) / 100;
                mUsedMemory.setText(String.valueOf(used) + " GB");

                mUsedPercentMemory.setText(MemoryUtil.getUsedPercentValue(this));
                break;

            case R.id.bt_open_accessibility:
                openAccessibilitySetting();
                break;

            case R.id.bt_clear_memory:
                MemoryUtil.cleanMemory(this);
                break;

            case R.id.bt_deep_clear_memory:
                deepClearMemory();
                break;

            case R.id.bt_get_running_app:
                getRunningApp();
                break;
        }

    }

    private void getRunningApp() {
        if (Build.VERSION.SDK_INT >= 21) {
            List<AndroidAppProcess> list = ProcessManager.getRunningAppProcesses();
            for (AndroidAppProcess process : list) {
                if (!process.foreground) {
                    mPkgNameList.add(process.getPackageName());
                }
            }
            RunningAppAdapter appAdapter = new RunningAppAdapter(mPkgNameList);
            mListView.setAdapter(appAdapter);
        } else {
            List<ActivityManager.RunningAppProcessInfo> list =
                mActivityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info : list) {
                if (info.importance
                    >= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    mPkgNameList.add(info.processName);
                }
            }
            RunningAppAdapter appAdapter = new RunningAppAdapter(mPkgNameList);
            mListView.setAdapter(appAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mWindowManager != null && mFloatLayout != null) {
            mWindowManager.removeViewImmediate(mFloatLayout);
        }
    }

    private void changeLabelStatus() {
        mAccessibilityLabel.setText(isAccessibleEnabled() ? "辅助功能已启用" : "辅助功能未启用");
    }

    public void openAccessibilitySetting() {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }

    public void deepClearMemory() {

        mWindowManager.addView(mFloatLayout, mWindowParams);

        for (String pkgName : mPkgNameList) {
            Intent killIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri packageURI = Uri.parse("package:" + pkgName);
            killIntent.setData(packageURI);
            startActivity(killIntent);
        }

        needDeepClean = true;
    }

    private boolean isAccessibleEnabled() {
        AccessibilityManager manager =
            (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = manager.getEnabledAccessibilityServiceList(
            AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo info : runningServices) {
            if (info.getId().equals(getPackageName()
                                    + "/.services.BoosterAccessibilityService")) {
                return true;
            }
        }
        return false;
    }

    class RunningAppAdapter extends BaseAdapter {

        private ArrayList<String> mPackageList;

        public RunningAppAdapter(ArrayList<String> packageList) {
            mPackageList = packageList;
        }

        @Override
        public int getCount() {
            return mPackageList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(TestActivity.this);
            }
            ((TextView) convertView).setText(mPackageList.get(position));
            return convertView;
        }
    }

}
