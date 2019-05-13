package com.wangsc.lovehome.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wangsc.lovehome.DataContext;
import com.wangsc.lovehome._Utils;
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
            mDataContext = new DataContext(context);
            mDataContext.addRunLog("考勤打卡",new DateTime().toLongDateTimeString());
            OprateFragment.setAlarmRimet(context);
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
}
