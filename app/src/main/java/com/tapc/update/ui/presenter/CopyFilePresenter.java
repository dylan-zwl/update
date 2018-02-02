package com.tapc.update.ui.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.tapc.update.utils.FileUtil;

import java.io.File;

/**
 * Created by Administrator on 2018/2/1.
 */

public class CopyFilePresenter {

    /**
     * 功能描述 : 复制文件
     */
    public boolean copyUpdateFile(String originFile, String savePath) {
        try {
            if (TextUtils.isEmpty(originFile) || !new File(originFile).exists()) {
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
}
