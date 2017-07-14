package com.tapc.update.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.tapc.update.ui.entity.AppInfoEntity;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class AppUtil {
    private static final String TAG = "AppUtil";

    @SuppressLint("NewApi")
    public static void setInstallAppPermission(Context context, boolean openFlag) {
        try {
            boolean appPermisson = (Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.INSTALL_NON_MARKET_APPS, 0) > 0 ? true : false);
            if (openFlag != appPermisson) {
                if (openFlag) {
                    Settings.Global.putInt(context.getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS, 1);
                } else {
                    Settings.Global.putInt(context.getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS, 0);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "set install app permisson fail");
        }
    }

    /**
     * 安装apk
     *
     * @param context
     * @param path
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static boolean installApk(Context context, File file, IPackageInstallObserver.Stub observer) {
        try {
            Uri uri = Uri.fromFile(file);
            PackageManager pm = context.getPackageManager();
            pm.installPackage(uri, observer, PackageManager.INSTALL_REPLACE_EXISTING, file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            Log.d(TAG, "install package fail");
        }
        return false;
    }

    public static void unInstallApk(Context context, String pkgName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + pkgName));
        context.startActivity(intent);
    }

    public static void unInstallApk(Context context, String pkgName, IPackageDeleteObserver.Stub observer) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.deletePackage(pkgName, observer, 0);
        } catch (Exception e) {
            Log.d(TAG, "delete package fail");
        }
    }

    public static void clearAppUserData(Context context, String pakageName, IPackageDataObserver.Stub observer) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.clearApplicationUserData(pakageName, observer);
        } catch (Exception e) {
            Log.d(TAG, "clean data fail");
        }
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    public static PackageInfo getPackageInfo(Context context, String pkgName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(pkgName, 0);
            if (packageInfo != null) {
                return packageInfo;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static String getVersionName(Context context, String pkgName) {
        PackageInfo packageInfo = getPackageInfo(context, pkgName);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return null;
    }

    public static boolean exitApp(Context context, String pkgName) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(Integer.MAX_VALUE);
            for (ActivityManager.RunningTaskInfo amTask : runningTasks) {
                if (pkgName.equals(amTask.baseActivity.getPackageName())) {
                    Method method = null;
                    method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage",
                            String.class);
                    method.invoke(manager, amTask.baseActivity.getPackageName());
                    Log.d(TAG, pkgName + " exit success");
                }
            }
            return true;
        } catch (Exception e) {
        }
        Log.d(TAG, pkgName + " exit fail");
        return false;
    }

    public static ArrayList<AppInfoEntity> getAllAppInfo(Context context) {
        ArrayList<AppInfoEntity> mlistAppInfo = new ArrayList<AppInfoEntity>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> appInfos = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo applicationInfo : appInfos) {
            if (applicationInfo != null) {
                String pakageName = applicationInfo.packageName;
                String appLabel = (String) applicationInfo.loadLabel(pm);
                Drawable icon = applicationInfo.loadIcon(pm);
                Intent launchIntent = new Intent();
                if (pakageName != null) {
                    launchIntent = context.getPackageManager().getLaunchIntentForPackage(pakageName);
                }
                AppInfoEntity appInfo = new AppInfoEntity();

                appInfo.setAppLabel(appLabel);
                appInfo.setPkgName(pakageName);
                appInfo.setAppIcon(icon);
                appInfo.setIntent(launchIntent);
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                    appInfo.setSystemApp(false);
                } else {
                    appInfo.setSystemApp(true);
                }
                mlistAppInfo.add(appInfo);
            }
        }
        return mlistAppInfo;
    }
}
