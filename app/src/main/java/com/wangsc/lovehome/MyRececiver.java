package com.wangsc.lovehome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wangsc.lovehome.fragment.OprateFragment;

public class MyRececiver extends BroadcastReceiver {
    public MyRececiver() {
    }

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