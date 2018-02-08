package com.tapc.update.ui.presenter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.text.TextUtils;

import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.utils.AppUtil;
import com.tapc.update.utils.FileUtil;

import java.io.File;
import java.io.FilenameFilter;

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

    public static String initUpdate(Context context, String pkgName, final AppUtil.ProgressListener listener) {
        //app 升级
        String updateFilePath = CopyFilePresenter.startCopyUpdateFile();
        boolean isNeedUpdateApp = false;
        if (!TextUtils.isEmpty(updateFilePath)) {
            isNeedUpdateApp = true;
        }

        if (isNeedUpdateApp && Config.isCoverInstall) {
            //等待device app 退出
            AppUtil.exitApp(context, pkgName);
//            boolean exitAppResult = exitDeviceApp(context, Config.APP_PACKGGE);
//            if (!exitAppResult) {
//                AppUtil.unInstallApk(context, pkgName, listener);
//            }
        }
        return updateFilePath;
    }

    @Override
    public void update(String filePath) {
        String appFileName = FileUtil.getFilename(filePath, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                if (name.startsWith("app") && name.endsWith(".apk")) {
                    return true;
                }
                return false;
            }
        });
        final UpdateInfor updateInfor = new UpdateInfor();
        updateInfor.setFileType(UpdateInfor.FileType.APP);
        updateInfor.setUpdateType(UpdateInfor.UpdateType.LOCAL);
        updateInfor.setFileName(appFileName);
        updateInfor.setPath(filePath);
        updateInfor.setPackageName(Config.APP_PACKGGE);

        String fileName = updateInfor.getFileName();
        if (!TextUtils.isEmpty(fileName)) {
            final File file = new File(updateInfor.getPath(), fileName);
            if (file != null && file.exists()) {

                //比较文件包名，为空时不比较
                String installPackageName = updateInfor.getPackageName();
                if (!TextUtils.isEmpty(installPackageName)) {
                    PackageManager pm = mContext.getPackageManager();
                    PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
                    if (info == null || (!info.packageName.equals(Config.TEST_APP_PACKGGE) && !info.packageName.equals
                            (installPackageName))) {
                        mView.updateCompleted(false, mContext.getString(R.string.file_Illegal));
                        return;
                    }
                }

                //开始升级
                mView.updateProgress(0, "");

                //升级设备app，自动卸载测试软件
                String testPkgName = Config.TEST_APP_PACKGGE;
//                if (AppUtil.isAppInstalled(mContext, testPkgName)) {
                AppUtil.unInstallApk(mContext, testPkgName, new AppUtil.ProgressListener() {
                    @Override
                    public void onCompleted(boolean isSuccessed, String message) {
                    }
                });
//                }

                //卸载设备app，覆盖安装不卸载。
                if (!Config.isCoverInstall && !TextUtils.isEmpty(installPackageName)) {
//                    boolean isRunning = AppUtil.isAppRunning(mContext, installPackageName);
//                    if (isRunning) {
//                        AppUtil.unInstallApk(mContext, installPackageName, new AppUtil.ProgressListener() {
//                            @Override
//                            public void onCompleted(boolean isSuccessed, String message) {
//                                Log.d("App",)
//                            }
//                        });
//                    } else {
//                        AppUtil.clearAppUserData(mContext, installPackageName, new IPackageDataObserver.Stub() {
//                            @Override
//                            public void onRemoveCompleted(String s, boolean b) throws RemoteException {
//                            }
//                        });
//                    }

                    AppUtil.unInstallApk(mContext, installPackageName, new AppUtil.ProgressListener() {
                        @Override
                        public void onCompleted(boolean isSuccessed, String message) {
                        }
                    });
                }
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

    public static boolean exitDeviceApp(final Context context, final String pkgName) {
        boolean isResult = false;
        int time = 8;
        do {
            if (!AppUtil.isAppRunning(context, pkgName)) {
                isResult = true;
                break;
            }
            SystemClock.sleep(1000);
        } while ((time--) > 0);
        return isResult;
    }
}
