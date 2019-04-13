package com.wangsc.lovehome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wangsc.lovehome.fragment.OprateFragment;

public class BootCompleteReceiver extends BroadcastReceiver
{
    public BootCompleteReceiver()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
            {
//                OprateFragment.setAlarmRimet(context);
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
}