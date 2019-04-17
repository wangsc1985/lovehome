package com.wangsc.lovehome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.wangsc.lovehome.fragment.OprateFragment;
import com.wangsc.lovehome.fragment.RunlogsFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech = null;//创建自带语音对象
    private BottomNavigationView navigation;

    private ViewPagerAdapter mViewPagerAdapter;
    private List<Fragment> fragmentList;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentList = new ArrayList<>();
        navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
//                        _Utils.speaker(MainActivity.this,"thank you");
//                        _Utils.speaker(MainActivity.this,"老实念佛");
//            textToSpeech.speak("thank you",TextToSpeech.QUEUE_FLUSH, null);
                        mViewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_runLog:
                        ((IfragmentInit) fragmentList.get(1)).init();
                        mViewPager.setCurrentItem(1);
                        return true;
                }
                return false;
            }
        });


        fragmentList.add(new OprateFragment());
        fragmentList.add(new RunlogsFragment());


        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    textToSpeech.setPitch(1.0f);//方法用来控制音调
                    textToSpeech.setSpeechRate(1.0f);//用来控制语速

                    //判断是否支持下面两种语言
                    int result1 = textToSpeech.setLanguage(Locale.US);
                    int result2 = textToSpeech.setLanguage(Locale.SIMPLIFIED_CHINESE);
                    boolean a = (result1 == TextToSpeech.LANG_MISSING_DATA || result1 == TextToSpeech.LANG_NOT_SUPPORTED);
                    boolean b = (result2 == TextToSpeech.LANG_MISSING_DATA || result2 == TextToSpeech.LANG_NOT_SUPPORTED);


                } else {
                    Toast.makeText(getApplicationContext(), "数据丢失或不支持", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.viewPage_content);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addTouchables(null);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((IfragmentInit) fragmentList.get(position)).init();
                navigation.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initTimePrompt() {
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mTimeReceiver, timeFilter);
    }

    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar cal = Calendar.getInstance();
            int min = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            _Utils.speaker(context,min + "分" + second + "秒");
//            textToSpeech.speak(min + "分" + second + "秒",TextToSpeech.QUEUE_FLUSH, null);
        }
    };

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
}
