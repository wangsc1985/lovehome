package com.wangsc.lovehome.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.wangsc.lovehome.R;
import com.wangsc.lovehome.helper._Utils;

public class MusicService extends Service {

    private MediaPlayer mPlayer;
    private float volumn;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("wangsc","music service onCreate ...");
        _Utils.acquireWakeLock(getApplicationContext());
        volumn = 0.001f;
        mPlayer = new MediaPlayer();
        try {
            play();
        } catch (Exception e) {
            _Utils.printException(getApplicationContext(), e);
        }
    }

    @Override
    public void onDestroy() {
        _Utils.releaseWakeLock(getApplicationContext());
        Log.e("wangsc","music service onDestroy ...");
        try {
            mPlayer.stop();
            mPlayer.release();
        } catch (IllegalStateException e) {
            _Utils.printException(getApplicationContext(), e);
        }
        super.onDestroy();
    }

    public void play() {
        try {
            Log.e("wangsc","music service play music ...");
//            File dir = new File(Environment.getExternalStorageDirectory(), "0000000");
//            File url = new File(dir, "1.mp3");
            mPlayer.reset();//把各项参数恢复到初始状态
            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.song);
            mPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                    file.getLength());
//            mPlayer.setDataSource(url.getPath());
            mPlayer.prepare();  //进行缓冲
            mPlayer.setLooping(true);
            mPlayer.start();
            mPlayer.setVolume(volumn, volumn);

        } catch (Exception e) {
            _Utils.printException(getApplicationContext(), e);
        }
    }
}
