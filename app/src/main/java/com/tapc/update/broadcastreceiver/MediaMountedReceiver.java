package com.tapc.update.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.tapc.update.application.Config;
import com.tapc.update.ui.activity.MainActivity;
import com.tapc.update.utils.IntentUtil;

import java.io.File;

public class MediaMountedReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == Intent.ACTION_MEDIA_MOUNTED) {
            String path = intent.getData().getPath() + "/";
            if (TextUtils.isEmpty(path) || new File(path).exists() == false) {
                return;
            }
            Config.MOUNTED_PATH = path;
            String manualPath = Config.MOUNTED_PATH + "/" + Config.SAVEFILE_PATH + "manual";
            if (new File(manualPath).exists()) {
                IntentUtil.startActivity(context, MainActivity.class, null, Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                        .FLAG_ACTIVITY_CLEAR_TOP);
            } else {
//                IntentUtil.startActivity(context, AutoUpdateActivity.class, null, Intent.FLAG_ACTIVITY_NEW_TASK | Intent
//                        .FLAG_ACTIVITY_CLEAR_TOP);
            }
        }
    }
}
