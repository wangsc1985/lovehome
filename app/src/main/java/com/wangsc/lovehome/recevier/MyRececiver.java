package com.wangsc.lovehome.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wangsc.lovehome.DataContext;
import com.wangsc.lovehome._Utils;
import com.wangsc.lovehome.fragment.OprateFragment;
import com.wangsc.lovehome.model.Setting;

public class MyRececiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            DataContext dataContext = new DataContext(context);
            if (dataContext.getSetting(Setting.KEYS.listener, false).getBoolean()) {
                OprateFragment.setAlarmRimet(context);
            }
//                Intent i = new Intent(context, MainActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
}