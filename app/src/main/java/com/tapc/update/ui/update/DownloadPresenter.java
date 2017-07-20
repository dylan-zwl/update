package com.tapc.update.ui.update;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.tapc.update.utils.okhttp.OkHttpUtils;
import com.tapc.update.utils.okhttp.callback.FileCallBack;

import java.io.File;

import io.reactivex.disposables.Disposable;
import okhttp3.Call;

/**
 * Created by Administrator on 2017/3/17.
 */

public class DownloadPresenter implements UpdateConttract.AppPresenter {
    private Context mContext;
    private UpdateConttract.View mView;
    private Disposable mDisposable;
    private Handler mHandler;

    public DownloadPresenter(Context context, UpdateConttract.View view) {
        mHandler = new Handler();
        mContext = context;
        mView = view;
    }

    @Override
    public void update(final UpdateInfor updateInfor) {
        String url = updateInfor.getFileUrl();
        String savePath = updateInfor.getSavePath();
        String fileName = updateInfor.getFileName();
        File file = new File(savePath, fileName);
        if (file != null && file.exists()) {
            file.delete();
        }
        if (TextUtils.isEmpty(url)) {
            stopUpdate(false, "");
            return;
        }
        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(savePath, fileName) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        stopUpdate(false, e.getMessage());
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        stopUpdate(true, "");
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        mView.updateProgress((int) (progress * 100), "");
                    }
                });
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
