package com.wangsc.lovehome.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wangsc.lovehome.model.DataContext;
import com.wangsc.lovehome.helper._Utils;
import com.wangsc.lovehome.fragment.OprateFragment;
import com.wangsc.lovehome.model.DateTime;

/**
 * Created by 阿弥陀佛 on 2016/10/23.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private DataContext mDataContext;

    // reviver里面用来确定唤醒屏幕的是闹钟，否则不会将唤醒记录到数据库。
    public static boolean isAlarmWakeup=false;

    @Override
    public void onReceive(final Context context, Intent argIntent) {

        try {
            isAlarmWakeup=true;
            _Utils.openAppFromOuter(context,"com.alibaba.android.rimet");
//            _Utils.openAppFromInner(context,"com.alibaba.android.rimet");
//            _Utils.openRimetFromOuter(context);
            mDataContext = new DataContext(context);
            OprateFragment.setAlarmRimet(context);
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
}
