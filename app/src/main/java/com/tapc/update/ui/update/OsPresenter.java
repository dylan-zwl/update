package com.tapc.update.ui.update;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.RecoverySystem;

import com.tapc.update.utils.FileUtil;

import java.io.File;
import java.io.IOException;

import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/3/17.
 */

public class OsPresenter implements UpdateConttract.OsPresenter {
    private Context mContext;
    private UpdateConttract.View mView;
    private Disposable mDisposable;
    private Handler mHandler;


    public OsPresenter(Context context, UpdateConttract.View view) {
        mHandler = new Handler();
        mContext = context;
        mView = view;
    }

    @Override
    public void update(UpdateInfor updateInfor) {
        final File file = new File(updateInfor.getPath(), updateInfor.getFileName());
        if (file.exists()) {
            final String cacheFilePath = Environment.getDataDirectory() + updateInfor.getFileName();
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
                                            mView.updateProgress(progress, "正在复制文件...");
                                        }
                                    });
                                }

                                @Override
                                public void finish(int error) {
                                    if (error == 0) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                stopUpdate(true, "正在进入升级，请勿操作！");
                                                try {
                                                    RecoverySystem.installPackage(mContext, new File(cacheFilePath));
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    stopUpdate(false, "升级失败！");
                                                }
                                            }
                                        });
                                    } else {
                                        stopUpdate(false, "复制文件错误！");
                                    }
                                }
                            });
                }
            }).start();
        } else {
            stopUpdate(false, "文件不存在,请查看文件路径！");
        }
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
