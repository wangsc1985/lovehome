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

public class MyRececiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            DataContext dataContext = new DataContext(context);
            switch (intent.getAction()){

                case Intent.ACTION_BOOT_COMPLETED:
                    /**
                     * 重启完成
                     */
                    if (dataContext.getSetting(Setting.KEYS.is_rimet_clock_running, false).getBoolean()) {
                        dataContext.addRunLog("重启后重新启动闹钟",new DateTime().toLongDateTimeString());
                        OprateFragment.setAlarmRimet(context);
                    }
//                Intent i = new Intent(context, MainActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);
                    break;
                case Intent.ACTION_USER_PRESENT:
                    /**
                     * 解锁
                     */
                    Log.e("wangsc","唤醒手机："+new DateTime().toLongDateTimeString());
                    if(AlarmReceiver.isAlarmWakeup) {
                        dataContext.addRunLog("唤醒手机", new DateTime().toLongDateTimeString());
                        AlarmReceiver.isAlarmWakeup=false;
                    }
                    break;
            }
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
}