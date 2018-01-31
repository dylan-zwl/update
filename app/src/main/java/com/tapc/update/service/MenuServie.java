package com.tapc.update.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.tapc.update.broadcastreceiver.MediaMountedReceiver;
import com.tapc.update.service.binder.LocalBinder;
import com.tapc.update.ui.entity.MenuInfo;
import com.tapc.update.ui.widget.MenuBar;
import com.tapc.update.ui.widget.UpdateProgress;


/**
 * Created by Administrator on 2017/3/17.
 */

public class MenuServie extends Service {
    private LocalBinder mBinder;
    private static MenuBar mMenuBar;
    private UpdateProgress mUpdateProgress;
    private WindowManager mWindowManager;
    private Handler mHandler = new Handler();
    private MediaMountedReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MenuServie", "start");
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
        removeMenuBar();
        if (mUpdateProgress != null) {
            mWindowManager.removeView(mUpdateProgress);
            mUpdateProgress = null;
        }
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }


    @SuppressLint("InlinedApi")
    private void initView() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
//        filter.addDataScheme("file");
//        mReceiver = new MediaMountedReceiver();
//        registerReceiver(mReceiver, filter);

        mMenuBar = new MenuBar(this);
        mMenuBar.show();

        mUpdateProgress = new UpdateProgress(this);
        mUpdateProgress.addViewToWindow();
    }

    public UpdateProgress getUpdateProgress() {
        return mUpdateProgress;
    }

    public MenuBar getMenuBar() {
        return mMenuBar;
    }

    private void removeMenuBar() {
        if (mMenuBar != null) {
            mWindowManager.removeView(mMenuBar);
            mMenuBar = null;
        }
    }

    public void setMenuBarVisibility(boolean visibility) {
        if (visibility) {
            getMenuBar().setVisibility(View.VISIBLE);
        } else {
            removeMenuBar();
        }
    }

    public void addInfor(MenuInfo.inforType type, String text) {
        if (mMenuBar != null) {
            mMenuBar.addInfor(type, text);
        }
    }
}
