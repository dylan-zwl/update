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
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.tapc.update.application.Config;
import com.tapc.update.ui.entity.AppInfoEntity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    public interface ProgressListener {
        void onCompleted(boolean isSuccessed, String message);
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

    public static void installApk(Context context, final File file, final ProgressListener listener) {
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            Uri uri = Uri.fromFile(file);
            PackageManager pm = context.getPackageManager();
            pm.installPackage(uri, new IPackageInstallObserver.Stub() {
                @Override
                public void packageInstalled(String s, int i) throws RemoteException {
                    if (listener != null) {
                        if (i == 1) {
                            listener.onCompleted(true, "");
                        } else {
                            listener.onCompleted(false, s);
                        }
                    }
                    countDownLatch.countDown();
                }
            }, PackageManager.INSTALL_REPLACE_EXISTING, file.getAbsolutePath());
            try {
                boolean result = countDownLatch.await(180, TimeUnit.SECONDS);
                if (!result) {
                    listener.onCompleted(false, "time out");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCompleted(false, e.getMessage());
        }
    }

    public static boolean installApk(Context context, File file, IPackageInstallObserver.Stub observer) {
        try {
            Uri uri = Uri.fromFile(file);
            PackageManager pm = context.getPackageManager();
            pm.installPackage(uri, observer, PackageManager.INSTALL_REPLACE_EXISTING, file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            Log.d(TAG, "install " + file.getName() + " fail");
        }
        return false;
    }

    /**
     * 执行具体的静默安装逻辑，需要手机ROOT。
     *
     * @param path 要安装的apk文件的路径
     * @return 安装成功返回true，安装失败返回false。
     */
    public static String pmInstall(String path) {
        StringBuilder result = new StringBuilder();
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su 548");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "pm install -r " + path + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            result.append(msg);
        } catch (Exception e) {
            result.append(e.getMessage());
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                result.append(e.getMessage());
            }
        }
        return result.toString();
    }

    public static void unInstallApk(Context context, String pkgName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + pkgName));
        context.startActivity(intent);
    }

    public static void unInstallApk(Context context, String pkgName, final ProgressListener listener) {
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            PackageManager pm = context.getPackageManager();
            pm.deletePackage(pkgName, new IPackageDeleteObserver.Stub() {
                @Override
                public void packageDeleted(String s, int i) throws RemoteException {
                    if (listener != null) {
                        if (i == 1) {
                            listener.onCompleted(true, "");
                        } else {
                            listener.onCompleted(false, s);
                        }
                    }
                    countDownLatch.countDown();
                }
            }, 0);
            try {
                boolean result = countDownLatch.await(180, TimeUnit.SECONDS);
                if (!result) {
                    listener.onCompleted(false, "time out");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCompleted(false, e.getMessage());
        }
    }

    public static boolean unInstallApk(Context context, String pkgName, IPackageDeleteObserver.Stub observer) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.deletePackage(pkgName, observer, 0);
            return true;
        } catch (Exception e) {
            Log.d(TAG, "delete " + pkgName + " fail");
        }
        return false;
    }

    public static void clearAppUserData(Context context, String pakageName, IPackageDataObserver.Stub observer) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.clearApplicationUserData(pakageName, observer);
        } catch (Exception e) {
            Log.d(TAG, "clean " + pakageName + " fail");
        }
    }

    public static void clearAppUserData(Context context, String pkgName, final ProgressListener listener) {
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            PackageManager pm = context.getPackageManager();
            pm.clearApplicationUserData(pkgName, new IPackageDataObserver.Stub() {
                @Override
                public void onRemoveCompleted(String s, boolean b) throws RemoteException {
                    listener.onCompleted(b, s);
                    countDownLatch.countDown();
                }
            });
            try {
                boolean result = countDownLatch.await(180, TimeUnit.SECONDS);
                if (!result) {
                    listener.onCompleted(false, "time out");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCompleted(false, e.getMessage());
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        Log.d(TAG, pkgName + " exit fail");
        return false;
    }

    public static boolean isAppRunning(Context context, String pkgName) {
        boolean result = false;
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(Integer.MAX_VALUE);
            for (ActivityManager.RunningTaskInfo amTask : runningTasks) {
                if (pkgName.equals(amTask.baseActivity.getPackageName())) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, pkgName + " is running " + result);
        return result;
    }

    public static ArrayList<AppInfoEntity> getAllAppInfo(Context context, boolean isShowSystemApp) {
        ArrayList<AppInfoEntity> listAppInfo = new ArrayList<AppInfoEntity>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> appInfos = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo applicationInfo : appInfos) {
            if (applicationInfo != null) {
                boolean isSystemApp = false;
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                    isSystemApp = false;
                } else {
                    isSystemApp = true;
                }
                String pakageName = applicationInfo.packageName;
                if (isShowSystemApp == false && isSystemApp || pakageName.equals(context.getPackageName()) ||
                        pakageName.equals(Config.APP_PACKGGE)) {
                    continue;
                }

                AppInfoEntity appInfo = new AppInfoEntity();
                String appLabel = (String) applicationInfo.loadLabel(pm);
                Drawable icon = applicationInfo.loadIcon(pm);
                Intent launchIntent = new Intent();
                if (pakageName != null) {
                    launchIntent = context.getPackageManager().getLaunchIntentForPackage(pakageName);
                }
                appInfo.setAppLabel(appLabel);
                appInfo.setPkgName(pakageName);
                appInfo.setAppIcon(icon);
                appInfo.setSystemApp(isSystemApp);
                appInfo.setIntent(launchIntent);
                listAppInfo.add(appInfo);
            }
        }
        return listAppInfo;
    }
}
