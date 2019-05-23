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

    @Override
    public void onReceive(final Context context, Intent argIntent) {

        try {
            _Utils.openAppFromOuter(context,"com.alibaba.android.rimet");
//            _Utils.openAppFromInner(context,"com.alibaba.android.rimet");
//            _Utils.openRimetFromOuter(context);
            mDataContext = new DataContext(context);
            mDataContext.addRunLog("唤醒手机",new DateTime().toLongDateTimeString());
            OprateFragment.setAlarmRimet(context);
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
}
