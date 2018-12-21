package com.tapc.update.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.tapc.update.broadcast.MediaMountedReceiver;
import com.tapc.update.service.binder.LocalBinder;


/**
 * Created by Administrator on 2017/3/17.
 */

public class BootService extends Service {
    private LocalBinder mBinder;
    private MediaMountedReceiver mMediaMountedReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BootService", "start");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mBinder = new LocalBinder(this);
        initView();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @SuppressLint("InlinedApi")
    private void initView() {
    }
}
