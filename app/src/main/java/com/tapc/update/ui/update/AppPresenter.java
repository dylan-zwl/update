package com.tapc.update.ui.update;

import android.content.Context;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;

import com.tapc.update.R;
import com.tapc.update.utils.AppUtil;

import java.io.File;

import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/3/17.
 */

public class AppPresenter implements UpdateConttract.AppPresenter {
    private Context mContext;
    private UpdateConttract.View mView;
    private Disposable mDisposable;
    private Handler mHandler;

    public AppPresenter(Context context, UpdateConttract.View view) {
        mHandler = new Handler();
        mContext = context;
        mView = view;
    }

    @Override
    public void update(final UpdateInfor updateInfor) {
        String fileName = updateInfor.getFileName();
        if (!TextUtils.isEmpty(fileName)) {
            final File file = new File(updateInfor.getPath(), fileName);
            if (file != null && file.exists()) {
                String installPackageName = updateInfor.getPackageName();
                if (!TextUtils.isEmpty(installPackageName)) {
                    PackageManager pm = mContext.getPackageManager();
                    PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
                    if (info == null || !info.packageName.equals(installPackageName)) {
                        stopUpdate(false, mContext.getString(R.string.file_Illegal));
                        return;
                    }
                }
                mView.updateProgress(0, "");
                boolean result = AppUtil.installApk(mContext, file, new IPackageInstallObserver.Stub() {
                    @Override
                    public void packageInstalled(String s, int i) throws RemoteException {
                        if (i == 1) {
                            stopUpdate(true, "");
                        } else {
                            stopUpdate(false, "");
                        }
                    }
                });
                if (result == false) {
                    stopUpdate(false, mContext.getString(R.string.no_system_permission));
                }
                return;
            }
        }
        stopUpdate(false, mContext.getString(R.string.no_file));
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
