package com.tapc.update.ui.fragment.install;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.ui.adpater.InstallAdpater;
import com.tapc.update.ui.entity.AppInfoEntity;
import com.tapc.update.ui.fragment.BaseFragment;
import com.tapc.update.utils.AppUtil;
import com.tapc.update.utils.FileUtil;
import com.tapc.update.utils.RxjavaUtils;
import com.tapc.update.utils.ShowInforUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class InstallAppFragment extends BaseFragment {
    @BindView(R.id.install_app_lv)
    RecyclerView mInstallAppLv;
    @BindView(R.id.install_all_chk)
    CheckBox mAllCheck;

    private InstallAdpater mAdapter;
    private List<String> mListFilePath = new ArrayList<String>();
    private List<AppInfoEntity> mListApkInfo = new ArrayList<AppInfoEntity>();

    @Override
    public int getContentView() {
        return R.layout.fragment_install_app;
    }

    @Override
    public void initView() {
        mHandler = new Handler();

        //全部选项勾选
        mAllCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mListApkInfo != null) {
                    for (int index = 0; index < mListApkInfo.size(); index++) {
                        mListApkInfo.get(index).setChecked(isChecked);
                    }
                    notifyChanged();
                }
            }
        });

        RxjavaUtils.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String path = Config.MOUNTED_PATH + Config.SAVEFILE_PATH + "/" + Config.INSTALL_APP_PATH;
                FileUtil.getFiles(path, new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if (name.toLowerCase().contains(".apk")) {
                            mListFilePath.add(dir + "/" + name);
                        }
                        return false;
                    }
                });
                if (mListFilePath != null && mListFilePath.size() > 0) {
                    getAppList();
                    if (mListApkInfo != null && !mListApkInfo.isEmpty()) {
                        e.onNext("show list");
                    }
                }
                e.onComplete();
            }
        }, new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                mAdapter = new InstallAdpater(mListApkInfo);
                mAdapter.setListener(new InstallAdpater.Listener() {
                    @Override
                    public void onStart(int position) {
                        List<AppInfoEntity> list = new ArrayList<AppInfoEntity>();
                        AppInfoEntity item = mListApkInfo.get(position);
                        list.add(item);
                        item.setChecked(true);
                        notifyChanged();
                        startInstallApp(list);
                    }
                });
                mInstallAppLv.setLayoutManager(new LinearLayoutManager(mContext));
                mInstallAppLv.setAdapter(mAdapter);
            }
        }, null);
    }

    /**
     * 功能描述 : 获取App显示列表
     */
    private void getAppList() {
        for (String apkPath : mListFilePath) {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                AppInfoEntity appEntity = new AppInfoEntity();
                // 得到安装包名称
                appInfo.sourceDir = apkPath;
                appInfo.publicSourceDir = apkPath;
                appEntity.setPath(apkPath);
                try {
                    appEntity.setAppLabel(appInfo.loadLabel(pm).toString());
                    appEntity.setAppIcon(appInfo.loadIcon(pm));
                } catch (OutOfMemoryError e) {
                    Log.e("ApkIconLoader", e.toString());
                }
                Log.d("app file", "" + appEntity.getAppLabel());
                mListApkInfo.add(appEntity);
            }
        }
    }

    private void installApp(final AppInfoEntity appInfoEntity, boolean isNeedChecked) {
        if (isNeedChecked && appInfoEntity.isChecked() == false) {
            return;
        }
        appInfoEntity.setInstallStatus("");
        String path = appInfoEntity.getPath();
        appInfoEntity.setInstallStatus("");
        AppUtil.installApk(mContext, new File(path), new AppUtil.ProgressListener() {
            @Override
            public void onCompleted(boolean isSuccessed, String message) {
                if (isSuccessed) {
                    appInfoEntity.setInstallStatus(getResources().getString(R.string.app_install_success));
                } else {
                    appInfoEntity.setInstallStatus(getResources().getString(R.string.app_install_fail));

                }
                ShowInforUtil.send(mContext, appInfoEntity.getAppLabel(), getString(R.string.install), isSuccessed,
                        "");
            }
        });
    }

    private void startInstallApp(final List<AppInfoEntity> list) {
        RxjavaUtils.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                startUpdate();
                for (AppInfoEntity appInfoEntity : list) {
                    installApp(appInfoEntity, true);
                    notifyChanged();
                }
                stopUpdate();
            }
        }, new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
            }
        }, null);
    }

    @OnClick(R.id.install_all_app_btn)
    void installAllApp(View v) {
        startInstallApp(mListApkInfo);
    }

    private void notifyChanged() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }
}
