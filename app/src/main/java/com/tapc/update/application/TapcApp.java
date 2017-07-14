package com.tapc.update.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.jht.tapc.jni.KeyEvent;
import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.update.service.MenuServie;
import com.tapc.update.service.binder.LocalBinder;
import com.tapc.update.utils.IntentUtil;

public class TapcApp extends Application {
    private static TapcApp sTapcApp;
    private KeyEvent mKeyboardEvent;
    private MenuServie mMenuService;

    @Override
    public void onCreate() {
        super.onCreate();
        sTapcApp = this;
        Config.initConfig();
        initMachineCtl();

//        stopAllService(this);
        startAllService(this);
    }

    public static TapcApp getInstance() {
        return sTapcApp;
    }

    public void startAllService(Context context) {
        IntentUtil.bindService(context, MenuServie.class, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopAllService(Context context) {
        IntentUtil.stopService(context, MenuServie.class);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            if (className.getClassName().equals(MenuServie.class.getName())) {
                mMenuService = (MenuServie) binder.getService();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
        }
    };

    private void initMachineCtl() {
//        SystemSettingsHelper.Init(this);
        MachineController controller = MachineController.getInstance();
        controller.setReceiveBroadcast(this);
        controller.initController();
        controller.start();

        mKeyboardEvent = new KeyEvent(null, 200);
        mKeyboardEvent.openUinput();
        mKeyboardEvent.initCom();
        mKeyboardEvent.start();
    }

//    public KeyEvent getKeyboardEvent() {
//        return mKeyboardEvent;
//    }
//
//    public BottomBar getBottomBar() {
//        if (mMenuService != null) {
//            return mMenuService.getBottomBar();
//        }
//        return null;
//    }
}
