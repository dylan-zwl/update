package com.tapc.update.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.tapc.update.broadcastreceiver.MediaMountedReceiver;
import com.tapc.update.service.binder.LocalBinder;
import com.tapc.update.ui.entity.MenuInfor;
import com.tapc.update.ui.widget.MenuBar;
import com.tapc.update.ui.widget.UpdateProgress;


/**
 * Created by Administrator on 2017/3/17.
 */

public class MenuServie extends Service {
    private LocalBinder mBinder;
    private MenuBar mMenuBar;
    private UpdateProgress mUpdateProgress;
    private WindowManager mWindowManager;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("###", "start menuc service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mWindowManager = (WindowManager) getSystemService("window");
        mBinder = new LocalBinder(this);
        initView();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeView(mMenuBar);
    }


    @SuppressLint("InlinedApi")
    private void initView() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                547, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.RIGHT | Gravity.CENTER_HORIZONTAL;
        params.x = 0;
        params.y = 0;
        mMenuBar = new MenuBar(this);
        mWindowManager.addView(mMenuBar, params);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addDataScheme("file");
        MediaMountedReceiver mReceiver = new MediaMountedReceiver();
        registerReceiver(mReceiver, filter);
    }

    public UpdateProgress getUpdateProgress() {
        if (mUpdateProgress == null) {
            final WindowManager.LayoutParams progressParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                            | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                    PixelFormat.TRANSPARENT);
            progressParams.gravity = Gravity.TOP | Gravity.CENTER_VERTICAL;
            progressParams.x = 0;
            progressParams.y = 0;
            mUpdateProgress = new UpdateProgress(this);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mWindowManager.addView(mUpdateProgress, progressParams);
                }
            });
        }
        return mUpdateProgress;
    }

    public void addInfor(MenuInfor.inforType type, String text) {
        mMenuBar.addInfor(type, text);
    }
}
