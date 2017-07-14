package com.tapc.update.ui.update;

import android.content.Context;
import android.os.Handler;

import com.tapc.platform.model.device.controller.IOUpdateController;
import com.tapc.platform.model.device.controller.MachineController;

import java.io.File;

import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/3/17.
 */

public class McuPresenter implements UpdateConttract.McuPresenter {
    private Context mContext;
    private UpdateConttract.View mView;
    private Disposable mDisposable;
    private Handler mHandler;
    private MachineController mController;
    private String mMsg = "";

    public McuPresenter(Context context, UpdateConttract.View view) {
        mHandler = new Handler();
        mContext = context;
        mView = view;
    }

    @Override
    public void update(final UpdateInfor updateInfor) {
        final File file = new File(updateInfor.getPath(), updateInfor.getFileName());
        if (file.exists()) {
            mController = MachineController.getInstance();
            mController.updateMCU(file.getAbsolutePath(), new IOUpdateController.IOUpdateListener() {
                @Override
                public void onProgress(int process, String msg) {
                    mView.updateProgress(process, msg);
                }

                @Override
                public void successful(String msg) {
                    stopUpdate(true, msg);
                }

                @Override
                public void failed(String msg) {
                    stopUpdate(false, msg);
                }
            });
            mView.updateProgress(0, "");
        } else {
            stopUpdate(false, "文件不存在,请查看文件路径！");
        }
    }

    void stopUpdate(final boolean isSuccess, final String msg) {
        if (mDisposable != null) {
            if (mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
            mDisposable = null;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mView.updateCompleted(isSuccess, msg);
            }
        });
    }
}
