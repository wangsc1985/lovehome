package com.wangsc.lovehome.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wangsc.lovehome.model.DataContext;
import com.wangsc.lovehome.helper._Utils;
import com.wangsc.lovehome.fragment.OprateFragment;
import com.wangsc.lovehome.model.DateTime;
import com.wangsc.lovehome.model.Setting;

/**
 * Created by 阿弥陀佛 on 2016/10/23.
 */
public class ActionReceiver extends BroadcastReceiver {

    public static final String ACTION_ALARM = "com.wangsc.lovehome.ALARM";
    // reviver里面用来确定唤醒屏幕的是闹钟，否则不会将唤醒记录到数据库。
    public static boolean isAlarmWakeup = false;

    @Override
    public void onReceive(final Context context, Intent intent) {

        try {
            DataContext dataContext = new DataContext(context);

            switch (intent.getAction()) {
                case Intent.ACTION_BOOT_COMPLETED:
                    /**
                     * 开机完成
                     */
                    if (dataContext.getSetting(Setting.KEYS.is_rimet_clock_running, false).getBoolean()) {
                        dataContext.addRunLog("开机启动闹钟",new DateTime().toLongDateTimeString());
                        OprateFragment.setAlarmRimet(context);
                    }
                    break;
                case Intent.ACTION_USER_PRESENT:
                    /**
                     * 解锁
                     */
                    if(isAlarmWakeup) {
                        Log.e("wangsc","唤醒手机："+ new DateTime().toLongDateTimeString());
                        dataContext.addRunLog("唤醒手机", new DateTime().toLongDateTimeString());
                        isAlarmWakeup=false;
                    }
                    break;
                case ACTION_ALARM:
                    isAlarmWakeup = true;
                    _Utils.openAppFromOuter(context, "com.alibaba.android.rimet");
                    OprateFragment.setAlarmRimet(context);
                    break;
            }

        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
}
