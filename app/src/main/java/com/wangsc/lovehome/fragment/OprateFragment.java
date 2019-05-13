package com.wangsc.lovehome.fragment;


import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.wangsc.lovehome.recevier.AlarmReceiver;
import com.wangsc.lovehome.DataContext;
import com.wangsc.lovehome.model.DateTime;
import com.wangsc.lovehome.IfragmentInit;
import com.wangsc.lovehome.service.MusicService;
import com.wangsc.lovehome.R;
import com.wangsc.lovehome.model.Setting;
import com.wangsc.lovehome._Utils;

import java.util.Calendar;
import java.util.Random;

import static android.content.Context.ALARM_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class OprateFragment extends Fragment implements IfragmentInit {

    private Button btnTrim, btnAccount;
    private ImageView btnHelper, btnIsWeek;
    public static final int ALARM_RIMET = 406;

    private DataContext mDataContext;
    private Switch aSwitchWeekRimet;
    private ToggleButton toggleButton;

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
//            btnHelper.setImageResource(R.mipmap.helper_open);
            btnHelper.clearAnimation();
        } else {
//            btnHelper.setImageResource(R.mipmap.helper_close);
            shanDong(btnHelper);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_oprate, container, false);


        final Button btnIntro = view.findViewById(R.id.button_intro);
        btnIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.support.v7.app.AlertDialog.Builder(getContext()).setMessage(R.string.introduce).setPositiveButton("确定", null).show();
            }
        });

        btnAccount = view.findViewById(R.id.button_account);
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eidtAccountDialog();
            }
        });

        btnIsWeek = view.findViewById(R.id.btn_isWeek);
        if (mDataContext.getSetting(Setting.KEYS.is_rimet_week, false).getBoolean()) {
            btnIsWeek.setImageResource(R.mipmap.seven);
        } else {
            btnIsWeek.setImageResource(R.mipmap.five);
        }

        btnIsWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isWeek = mDataContext.getSetting(Setting.KEYS.is_rimet_week, false).getBoolean();
                final boolean listener = mDataContext.getSetting(Setting.KEYS.listener, false).getBoolean();
                isWeek = !isWeek;
                final boolean finalIsWeek = isWeek;
                new android.support.v7.app.AlertDialog.Builder(getContext()).setMessage("确认要变更打卡周期？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (finalIsWeek) {
                            btnIsWeek.setImageResource(R.mipmap.seven);
                        } else {
                            btnIsWeek.setImageResource(R.mipmap.five);
                        }
                        if (listener)
                            setAlarmRimet(getContext());
                        mDataContext.editSetting(Setting.KEYS.is_rimet_week, finalIsWeek);

                    }
                }).setNegativeButton("否", null).show();
            }
        });
//
//        toggleButton = view.findViewById(R.id.toggleButton);
//        toggleButton.setChecked(mDataContext.getSetting(Setting.KEYS.is_rimet_week, false).getBoolean());
//        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                mDataContext.editSetting(Setting.KEYS.is_rimet_week, isChecked);
//            }
//        });
//
//        aSwitchWeekRimet = view.findViewById(R.id.switch_week_rimet);
//        aSwitchWeekRimet.setChecked(mDataContext.getSetting(Setting.KEYS.is_rimet_week, false).getBoolean());
//        aSwitchWeekRimet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                mDataContext.editSetting(Setting.KEYS.is_rimet_week, isChecked);
//            }
//        });

        btnHelper = view.findViewById(R.id.btn_helper);
        btnHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        btnTrim = view.findViewById(R.id.btn_trim);
        if (mDataContext.getSetting(Setting.KEYS.listener, false).getBoolean()) {
            btnTrim.setText("正在运行");
            animatorSuofang(btnTrim);
        } else {
            btnTrim.setText("开始");
        }
        btnTrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataContext.getSetting(Setting.KEYS.listener, false).getBoolean() == false) {
                    setAlarmRimet(getContext());
                    btnTrim.setText("正在运行");
                    animatorSuofang(btnTrim);
                } else {
                    stopAlarm(getContext());
                    btnTrim.setText("开始");
                    stopAnimatorSuofang();
                }
            }
        });
