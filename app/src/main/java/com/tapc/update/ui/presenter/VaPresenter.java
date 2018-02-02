package com.tapc.update.ui.presenter;

import android.util.Log;

import java.io.File;

/**
 * Created by Administrator on 2018/2/1.
 */

public class VaPresenter {

    public boolean check(String originFile, String targetFile) {
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
                    Log.d("check file fail:", "" + checkFile.getAbsoluteFile());
                    return false;
                } else if (temp.isDirectory()) {
                    return check(originFile + "/" + file[i], targetFile + "/" + file[i]);
                }
            }
        }
        return true;
    }
}
