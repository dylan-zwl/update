package com.tapc.update.ui.presenter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.tapc.update.R;
import com.tapc.update.utils.AppUtil;

import java.io.File;

/**
 * Created by Administrator on 2017/3/17.
 */

public class AppPresenter implements UpdateConttract.UpdatePresenter {
    private Context mContext;
    private UpdateConttract.View mView;

    public AppPresenter(Context context, UpdateConttract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void update(final UpdateInfor updateInfor) {
        String fileName = updateInfor.getFileName();
        if (!TextUtils.isEmpty(fileName)) {
            final File file = new File(updateInfor.getPath(), fileName);
            if (file != null && file.exists()) {

                //比较文件包名，为空时不比较
                String installPackageName = updateInfor.getPackageName();
                if (!TextUtils.isEmpty(installPackageName)) {
                    PackageManager pm = mContext.getPackageManager();
                    PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
                    if (info == null || !info.packageName.equals(installPackageName)) {
                        mView.updateCompleted(false, mContext.getString(R.string.file_Illegal));
                        return;
                    }
                }

                //开始升级
                mView.updateProgress(0, "");
                AppUtil.installApk(mContext, file, new AppUtil.ProgressListener() {
                    @Override
                    public void onCompleted(boolean isSuccessed, String message) {
                        mView.updateCompleted(isSuccessed, message);
                    }
                });
                return;
            }
        }
        mView.updateCompleted(false, mContext.getString(R.string.no_file));
    }
}
