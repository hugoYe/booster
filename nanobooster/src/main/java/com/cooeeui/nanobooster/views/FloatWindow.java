package com.cooeeui.nanobooster.views;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by hugo.Ye on 2016/4/15. 单实例对象实现
 */
final public class FloatWindow {

    private static final String TAG = FloatWindow.class.getSimpleName();

    private static FloatWindow sInstance;

    public static boolean deepCleaning;

    private Context mContext;
    private ActivityManager mActivityManager;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;

    private ImageView mFloatLayout;

    private FloatWindow(Context context) {
        mContext = context;
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    public static FloatWindow getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new FloatWindow(context);
        }

        return sInstance;
    }

    public void showDeepCleanWindow() {
        deepCleaning = true;

        mFloatLayout = new ImageView(mContext);
        mFloatLayout.setBackgroundColor(Color.BLACK);
        mFloatLayout.setAlpha(0.5f);

        mWindowManager.addView(mFloatLayout, mWindowParams);
    }

    public void removeDeepCleanWindow() {
        deepCleaning = false;

        if (mWindowManager != null && mFloatLayout != null) {
            mWindowManager.removeViewImmediate(mFloatLayout);
            mFloatLayout = null;
        }
    }

}
