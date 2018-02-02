package com.tapc.update.ui.presenter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.tapc.update.ui.entity.AppInfoEntity;
import com.tapc.update.utils.AppUtil;
import com.tapc.update.utils.FileUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

/**
 * Created by Administrator on 2018/2/1.
 */

public class InstallPresenter {
    private Context mContext;

    public InstallPresenter(Context context) {
        mContext = context;
    }


    /**
     * 功能描述 : 获取App显示列表
     */
    public List<AppInfoEntity> getAppList(@NonNull String path) {
        List<AppInfoEntity> listApkInfo = new ArrayList<>();
        String[] filePathList = FileUtil.getFiles(path, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.toLowerCase().contains(".apk")) {
                    return true;
                }
                return false;
            }
        });
        if (filePathList != null && filePathList.length > 0) {
            for (String apkPath : filePathList) {
                PackageManager pm = mContext.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(path + "/" + apkPath, PackageManager.GET_ACTIVITIES);
                if (info != null) {
                    ApplicationInfo appInfo = info.applicationInfo;
                    AppInfoEntity appEntity = new AppInfoEntity();
                    // 得到安装包名称
                    appInfo.sourceDir = apkPath;
                    appInfo.publicSourceDir = apkPath;
                    try {
                        appEntity.setAppLabel(appInfo.loadLabel(pm).toString());
                        appEntity.setAppIcon(appInfo.loadIcon(pm));
                        appEntity.setPath(apkPath);
                        appEntity.setVersion(info.versionName);
                        listApkInfo.add(appEntity);
                        Log.d("app file", "" + appEntity.getAppLabel());
                    } catch (OutOfMemoryError e) {
                        Log.e("app file icon loader", e.toString());
                    }
                }
            }
        }
        return listApkInfo;
    }

    public void installApp(final AppInfoEntity appInfoEntity, boolean isNeedChecked, final AppUtil.ProgressListener
            listener) {
        if (isNeedChecked && appInfoEntity.isChecked() == false) {
            return;
        }

        PackageInfo packageInfo = AppUtil.getPackageInfo(mContext, appInfoEntity.getPkgName());
        if (packageInfo != null && packageInfo.versionName.equals(appInfoEntity.getVersion())) {
            listener.onCompleted(true, "");
            return;
        }

        appInfoEntity.setInstallStatus("");
        String path = appInfoEntity.getPath();
        appInfoEntity.setInstallStatus("");
        AppUtil.installApk(mContext, new File(path), listener);
    }
}
