package com.wangsc.lovehome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.KEYGUARD_SERVICE;

public class _Utils {
    /**
     * 从app内部启动外部程序
     *
     * @param context
     * @param packageName
     * @throws PackageManager.NameNotFoundException
     */
    public static void openAppFromInner(Context context, String packageName) throws PackageManager.NameNotFoundException {
        PackageInfo pi = context.getPackageManager().getPackageInfo(packageName, 0);

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);

        List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            ComponentName cn = new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name);

            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    /**
     * 从app外部启动外部程序
     *
     * @param context
     * @param packageName
     */
    public static void openAppFromOuter(Context context, String packageName) throws InterruptedException {
        wakeScreen(context);
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(LaunchIntent);
    }

    public static PowerManager.WakeLock mWakeLock;
    @SuppressLint("InvalidWakeLockTag")
    public static void wakeScreen(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "bright");
        mWakeLock.acquire(60000);

        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // 屏幕锁定
        keyguardLock.disableKeyguard(); // 解锁
    }


    private static void unLockScreen(Activity activity) {
        final Window win = activity.getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    }

    public static void printException(Context context, Exception e) {
        if (e.getStackTrace().length == 0)
            return;
        String msg = "";
        for (StackTraceElement ste : e.getStackTrace()) {
            if (ste.getClassName().contains(context.getPackageName())) {
                msg += "类名：\n" + ste.getClassName()
                        + "\n方法名：\n" + ste.getMethodName()
                        + "\n行号：" + ste.getLineNumber()
                        + "\n错误信息：\n" + e.getMessage() + "\n";
            }
        }
        try {
            new AlertDialog.Builder(context).setMessage(msg).setCancelable(false).setPositiveButton("知道了", null).show();
        } catch (Exception e1) {
        }
        addRunLog(context, "运行错误", msg);
        e.printStackTrace();
    }

    public static void addRunLog(Context context, String item, String message) {
        new DataContext(context).addRunLog(new RunLog(item, message));
    }

    /**
     * 检测辅助功能是否开启<br>
     * 方 法 名：isAccessibilitySettingsOn <br>
     * 创 建 人 <br>
     * 创建时间：2016-6-22 下午2:29:24 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     *
     * @param mContext
     * @return boolean
     */
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        // TestService为对应的服务
        final String service = mContext.getPackageName() + "/" + MyListenerService.class.getCanonicalName();
        Log.i(TAG, "service:" + service);
        // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }
}
