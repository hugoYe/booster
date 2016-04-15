package com.cooeeui.nanobooster.services;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.cooeeui.nanobooster.test.TestActivity;
import com.cooeeui.nanobooster.views.FloatWindow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by user on 2016/1/13.
 */
public class BoosterAccessibilityService extends AccessibilityService {

    private final String TAG = BoosterAccessibilityService.class.getSimpleName();

    private HashSet<String> mForceStopTextSet;
    private HashSet<String> mForceStopViewIdSet;
    private HashSet<String> mOkTextSet;
    private HashSet<String> mOkViewIdSet;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("yezhennan", "onCreate");

        initHashSet();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
//        Log.i("yezhennan", "onServiceConnected");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i("yezhennan", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        Log.i("yezhennan", "onConfigurationChanged");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.i("yezhennan", "onDestroy");
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        processKillApplication(event);
    }

    @Override
    public void onInterrupt() {
//        Log.i("yezhennan", "onInterrupt");
    }

    private void initHashSet() {
        mForceStopTextSet = new HashSet<>();
        mForceStopTextSet.add("Force Stop");
        mForceStopTextSet.add("common_force_stop");
        mForceStopTextSet.add("finish_application");
        mForceStopTextSet.add("End");
        mForceStopTextSet.add("强制停止");
        mForceStopTextSet.add("强行停止");
        mForceStopTextSet.add("结束运行");
        mForceStopTextSet.add("強制停止");
        mForceStopTextSet.add("結束操作");

        mForceStopViewIdSet = new HashSet<>();
        mForceStopViewIdSet.add("com.android.settings:id/force_stop_button");
        mForceStopViewIdSet.add("com.android.settings:id/left_button");
        mForceStopViewIdSet.add("miui:id/v5_icon_menu_bar_primary_item");

        mOkTextSet = new HashSet<>();
        mOkTextSet.add("ok");
        mOkTextSet.add("confirm");
        mOkTextSet.add("确定");
        mOkTextSet.add("确认");
        mOkTextSet.add("好");
        mOkTextSet.add("確定");
        mOkTextSet.add("確認");

        mOkViewIdSet = new HashSet<>();
        mOkViewIdSet.add("android:id/button1");
    }

    private void processKillApplication(AccessibilityEvent event) {
        if (!FloatWindow.deepCleaning) {
            return;
        }

//        Log.i(TAG, "processKillApplication： " + event.getPackageName());

        if (event.getSource() != null) {
            if ("com.android.settings".equals(event.getPackageName())) {
                String className = (String) event.getClassName();
                Object titleObject = "";
                if (event.getText().size() > 0) {
                    titleObject = (CharSequence) event.getText().get(0);
                }

                if (("com.android.settings.applications.InstalledAppDetailsTop".equals(className))
                    || ("com.android.settings.SubSettings".equals(className))
                    || "App info".equals((CharSequence) titleObject)
                    || "应用信息".equals((CharSequence) titleObject)
                    || "应用程序信息".equals((CharSequence) titleObject)) {
                    handleForceStopButton(event.getSource());
                } else if (("android.app.AlertDialog".equals(className))
                           || ("com.htc.widget.HtcAlertDialog".equals(className))
                           || ("com.yulong.android.view.dialog.AlertDialog".equals(className))) {
                    handleOkButton(event.getSource());
                }
            }
        }
    }

    private boolean handleForceStopButton(AccessibilityNodeInfo accessibilityNodeInfo) {
        boolean handleRst = false;

//        Log.i("yezhennan", "handleForceStopButton 111");

        ArrayList localList = new ArrayList();
        // 根据按钮的id来寻找
        if (Build.VERSION.SDK_INT >= 18) {
            Iterator stopIterator = mForceStopViewIdSet.iterator();
            while (stopIterator.hasNext()) {
                List nodeList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(
                    (String) stopIterator.next());
                if (nodeList != null && nodeList.size() > 0) {
                    localList.addAll(nodeList);
//                    Log.i("yezhennan", "handleForceStopButton 222");
                    break;
                }
            }
        }
        // 根据按钮的文字标题来寻找
        if (localList.size() <= 0) {
            Iterator stopIterator = mForceStopTextSet.iterator();
            while (stopIterator.hasNext()) {
                List nodeList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(
                    (String) stopIterator.next());
                if (nodeList != null && nodeList.size() > 0) {
                    localList.addAll(nodeList);
//                    Log.i("yezhennan", "handleForceStopButton 333");
                    break;
                }
            }
        }
        if (localList.size() > 0) {
            Iterator iterator = localList.iterator();
            while (iterator.hasNext()) {
                AccessibilityNodeInfo localAccessibilityNodeInfo =
                    (AccessibilityNodeInfo) iterator.next();
                String str = localAccessibilityNodeInfo.getText().toString();
                if (mForceStopTextSet.contains(str) && localAccessibilityNodeInfo
                    .isClickable() && localAccessibilityNodeInfo.isEnabled()) {
                    localAccessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    handleRst = true;
//                    Log.i("yezhennan", "handleForceStopButton 444");
                }
                localAccessibilityNodeInfo.recycle();
            }
        }
        performGlobalAction(GLOBAL_ACTION_BACK);
//        Log.i("yezhennan", "handleForceStopButton GLOBAL_ACTION_BACK");
        return handleRst;
    }

    private boolean handleOkButton(AccessibilityNodeInfo accessibilityNodeInfo) {
        boolean handleRst = false;

//        Log.i("yezhennan", "handleOkButton 111");

        ArrayList localList = new ArrayList();
        // 根据按钮的id来寻找
        if (Build.VERSION.SDK_INT >= 18) {
            Iterator okIterator = mOkViewIdSet.iterator();
            while (okIterator.hasNext()) {
                List nodeList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(
                    (String) okIterator.next());
                if (nodeList != null && nodeList.size() > 0) {
                    localList.addAll(nodeList);
//                    Log.i("yezhennan", "handleOkButton 222");
                    break;
                }
            }
        }
        // 根据按钮的文字标题来寻找
        if (localList.size() <= 0) {
            Iterator okIterator = mOkTextSet.iterator();
            while (okIterator.hasNext()) {
                List list = accessibilityNodeInfo.findAccessibilityNodeInfosByText(
                    (String) okIterator.next());
                if (list != null && list.size() > 0) {
                    localList.addAll(list);
//                    Log.i("yezhennan", "handleOkButton 333");
                    break;
                }
            }
        }

        if (localList.size() > 0) {
            Iterator iterator = localList.iterator();
            while (iterator.hasNext()) {
                AccessibilityNodeInfo localAccessibilityNodeInfo =
                    (AccessibilityNodeInfo) iterator.next();
                String str = localAccessibilityNodeInfo.getText().toString();
                if (mOkTextSet.contains(str) && localAccessibilityNodeInfo.isClickable()
                    && localAccessibilityNodeInfo.isEnabled()) {
                    localAccessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    handleRst = true;
//                    Log.i("yezhennan", "handleOkButton 444");
                }
                localAccessibilityNodeInfo.recycle();
            }
        }
        return handleRst;
    }
}
