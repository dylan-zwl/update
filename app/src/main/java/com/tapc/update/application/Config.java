/**
 * java[v 1.0.0]
 * classes:com.oxbix.tapc.Config
 * fch Create of at 2015�?2�?3�? 下午5:10:26
 */
package com.tapc.update.application;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class Config {
    public static String IN_SD_FILE_PATH;
    public static String EX_SD_FILE_PATH;
    public static String UDISK_FILE_PATH;

    public static String MOUNTED_PATH = "/mnt/external_sd/";
    public static String TARGET_SAVEFILE_PATH = "";
    public static String ORIGIN_SAVEFILE_PATH = "";
    public static String INSTALL_APP_PATH = "";

    public static final String UPDATE_APP_NAME = "update_app";
    public static final String UPDATE_OS_NAME = "update.zip";

    public static final String APP_PACKGGE = "com.tapc.platform";

    public static void initConfig(String mountedPath) {
        if (new File("/dev/ttyS3").exists()) {
            //rk3188
            IN_SD_FILE_PATH = "/mnt/internal_sd/";
            EX_SD_FILE_PATH = "/mnt/external_sd/";
            UDISK_FILE_PATH = "/mnt/usb_storage/";
        } else {
            //8935
            IN_SD_FILE_PATH = "/storage/sdcard0/";
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

        String savefile = "tapc/";
        TARGET_SAVEFILE_PATH = IN_SD_FILE_PATH + savefile;
        ORIGIN_SAVEFILE_PATH = MOUNTED_PATH + savefile;

        INSTALL_APP_PATH = ORIGIN_SAVEFILE_PATH + "third_app/";
    }
}
