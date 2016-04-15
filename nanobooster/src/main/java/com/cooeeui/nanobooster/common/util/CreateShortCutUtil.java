package com.cooeeui.nanobooster.common.util;


import android.content.Context;
import android.content.Intent;
import com.cooeeui.nanobooster.R;

/**
 * Created by Administrator on 2016/4/14.
 */
public class CreateShortCutUtil {

    public static void createShortCut(Context context) {
        Intent shortcutintent = new Intent(
            "com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutintent.putExtra("duplicate", false);
        //设置名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,context.getString(R.string.app_name));

        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                          Intent.ShortcutIconResource.fromContext(context,
                                                                  R.mipmap.ic_launcher));
        // 点击快捷图片，运行的程序主入口
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
                                new Intent(context.getApplicationContext(), context.getClass()));
        // 发送广播
        context.sendBroadcast(shortcutintent);
    }

}
