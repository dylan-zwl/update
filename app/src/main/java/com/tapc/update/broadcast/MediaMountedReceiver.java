package com.tapc.update.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.tapc.update.application.Config;
import com.tapc.update.ui.activity.AutoUpdateActivity;
import com.tapc.update.ui.activity.MainActivity;
import com.tapc.update.utils.IntentUtil;
import com.tapc.update.utils.XmlUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class MediaMountedReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == Intent.ACTION_MEDIA_MOUNTED) {
            String mountedPath = intent.getData().getPath() + "/";
            if (TextUtils.isEmpty(mountedPath) || new File(mountedPath).exists() == false) {
                return;
            }

            File updateFile = new File(mountedPath + "/" + Config.SAVEFILE_NAME);
            if (!updateFile.exists() || updateFile.list() == null || updateFile.list().length <= 0) {
                return;
            }

            Config.MOUNTED_PATH = mountedPath;
            Config.initConfig(mountedPath);

            initUpdateConfig(context, updateFile.getAbsolutePath() + "/update_config.xml");
            String manualPath = Config.SAVEFILE_TARGET_PATH + "manual";

            if (!IntentUtil.isApplicationBroughtToBackground(context)) {
                return;
            }
            if (new File(manualPath).exists()) {
                IntentUtil.startActivity(context, MainActivity.class, null, Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                        .FLAG_ACTIVITY_CLEAR_TOP);
            } else {
                IntentUtil.startActivity(context, AutoUpdateActivity.class, null, Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                        .FLAG_ACTIVITY_CLEAR_TOP);
            }
        }
    }

    private void initUpdateConfig(Context context, String path) {
        try {
            if (!new File(path).exists()) {
                return;
            }
            InputStream inputStream = new FileInputStream(path);
            if (inputStream == null) {
                return;
            }
            Map<String, String> configMap = XmlUtils.getXmlMap(inputStream);
            if (configMap != null) {
                Log.d("update_config.xml", configMap.toString());
            }
            String updateAppMode = configMap.get("update_app_mode");
            if (!TextUtils.isEmpty(updateAppMode) && updateAppMode.equals(1)) {
                Config.isCoverInstall = true;
            }

            String vaOrginPath = configMap.get("va_origin_path");
            if (!TextUtils.isEmpty(vaOrginPath)) {
                Config.VA_ORIGIN_PATH = vaOrginPath;
            }
            String vaTargetPath = configMap.get("va_target_path");
            if (!TextUtils.isEmpty(vaTargetPath)) {
                Config.VA_TARGET_PATH = vaTargetPath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
