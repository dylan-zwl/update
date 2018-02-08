/**
 * java[v 1.0.0]
 * classes:com.oxbix.tapc.Config
 * fch Create of at 2015�?2�?3�? 下午5:10:26
 */
package com.tapc.update.application;

import android.os.Environment;
import android.text.TextUtils;

import com.tapc.update.ui.presenter.CopyFilePresenter;

import java.io.File;

public class Config {
    public static String IN_SD_FILE_PATH;
    public static String EX_SD_FILE_PATH;
    public static String UDISK_FILE_PATH;

    public static String MOUNTED_PATH = "/mnt/external_sd/";
    public static String SAVEFILE_TARGET_PATH = "";
    public static String SAVEFILE_ORIGIN__PATH = "";
    public static String INSTALL_APP_PATH = "";
    public static final String SAVEFILE_NAME = "tapc/";

    public static final String UPDATE_APP_NAME = "update_app";
    public static final String UPDATE_OS_NAME = "update.zip";

    public static String APP_PACKGGE = "com.tapc.platform";
    public static final String TEST_APP_PACKGGE = "com.tapc.test";

    public static boolean isCoverInstall = false;
    public static String VA_TARGET_PATH = "";
    public static String VA_ORIGIN_PATH = "";

    public static void initConfig(String mountedPath) {
        if (new File("/dev/ttyS3").exists()) {
            //rk3188
            EX_SD_FILE_PATH = "/mnt/external_sd/";
            UDISK_FILE_PATH = "/mnt/usb_storage/";
        } else {
            //8935
            EX_SD_FILE_PATH = "/storage/sdcard1/";
            UDISK_FILE_PATH = "/storage/usb0/";
        }
        IN_SD_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        if (TextUtils.isEmpty(mountedPath)) {
            MOUNTED_PATH = EX_SD_FILE_PATH;
            //        MOUNTED_PATH = UDISK_FILE_PATH;
        } else {
            MOUNTED_PATH = mountedPath;
        }

        String savefile = SAVEFILE_NAME;
        SAVEFILE_TARGET_PATH = IN_SD_FILE_PATH + savefile;
        SAVEFILE_ORIGIN__PATH = MOUNTED_PATH + savefile;

        if (TextUtils.isEmpty(VA_ORIGIN_PATH)) {
            VA_ORIGIN_PATH = CopyFilePresenter.getVaOriginPath(SAVEFILE_ORIGIN__PATH);
        }
        if (TextUtils.isEmpty(VA_TARGET_PATH)) {
            VA_TARGET_PATH = Config.SAVEFILE_TARGET_PATH + ".va";
        }

        INSTALL_APP_PATH = SAVEFILE_ORIGIN__PATH + "third_app/";
    }
}
