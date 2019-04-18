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
import android.os.SystemClock;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static android.content.Context.KEYGUARD_SERVICE;

public class _Utils {

    public static int rimetClockOnHour =0;
    public static int rimetIKnowHour=0;

    private static TextToSpeech textToSpeech=null;//创建自带语音对象
    public static void speaker(final Context context, final String msg){
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setPitch(1.0f);//方法用来控制音调
                    textToSpeech.setSpeechRate(0.7f);//用来控制语速

                    //判断是否支持下面两种语言
//                    int result1 = textToSpeech.setLanguage(Locale.US);
                    int result = textToSpeech.setLanguage(Locale.CHINA);
//                    if(result1 == TextToSpeech.LANG_MISSING_DATA || result1 == TextToSpeech.LANG_NOT_SUPPORTED){
//                        Toast.makeText(context, "US数据丢失或不支持", Toast.LENGTH_SHORT).show();
//                    }
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(context, "SIMPLIFIED_CHINESE数据丢失或不支持", Toast.LENGTH_SHORT).show();
                    }else{
                        textToSpeech.speak(msg,TextToSpeech.QUEUE_FLUSH, null);//输入中文，若不支持的设备则不会读出来
                    }
                }
            }
        });
    }
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
        mWakeLock.acquire(120000);
    }

    public static void closeScreen(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
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
    private static PowerManager.WakeLock wakeLock = null;

    /**
     * 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
     * <p>
     * PARTIAL_WAKE_LOCK :保持CPU 运转，屏幕和键盘灯是关闭的。
     * SCREEN_DIM_WAKE_LOCK ：保持CPU 运转，允许保持屏幕显示但有可能是灰的，关闭键盘灯
     * SCREEN_BRIGHT_WAKE_LOCK ：保持CPU 运转，保持屏幕高亮显示，关闭键盘灯
     * FULL_WAKE_LOCK ：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
     *
     * @param context
     */
    public static void acquireWakeLock(Context context) {
        try {
            if (null == wakeLock) {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getClass().getCanonicalName());
                if (null != wakeLock) {
                    wakeLock.acquire();
                    addRunLog(context, "", "锁定唤醒锁。");
                }
            }
        } catch (Exception e) {
            printException(context, e);
        }
    }
    /**
     * 释放设备电源锁
     */
    public static void releaseWakeLock(Context context) {
        try {
            if (null != wakeLock && wakeLock.isHeld()) {
                wakeLock.release();
                wakeLock = null;
                addRunLog(context, "", "解除唤醒锁。");
            }
        } catch (Exception e) {
            printException(context, e);
        }
    }
}
