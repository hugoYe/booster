package com.cooeeui.nanobooster.common.util;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

/**
 * Created by hugo.Ye on 2016/4/13. 辅助功能开关工具类
 */
final public class AccessibilityServiceUtil {

    /**
     * 判断辅助功能开关是否打开
     *
     * @param context 上下文环境
     */
    public static boolean isAccessibleEnabled(Context context) {
        AccessibilityManager manager =
            (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = manager.getEnabledAccessibilityServiceList(
            AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo info : runningServices) {
            if (info.getId().equals(context.getPackageName()
                                    + "/.services.BoosterAccessibilityService")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 进入系统辅助功能设置界面
     *
     * @param context 上下文环境
     */
    public static void openAccessibilitySetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
