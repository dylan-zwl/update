package com.tapc.update.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.tapc.platform.jni.Driver;
import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.update.service.MenuService;
import com.tapc.update.service.binder.LocalBinder;
import com.tapc.update.ui.entity.MenuInfo;
import com.tapc.update.ui.widget.UpdateProgress;
import com.tapc.update.utils.IntentUtil;

public class TapcApp extends Application {
    private static TapcApp sTapcApp;
    private MenuService mMenuService;

    @Override
    public void onCreate() {
        super.onCreate();
        sTapcApp = this;
        stopAllService(this);
        startAllService(this);
        Config.initConfig(null);
        initMachineCtl();
    }

    public static TapcApp getInstance() {
        return sTapcApp;
    }

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

    private void initMachineCtl() {
        Driver.openUinput(Driver.UINPUT_DEVICE_NAME);
        Driver.initCom("/dev/ttyS3", 115200);
        Driver.initCom("/dev/ttyS0", 115200);

        MachineController controller = MachineController.getInstance();
        controller.initController(this);
        controller.start();
    }

    public MenuService getService() {
        return mMenuService;
    }

    public void setMenuBarVisibility(boolean visibility) {
        if (mMenuService != null) {
            mMenuService.setMenuBarVisibility(visibility);
        }
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
}
