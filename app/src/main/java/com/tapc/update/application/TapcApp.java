package com.tapc.update.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.tapc.platform.jni.Driver;
import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.platform.model.device.controller.uart.UARTController;
import com.tapc.update.broadcast.MediaMountedReceiver;
import com.tapc.update.service.MenuService;
import com.tapc.update.service.binder.LocalBinder;
import com.tapc.update.ui.entity.MenuInfo;
import com.tapc.update.ui.widget.UpdateProgress;
import com.tapc.update.utils.IntentUtil;

public class TapcApp extends Application {
    private static TapcApp sTapcApp;
    private MenuService mMenuService;
    private MediaMountedReceiver mMediaMountedReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        sTapcApp = this;
        Config.initConfig(null);
        stopAllService(this);
        startAllService(this);
    }

    public static TapcApp getInstance() {
        return sTapcApp;
    }

    /**
     * 开始升级初始化
     */
    public void startUpdate() {
        IntentUtil.sendBroadcast(this, "tapc_start_update", null);
        initMachineCtl();
    }

    /**
     * 停止升级
     */
    public void stopUpdate() {
        MachineController.getInstance().stop();
        IntentUtil.sendBroadcast(this, "tapc_stop_update", null);
        switch (Config.DEVICE_TYPE) {
            case RK3399:
                System.exit(0);
                break;
        }
    }

    private void initMachineCtl() {
        Driver.openUinput(Driver.UINPUT_DEVICE_NAME);
        String deviceName = "";
        switch (Config.DEVICE_TYPE) {
            case RK3188:
                deviceName = "/dev/ttyS3";
                break;
            case RK3399:
                Driver.KEY_EVENT_TYPE = 0;
                deviceName = "/dev/ttyS1";
                break;
            case S700:
                deviceName = "/dev/ttyS0";
                break;
            case TCC8935:
                deviceName = "/dev/ttyTCC3";
                break;
        }
        UARTController.DEVICE_NAME = deviceName;
        Driver.initCom(deviceName, 115200);

        MachineController controller = MachineController.getInstance();
        controller.initController(this);
        controller.start();
    }

    /**
     * sevice服务
     */
    public void startAllService(Context context) {
        IntentUtil.bindService(context, MenuService.class, mConnection, Context.BIND_AUTO_CREATE | Context
                .BIND_ABOVE_CLIENT);
    }

    public void stopAllService(Context context) {
        IntentUtil.stopService(context, MenuService.class);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            if (className.getClassName().equals(MenuService.class.getName())) {
                mMenuService = (MenuService) binder.getService();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
        }
    };

    public MenuService getService() {
        return mMenuService;
    }

    public UpdateProgress getUpdateProgress() {
        if (mMenuService != null) {
            return mMenuService.getUpdateProgress();
        }
        return null;
    }

    public void addInfor(MenuInfo.inforType type, String text) {
        if (mMenuService != null) {
            mMenuService.addInfor(type, text);
        }
    }

    public void unRegisterMediaMountedReceiver() {
        if (mMediaMountedReceiver != null) {
            unregisterReceiver(mMediaMountedReceiver);
            mMediaMountedReceiver = null;
        }
    }

    public void registerMediaMountedReceiver() {
        if (mMediaMountedReceiver == null) {
            mMediaMountedReceiver = new MediaMountedReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MEDIA_EJECT);
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter.addDataScheme("file");
            TapcApp.this.registerReceiver(mMediaMountedReceiver, filter);
        }
    }
}
