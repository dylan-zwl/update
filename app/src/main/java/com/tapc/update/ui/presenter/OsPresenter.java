package com.tapc.update.ui.presenter;

import android.content.Context;
import android.os.Environment;
import android.os.RecoverySystem;
import android.text.TextUtils;

import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.utils.CopyFileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2017/3/17.
 */

public class OsPresenter implements UpdateConttract.UpdatePresenter {
    private Context mContext;
    private UpdateConttract.View mView;

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
        updateInfor.setFileName(Config.UPDATE_OS_NAME);
        update(updateInfor);
    }

    private void update(UpdateInfor updateInfor) {
        if (!TextUtils.isEmpty(updateInfor.getPath())) {
            final File file = new File(updateInfor.getPath(), updateInfor.getFileName());
            if (file != null && file.exists()) {
                final String cacheFilePath = Environment.getDataDirectory() + "/" + updateInfor.getFileName();
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
}
