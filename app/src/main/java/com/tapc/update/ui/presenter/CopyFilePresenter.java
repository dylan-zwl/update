package com.tapc.update.ui.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.tapc.update.application.Config;
import com.tapc.update.utils.FileUtil;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Administrator on 2018/2/1.
 */

public class CopyFilePresenter {

    /**
     * 功能描述 : 复制升级文件
     */
    public static String startCopyUpdateFile() {
        String originFile = Config.SAVEFILE_ORIGIN__PATH + Config.UPDATE_APP_NAME + ".zip";
        String updateFilePath = Config.SAVEFILE_TARGET_PATH + Config.UPDATE_APP_NAME;
        boolean isCopySuccessed = copyUpdateFile(originFile, updateFilePath);
        if (isCopySuccessed) {
            return updateFilePath;
        }
        return null;
    }

    /**
     * 功能描述 : 复制文件
     */
    public static boolean copyUpdateFile(String originFile, String savePath) {
        try {
            if (TextUtils.isEmpty(originFile)) {
                return false;
            }
            File file = new File(originFile);
            if (!file.exists()) {
                return false;
            }
            File saveFile = new File(savePath);
            if (saveFile.exists()) {
                FileUtil.RecursionDeleteFile(saveFile);
            } else {
                saveFile.mkdirs();
            }

            FileUtil.upZipFile(originFile, saveFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            Log.d("copy file", e.getMessage());
        }
        return false;
    }

    /**
     * 功能描述 : va文件校验
     */
    public static boolean check(String originFile, String targetFile) {
        File listFile = new File(originFile);
        final String[] file = listFile.list();
        if (file != null && file.length > 0) {
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (originFile.endsWith(File.separator)) {
                    temp = new File(originFile + file[i]);
                } else {
                    temp = new File(originFile + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    File checkFile = new File(targetFile + "/" + (temp.getName()).toString());
                    if (checkFile.exists()) {
                        if (checkFile.length() == temp.length()) {
                            return true;
                        }
                    }
                    Log.d("check file fail", "" + checkFile.getAbsoluteFile());
                    return false;
                } else if (temp.isDirectory()) {
                    return check(originFile + "/" + file[i], targetFile + "/" + file[i]);
                }
            }
        }
        return true;
    }

    /**
     * 功能描述 : 获取va复制路径
     *
     * @param :
     */
    public static String getVaOriginPath(String path) {
        String filePath = path;
        String vaFileName = FileUtil.getFilename(filePath, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith(".va") || name.endsWith("va")) {
                    return true;
                }
                return false;
            }
        });

        if (TextUtils.isEmpty(vaFileName)) {
            vaFileName = "va";
        }
        return filePath + vaFileName;
    }
}
