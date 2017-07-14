package com.tapc.update.ui.fragment.uninstall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.tapc.update.R;
import com.tapc.update.ui.entity.AppInfoEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UninstallAppFragment extends Fragment {
    @BindView(R.id.uninstall_app_grid)
    GridView mUninstallGrid;

    private Context mContext;
    //    private AppGridViewAdapter mAdapter;
    private List<AppInfoEntity> mlistAppInfo;

    private List<String> mListFilePath = new ArrayList<String>();
    private List<String> mInternalApp = new ArrayList<String>();
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uninstall_app, container, false);
        ButterKnife.bind(this, view);
        initUI();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initUI() {
//        getThirdApplication();
    }

//    private void getThirdApplication() {
//        mlistAppInfo = new ArrayList<AppInfoEntity>();
//        mlistAppInfo = AppUtil.getAllAppInfo(mContext);
//        mAdapter = new AppGridViewAdapter(mContext, mlistAppInfo);
//        mUninstallGrid.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
//        mUninstallGrid.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
//                AppUtil.unInstallApk(mContext, mlistAppInfo.get(position).getPkgName());
//            }
//        });
//        mHandler.sendMessageDelayed(mHandler.obtainMessage(0), 1000);
//
//        getFiles("/system/preinstall", "apk", true);
//        if (mListFilePath != null && mListFilePath.size() > 0) {
//            getAppList();
//        }
//
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(mContext);
//            mProgressDialog.setMax(mlistAppInfo.size());
//            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.hide();
//        }
//    }
//
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    checkRemoveApp();
//                    mHandler.sendMessageDelayed(mHandler.obtainMessage(0), 1000);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    private void checkRemoveApp() {
//        for (int i = 0; i < mlistAppInfo.size(); i++) {
//            if (isAppHasRemove(mContext, mlistAppInfo.get(i).getPkgName())) {
//                mlistAppInfo.remove(i);
//            }
//        }
//        mAdapter.notifyDataSetChanged();
//    }
//
//    private boolean isAppHasRemove(Context context, String packagename) {
//        PackageInfo packageInfo;
//        try {
//            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
//        } catch (NameNotFoundException e) {
//            packageInfo = null;
//        }
//        if (packageInfo == null) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    private void getFiles(String Path, String Extension, boolean IsIterative) // 搜索目录，扩展名，是否进入子文件夹
//    {
//        File[] files = new File(Path).listFiles();
//        if (files != null) {
//            for (int i = 0; i < files.length; i++) {
//                File f = files[i];
//                if (f.isFile()) {
//                    if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension)) // 判断扩展名
//                        mListFilePath.add(f.getPath());
//                    if (!IsIterative)
//                        break;
//                } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) // 忽略点文件（隐藏文件/文件夹）
//                    getFiles(f.getPath(), Extension, IsIterative);
//            }
//        }
//    }
//
//    private void getAppList() {
//        for (String apkPath : mListFilePath) {
//            PackageManager pm = mContext.getPackageManager();
//            PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
//            if (info != null) {
//                ApplicationInfo appInfo = info.applicationInfo;
//                Log.d("Internal App", "" + appInfo.packageName);
//                mInternalApp.add(appInfo.packageName);
//            }
//        }
//    }
//
//    private Handler progressHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            if (msg.what == 255) {
//                mProgressDialog.hide();
//            } else {
//                mProgressDialog.show();
//                mProgressDialog.setProgress(msg.what);
//            }
//        }
//
//        ;
//    };
//
//    private Runnable installAppRunnable = new Runnable() {
//        public void run() {
//            List<AppInfoEntity> listAppInfo = new ArrayList<AppInfoEntity>();
//            listAppInfo.addAll(mlistAppInfo);
//            progressHandler.sendEmptyMessage(0);
//            if (listAppInfo != null && listAppInfo.size() > 0) {
//                for (AppInfoEntity appInfoEntity : listAppInfo) {
//                    boolean notUninstallApp = false;
//
//                    if (mInternalApp != null && mInternalApp.size() > 0) {
//                        for (String pakName : mInternalApp) {
//                            if (appInfoEntity.getPkgName().equals(pakName)) {
//                                notUninstallApp = true;
//                                break;
//                            }
//                        }
//                    }
////                    if (!notUninstallApp) {
////                        AppUtil.unInstallApk(mContext, appInfoEntity.getPkgName(), packageDeleteObserver);
////                        while (!packageDeleteObserver.getStatus()) {
////                            SystemClock.sleep(200);
////                        }
////                    }
//                    progressHandler.sendEmptyMessage(listAppInfo.indexOf(appInfoEntity) + 1);
//                }
//            }
//            progressHandler.sendEmptyMessage(listAppInfo.size());
//            SystemClock.sleep(2000);
//            progressHandler.sendEmptyMessage(255);
//        }
//    };
//
//    @OnClick(R.id.a_key_uninstall)
//    protected void unistallApp(View v) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setMessage(getString(R.string.a_key_uninstall_app)).setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        new Thread(installAppRunnable).start();
//                    }
//                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
}
