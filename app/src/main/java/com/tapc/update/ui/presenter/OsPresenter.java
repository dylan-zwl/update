package com.tapc.update.ui.presenter;

import android.content.Context;
import android.os.RecoverySystem;
import android.text.TextUtils;

import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.utils.CopyFileUtils;
import com.tapc.update.utils.FileUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Created by Administrator on 2017/3/17.
 */

public class OsPresenter implements UpdateConttract.UpdatePresenter {
    private Context mContext;
    private UpdateConttract.View mView;
    private boolean isNeedUpdate;

    public OsPresenter(Context context, UpdateConttract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void update(String filePath) {
        UpdateInfor updateInfor = new UpdateInfor();
        updateInfor.setFileType(UpdateInfor.FileType.OS);
        updateInfor.setUpdateType(UpdateInfor.UpdateType.LOCAL);
        updateInfor.setPath(filePath);
        updateInfor.setSavePath(Config.UPDATE_OS_SAVE_PATH);
        String osName = checkHasOsFile(filePath);
        if (!TextUtils.isEmpty(osName)) {
            updateInfor.setFileName(osName);
        } else {
            updateInfor.setFileName(Config.UPDATE_OS_NAME);
        }
        isNeedUpdate = checkNeedUpdate(updateInfor.getSavePath(), updateInfor.getFileName());
        update(updateInfor);
    }

    private void update(UpdateInfor updateInfor) {
        if (!TextUtils.isEmpty(updateInfor.getPath())) {
            final File file = new File(updateInfor.getPath(), updateInfor.getFileName());
            if (file != null && file.exists()) {
                if (!isNeedUpdate) {
                    mView.updateCompleted(true, mContext.getString(R.string.os_same));
                    return;
                }
                deleteOldOs(updateInfor);
                final String cacheFilePath = updateInfor.getSavePath() + "/" + updateInfor.getFileName();
                CopyFileUtils.copyFile(file.getAbsolutePath(), cacheFilePath, new CopyFileUtils.ProgressCallback() {
                    @Override
                    public void onProgress(final int progress) {
                        mView.updateProgress(progress, "");
                    }

                    @Override
                    public void onCompeleted(boolean isSuccessed, String msg) {
                        if (isSuccessed) {
                            try {
                                mView.updateCompleted(true, "");
                                RecoverySystem.installPackage(mContext, new File(cacheFilePath));
                            } catch (IOException e) {
                                e.printStackTrace();
                                mView.updateCompleted(false, e.getMessage());
                            }
                        } else {
                            mView.updateCompleted(false, msg);
                        }
                    }
                });
                return;
            }
        }
        mView.updateCompleted(false, mContext.getString(R.string.no_file));
    }

    /**
     * 功能描述 : 检测是否是相同os
     *
     * @param :
     */
    public boolean isNeedUpdate() {
        return isNeedUpdate;
    }

    public static String checkHasOsFile(String originPath) {
        String osName = FileUtil.getFilename(originPath, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                if (name.startsWith("update_os") && name.endsWith(".zip") || name.equals(Config.UPDATE_OS_NAME)) {
                    return true;
                }
                return false;
            }
        });
        return osName;
    }

    public static boolean checkNeedUpdate(String path, String fileName) {
        File file = new File(path, fileName);
        if (file.exists()) {
            return false;
        }
        return true;
    }

    private void deleteOldOs(UpdateInfor updateInfor) {
        String[] osList = FileUtil.getFiles(updateInfor.getSavePath(), new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                if (name.startsWith("update_os") && name.endsWith(".zip")) {
                    return true;
                }
                return false;
            }
        });
        if (osList != null && osList.length > 0) {
            for (String path : osList) {
                FileUtil.RecursionDeleteFile(new File(path));
            }
        }
    }
}
