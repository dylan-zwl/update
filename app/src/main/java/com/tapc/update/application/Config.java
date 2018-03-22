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
import java.util.ArrayList;
import java.util.List;

public class Config {
    public static String IN_SD_FILE_PATH;
    public static String EX_SD_FILE_PATH;
    public static String UDISK_FILE_PATH;

    //挂载存储器
    public static String MOUNTED_PATH = "/mnt/external_sd/";

    //复制文件路径
    public static String SAVEFILE_TARGET_PATH = "";
    public static String SAVEFILE_ORIGIN__PATH = "";
    //存放文件根目录
    public static final String SAVEFILE_NAME = "tapc/";

    //安装设备APP文件路径
    public static String INSTALL_APP_PATH = "";

    //设备软件和OS文件名：更新源文件需同名。
    public static final String UPDATE_APP_NAME = "update_app";
    public static final String UPDATE_OS_NAME = "update.zip";

    //设备软件包名
    public static String APP_PACKGGE = "com.tapc.platform";
    //测试软件包名
    public static final String TEST_APP_PACKGGE = "com.tapc.test";

    //VA 复制目标路径
    public static String VA_TARGET_PATH = "";
    //VA 复制源文件路径 ，默认U盘，没有检测到U盘，设置SD卡。
    public static String VA_ORIGIN_PATH = "";

    //是否更新APP
    public static boolean isUpdateApp = true;
    //是否覆盖安装APP
    public static boolean isCoverInstall = false;
    //是否更新MCU
    public static boolean isUpdateMcu = true;

    public static void initConfig(String mountedPath) {
        if (new File("/dev/ttyS3").exists()) {
            if (new File("/mnt/sd-ext/").exists()) {
                //s700 s900
                EX_SD_FILE_PATH = "/mnt/sd-ext/";
                UDISK_FILE_PATH = "/mnt/uhost/";
            } else {
                //rk3188
                EX_SD_FILE_PATH = "/mnt/external_sd/";
                UDISK_FILE_PATH = "/mnt/usb_storage/";
            }
        } else {
            //8935
            EX_SD_FILE_PATH = "/storage/sdcard1/";
            UDISK_FILE_PATH = "/storage/usb0/";
        }
        IN_SD_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        if (TextUtils.isEmpty(mountedPath)) {
            String temp = getUsbPath();
            if (!TextUtils.isEmpty(temp)) {
                MOUNTED_PATH = temp;
                UDISK_FILE_PATH = temp;
            } else {
                MOUNTED_PATH = EX_SD_FILE_PATH;
            }
//            File file = new File(UDISK_FILE_PATH);
//            if (file != null && file.exists() && file.list() != null && file.list().length > 0) {
//                MOUNTED_PATH = UDISK_FILE_PATH;
//            } else {
//                MOUNTED_PATH = EX_SD_FILE_PATH;
//            }
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

    private static String getUsbPath() {
        List<String> list = new ArrayList<>();
        list.add("/mnt/uhost/");
        list.add("/mnt/uhost1/");
        list.add("/mnt/uhost2/");

        list.add("/mnt/usb_storage/");

        list.add("/storage/usb0/");
        list.add("/storage/usb1/");

        String uPath = null;
        for (String path : list) {
            File file = new File(path);
            if (file != null && file.exists() && file.list() != null && file.list().length > 0) {
                uPath = path;
                break;
            }
        }
        return uPath;
    }
}
