/**
 * java[v 1.0.0]
 * classes:com.oxbix.tapc.Config
 * fch Create of at 2015�?2�?3�? 下午5:10:26
 */
package com.tapc.update.application;

import java.io.File;

public class Config {
    public static String IN_SD_FILE_PATH;
    public static String EX_SD_FILE_PATH;
    public static String UDISK_FILE_PATH;
    public static String VA_FILE_PATH_SD;
    public static String VA_FILE_PATH_NAND;
    public static String VA_FILE_PATH_USB;
    public static String CACHE_FILE_PATH;
    public static String FILE_PATH;

    /**
     * 升级文件名字
     */
    public static final String UPDATE_APP_FILE_NAME = "tapc_platform.apk";
    public static final String UPDATE_MCU_FILE_NAME = "ROM.bin";
    public static final String APP_PACKGGE = "com.tapc.platform";
    public static final String TAPC_FILE_PATH = "tapc/";

    public static String MOUNTED_PATH = "/mnt/external_sd/";

    public static void initConfig() {
        if (new File("/dev/ttyS3").exists()) {
            //rk3188
            IN_SD_FILE_PATH = "/mnt/internal_sd/";
            EX_SD_FILE_PATH = "/mnt/external_sd/";
            UDISK_FILE_PATH = "/mnt/usb_storage/";
            VA_FILE_PATH_SD = "/mnt/external_sd/.va/";
            VA_FILE_PATH_NAND = "/mnt/internal_sd/.va/";
            CACHE_FILE_PATH = "/cache/";
            VA_FILE_PATH_USB = "/mnt/usb_storage/va/";
        } else {
            //8935
            IN_SD_FILE_PATH = "/storage/sdcard0/";
            EX_SD_FILE_PATH = "/storage/sdcard1/";
            UDISK_FILE_PATH = "/storage/usb0/";
            VA_FILE_PATH_SD = "/storage/sdcard1/.va/";
            VA_FILE_PATH_NAND = "/storage/sdcard0/.va/";
            CACHE_FILE_PATH = "/cache/";
            VA_FILE_PATH_USB = "/mnt/usb_storage/va/";
        }
        FILE_PATH = UDISK_FILE_PATH + TAPC_FILE_PATH;
    }

    public static String INSTALL_APP_FILE_PATH = "/mnt/external_sd/";
}
