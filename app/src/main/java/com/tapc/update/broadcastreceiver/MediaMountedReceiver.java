package com.tapc.update.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tapc.update.application.Config;
import com.tapc.update.ui.activity.AutoUpdateActivity;
import com.tapc.update.utils.IntentUtil;

import java.io.File;

public class MediaMountedReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == Intent.ACTION_MEDIA_MOUNTED) {
            Config.MOUNTED_PATH = intent.getDataString() + "/";
            IntentUtil.startActivity(context, AutoUpdateActivity.class, null, Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                    .FLAG_ACTIVITY_CLEAR_TOP);
//            SystemClock.sleep(1000);
//            Config.initConfig();
//            String udiskPath = Config.UDISK_FILE_PATH + Config.TAPC_FILE_PATH;
//            String sdPath = Config.EX_SD_FILE_PATH + Config.TAPC_FILE_PATH;
//            if (checkUpdateFileExists(udiskPath, Config.UPDATE_APP_FILE_NAME) ||
//                    checkUpdateFileExists(udiskPath, Config.UPDATE_MCU_FILE_NAME)) {
//                Bundle bundle = new Bundle();
//                bundle.putString("media_type", Config.UDISK_FILE_PATH);
////                IntentUtil.startActivity(context, UpdateMainActivity.class, bundle);
//            } else if (checkUpdateFileExists(sdPath, Config.UPDATE_APP_FILE_NAME) ||
//                    checkUpdateFileExists(sdPath, Config.UPDATE_MCU_FILE_NAME)) {
//                Bundle bundle = new Bundle();
//                bundle.putString("media_type", Config.EX_SD_FILE_PATH);
////                IntentUtil.startActivity(context, UpdateMainActivity.class);
//            }
        }
    }

    private boolean checkUpdateFileExists(String path, String apk) {
        File file = new File(path, apk);
        if (file != null && file.exists()) {
            return true;
        }
        return false;
    }
}
