package com.tapc.update.ui.update;

import android.content.Context;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;

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
        final File file = new File(updateInfor.getPath(), updateInfor.getFileName());
        if (file.exists()) {
            String installPackageName = updateInfor.getPackageName();
            if (!TextUtils.isEmpty(installPackageName)) {
                PackageManager pm = mContext.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
                if (info == null || !info.packageName.equals(installPackageName)) {
                    stopUpdate(false, "请查看文件是否是正确升级软件！");
                    return;
                }
            }
            mView.updateProgress(-1, "正在升级，请勿操作!");
            boolean result = AppUtil.installApk(mContext, file, new IPackageInstallObserver.Stub() {
                @Override
                public void packageInstalled(String s, int i) throws RemoteException {
                    if (i == 1) {
                        stopUpdate(true, "安装成功！");
                    } else {
                        stopUpdate(false, "安装失败！");
                    }
                }
            });
            if (result == false) {
                stopUpdate(false, "无系统权限安装文件！");
            }
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