//        btnTrim.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                startAlarm(getContext(), System.currentTimeMillis() + 5000);
//                Snackbar.make(btnHelper, "执行成功", Snackbar.LENGTH_SHORT).show();
//                return true;
//            }
//        });

        return view;
    }

    //缩放
    public ObjectAnimator scaleY;

    public void animatorSuofang(View myView) {
        if (scaleY == null)
            scaleY = ObjectAnimator.ofFloat(myView, "scaleY", 1f, 0.9f, 1f);
        scaleY.setRepeatCount(-1);
        scaleY.setDuration(600);
        scaleY.start();
    }

    public void stopAnimatorSuofang() {
        scaleY.setRepeatCount(0);
//        if (scaleY != null)
//            scaleY.cancel();
    }

    public void eidtAccountDialog() {
        View view = View.inflate(getContext(), R.layout.layout, null);
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext()).create();
        dialog.setView(view);
        dialog.setTitle("输入登录密码");

//        final EditText editTextPhone = view.findViewById(R.id.editText_phone);
        final EditText editTextPassword = view.findViewById(R.id.editText_password);
//        editTextPhone.setText(mDataContext.getSetting(Setting.KEYS.phone, "").getString());
        editTextPassword.setText(mDataContext.getSetting(Setting.KEYS.password, "").getString());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                mDataContext.editSetting(Setting.KEYS.phone, editTextPhone.getText().toString());
                mDataContext.editSetting(Setting.KEYS.password, editTextPassword.getText().toString());
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void shanDong(View view) {
        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation1.setDuration(600);
        alphaAnimation1.setRepeatCount(Animation.INFINITE);
        alphaAnimation1.setRepeatMode(Animation.REVERSE);
        view.setAnimation(alphaAnimation1);
        alphaAnimation1.start();
    }

    public static void setAlarmRimet(Context context) {
        try {
            /**
             * 7-8.30 上班
             * 12-13 下班
             * 13-14 上班
             * 18-19.30 下班
             */
            DateTime calendar = new DateTime();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minite = calendar.get(Calendar.MINUTE);

//            int minRandom = (int) (Math.minRandom() * 10);
            Random ran = new Random();
            int minRandom = ran.nextInt(5);
            int secRandom = ran.nextInt(59);



            DataContext dataContext = new DataContext(context);
            int week_day = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            if (dataContext.getSetting(Setting.KEYS.is_rimet_week, false).getBoolean() == false) {
                /**
                 * 如果上班时间 - 5天制。
                 *
                 * 周末直接跳转到周一的早上八点。
                 *
                 * 其他日期
                 * 1、如果是当天最后一个闹钟，跳转到下一天的八点。
                 * 2、其他时间设置为当天的下一个时间点。
                 */
                if (week_day == 6 || week_day == 0) {
                    addDay(dataContext,calendar, week_day);
                    calendar.set(Calendar.HOUR_OF_DAY, 8);
                    calendar.set(Calendar.MINUTE, minRandom);
                    calendar.set(Calendar.SECOND, secRandom);
                } else {
                    if (hour < 8) {
                        calendar.set(Calendar.HOUR_OF_DAY, 8);
                        calendar.set(Calendar.MINUTE, minRandom);
                        calendar.set(Calendar.SECOND, secRandom);
                    } else if (hour < 12) {
                        calendar.set(Calendar.HOUR_OF_DAY, 12);
                        calendar.set(Calendar.MINUTE, 5 + minRandom);
                        calendar.set(Calendar.SECOND, secRandom);
                    } else if (hour < 13) {
                        calendar.set(Calendar.HOUR_OF_DAY, 13);
                        calendar.set(Calendar.MINUTE, 10 + minRandom);
                        calendar.set(Calendar.SECOND, secRandom);
                    } else if (hour == 13 && minite < 10) {
                        calendar.set(Calendar.HOUR_OF_DAY, 13);
                        calendar.set(Calendar.MINUTE, 10 + minRandom);
                        calendar.set(Calendar.SECOND, secRandom);
                    } else if (hour < 18) {
                        calendar.set(Calendar.HOUR_OF_DAY, 18);
                        calendar.set(Calendar.MINUTE, 5 + minRandom);
                        calendar.set(Calendar.SECOND, secRandom);
                    }
                    // TODO: 2019/4/15 测试代码，用完删除。
//                    else if (hour < 22) {
//                        calendar.set(Calendar.HOUR_OF_DAY, 22);
//                        calendar.set(Calendar.MINUTE, minRandom);
//                    calendar.set(Calendar.SECOND, secRandom);
//                    } else if (hour < 23) {
//                        calendar.set(Calendar.HOUR_OF_DAY, 23);
//                        calendar.set(Calendar.MINUTE, minRandom);
//                    calendar.set(Calendar.SECOND, secRandom);
//                    }
                    //

                    else {
                        if(week_day==5){
                            calendar.add(Calendar.DAY_OF_MONTH, 3);
                        }else{
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 8);
                        calendar.set(Calendar.MINUTE, minRandom);
                        calendar.set(Calendar.SECOND, secRandom);
                    }
                }
            } else {
                /**
                 * 如果上班时间 - 7天制
                 *
                 * 1、当天最后一个闹钟，跳转到下一天的八点。
                 * 2、其他时间设置为当天的下一个时间点。
                 */
                if (hour < 8) {
                    calendar.set(Calendar.HOUR_OF_DAY, 8);
                    calendar.set(Calendar.MINUTE, minRandom);
                    calendar.set(Calendar.SECOND, secRandom);
                } else if (hour < 12) {
                    calendar.set(Calendar.HOUR_OF_DAY, 12);
                    calendar.set(Calendar.MINUTE, 5 + minRandom);
                    calendar.set(Calendar.SECOND, secRandom);
                } else if (hour < 13) {
                    calendar.set(Calendar.HOUR_OF_DAY, 13);
                    calendar.set(Calendar.MINUTE, 10 + minRandom);
                    calendar.set(Calendar.SECOND, secRandom);
                } else if (hour == 13 && minite < 10) {
                    calendar.set(Calendar.HOUR_OF_DAY, 13);
                    calendar.set(Calendar.MINUTE, 10 + minRandom);
                    calendar.set(Calendar.SECOND, secRandom);
                } else if (hour < 18) {
                    calendar.set(Calendar.HOUR_OF_DAY, 18);
                    calendar.set(Calendar.MINUTE, 5 + minRandom);
                    calendar.set(Calendar.SECOND, secRandom);
                }
                 //TODO: 2019/4/15 测试代码，用完删除。
//                else if (hour < 22) {
//                    calendar.set(Calendar.HOUR_OF_DAY, 22);
//                    calendar.set(Calendar.MINUTE, minRandom);
//                calendar.set(Calendar.SECOND, secRandom);
//                } else if (hour < 23) {
//                    calendar.set(Calendar.HOUR_OF_DAY, 23);
//                    calendar.set(Calendar.MINUTE, minRandom);
//                calendar.set(Calendar.SECOND, secRandom);
//                }
                //

                else {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    calendar.set(Calendar.HOUR_OF_DAY, 8);
                    calendar.set(Calendar.MINUTE, minRandom);
                    calendar.set(Calendar.SECOND, secRandom);
                }
            }


            /**
             * 一    二   三   四   五   六   日
             * 1     2    3    4    5   6   0
             * 2     3    4    5    6   7   1
             *
             * 周五+3 周六+2 其余+1
             */
            Log.e("wangsc", calendar.toLongDateTimeString());
            startAlarm(context, calendar.getTimeInMillis());
        } catch (
                Exception e) {
            _Utils.printException(context, e);
        }

    }

    private static void addDay(DataContext dataContext, DateTime calendar, int week_day) {
        if (dataContext.getSetting(Setting.KEYS.is_rimet_week, false).getBoolean() == false) {
            switch (week_day) {
                case 5:
                    calendar.add(Calendar.DAY_OF_MONTH, 3);
                    break;
                case 6:
                    calendar.add(Calendar.DAY_OF_MONTH, 2);
                    break;
                default:
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }else{
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    public static void startAlarm(Context context, long alarmTimeInMillis) {
        try {

            context.startService(new Intent(context, MusicService.class));
//            _Utils.acquireWakeLock(context);

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

            DataContext dataContext = new DataContext(context);
            dataContext.addSetting(Setting.KEYS.alarmTimeInMillis,alarmTimeInMillis);
            dataContext.editSetting(Setting.KEYS.listener, true);
            dataContext.addRunLog("下次启动时间", new DateTime(alarmTimeInMillis).toLongDateTimeString());
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public static void stopAlarm(Context context) {
        try {
            context.stopService(new Intent(context, MusicService.class));// 停止服务
//            _Utils.releaseWakeLock(context);
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, ALARM_RIMET, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.cancel(pi);


            DataContext dataContext = new DataContext(context);
            dataContext.deleteSetting(Setting.KEYS.alarmTimeInMillis);
            dataContext.editSetting(Setting.KEYS.listener, false);
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    @Override
    public void init() {

    }
}
