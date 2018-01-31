package com.tapc.update.ui.presenter;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.RecoverySystem;
import android.text.TextUtils;

import com.tapc.update.R;
import com.tapc.update.utils.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2017/3/17.
 */

public class OsPresenter implements UpdateConttract.OsPresenter {
    private Context mContext;
    private UpdateConttract.View mView;
    private Handler mHandler;


    public OsPresenter(Context context, UpdateConttract.View view) {
        mHandler = new Handler();
        mContext = context;
        mView = view;
    }

    @Override
    public void update(UpdateInfor updateInfor) {
        String fileName = updateInfor.getFileName();
        if (!TextUtils.isEmpty(fileName)) {
            final File file = new File(updateInfor.getPath(), fileName);
            if (file != null && file.exists()) {
                final String cacheFilePath = Environment.getDataDirectory() + "/" + updateInfor.getFileName();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileUtil.copyFile(file.getAbsolutePath(), cacheFilePath, new
                                FileUtil.ProgressCallback() {
                                    @Override
                                    public void onProgress(final int progress) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mView.updateProgress(progress, "");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCompeleted(boolean isSuccessd, String msg) {
                                        if (isSuccessd) {
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        stopUpdate(true, "");
                                                        RecoverySystem.installPackage(mContext, new File
                                                                (cacheFilePath));

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                        stopUpdate(false, "");
                                                    }
                                                }
                                            });
                                        } else {
                                            stopUpdate(false, mContext.getString(R.string.copy_file_error));
                                        }
                                    }
                                });
                    }
                }).start();
                return;
            }
        }
        stopUpdate(false, mContext.getString(R.string.no_file));
    }

    void stopUpdate(final boolean isSuccess, final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mView.updateCompleted(isSuccess, msg);
            }
        });
    }
}
