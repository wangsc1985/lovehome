package com.wangsc.lovehome.fragment;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wangsc.lovehome.AlarmReceiver;
import com.wangsc.lovehome.DataContext;
import com.wangsc.lovehome.DateTime;
import com.wangsc.lovehome.R;
import com.wangsc.lovehome.Setting;
import com.wangsc.lovehome._Utils;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class OprateFragment extends Fragment {

    private Button btnTrim,btnHelper;
    public static final int ALARM_RIMET = 406;

    private DataContext mDataContext;

    public OprateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataContext = new DataContext(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (_Utils.isAccessibilitySettingsOn(getContext())) {
            btnHelper.setText("（辅助功能）");
        } else {
            btnHelper.setText("辅助功能");
        }
        if (mDataContext.getSetting(Setting.KEYS.listener, false).getBoolean()) {
            btnTrim.setText("停止");
        } else {
            btnTrim.setText("启动");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_oprate, container, false);


        btnHelper=view.findViewById(R.id.btn_helper);
        btnHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        btnTrim = view.findViewById(R.id.btn_trim);
        btnTrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataContext.getSetting(Setting.KEYS.listener, false).getBoolean()==false) {
                    setAlarmRimet(getContext());
                    mDataContext.editSetting(Setting.KEYS.listener,true);
                    btnTrim.setText("停止");
                }else{
                    stopAlarm(getContext());
                    mDataContext.editSetting(Setting.KEYS.listener,false);
                    btnTrim.setText("启动");
                }
            }
        });
        btnTrim.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                startAlarm(getContext(), System.currentTimeMillis()+5000);
                return true;
            }
        });

        return view;
    }

    public static void setAlarmRimet(Context context){
        /**
         * 7-8.30 上班
         * 12-13 下班
         * 13-14 上班
         * 18-19.30 下班
         */
        DateTime calendar = new DateTime();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        int random =  (int)(Math.random()*10);

        if(hour<8){
            calendar.set(Calendar.HOUR_OF_DAY,8);
            calendar.set(Calendar.MINUTE,20+random);
        }else if(hour<12) {
            calendar.set(Calendar.HOUR_OF_DAY,12);
            calendar.set(Calendar.MINUTE,random);
        }else if(hour<13){
            calendar.set(Calendar.HOUR_OF_DAY,13);
            calendar.set(Calendar.MINUTE,random);
        }else if(hour<18){
            calendar.set(Calendar.HOUR_OF_DAY,18);
            calendar.set(Calendar.MINUTE,random);
        }else{
            if(calendar.get(Calendar.DAY_OF_WEEK)-1>=5){
                calendar.add(Calendar.DAY_OF_MONTH,8-calendar.get(Calendar.DAY_OF_WEEK));
            }else {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            calendar.set(Calendar.HOUR_OF_DAY,8);
            calendar.set(Calendar.MINUTE,10+random);
        }
        Log.e("wangsc",calendar.toLongDateTimeString());
        startAlarm(context, calendar.getTimeInMillis());

    }

    public static void startAlarm(Context context, long alarmTimeInMillis) {
        try {

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("tag", ALARM_RIMET);
            // 念佛结束闹钟
            PendingIntent pi = PendingIntent.getBroadcast(context, ALARM_RIMET, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pi);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pi);
            }

            new DataContext(context).addRunLog("下次打卡时间",new DateTime(alarmTimeInMillis).toLongDateTimeString());
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public static void stopAlarm(Context context) {
        try {
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, ALARM_RIMET, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.cancel(pi);
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
}
