package com.tapc.update.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.tapc.update.service.binder.LocalBinder;
import com.tapc.update.ui.entity.MenuInfo;
import com.tapc.update.ui.widget.MenuBar;
import com.tapc.update.ui.widget.UpdateProgress;


/**
 * Created by Administrator on 2017/3/17.
 */

public class MenuServie extends Service {
    private LocalBinder mBinder;
    private MenuBar mMenuBar;
    private UpdateProgress mUpdateProgress;

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
        mMenuBar.dismiss();
        mUpdateProgress.dismiss();
    }


    @SuppressLint("InlinedApi")
    private void initView() {
        mMenuBar = new MenuBar(this);
        mUpdateProgress = new UpdateProgress(this);
    }

    public UpdateProgress getUpdateProgress() {
        return mUpdateProgress;
    }

    public MenuBar getMenuBar() {
        return mMenuBar;
    }

    public void setMenuBarVisibility(boolean visibility) {
        if (visibility) {
            mMenuBar.setVisibility(View.VISIBLE);
            mMenuBar.show();
            mUpdateProgress.addViewToWindow();
        } else {
            getMenuBar().setVisibility(View.GONE);
        }
    }

    public void addInfor(MenuInfo.inforType type, String text) {
        if (mMenuBar != null) {
            mMenuBar.addInfor(type, text);
        }
    }
}
