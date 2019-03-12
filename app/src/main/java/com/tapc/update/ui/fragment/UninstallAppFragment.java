package com.tapc.update.ui.fragment;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tapc.update.R;
import com.tapc.update.ui.adpater.UninstallAdpater;
import com.tapc.update.ui.entity.AppInfoEntity;
import com.tapc.update.utils.AppUtil;
import com.tapc.update.utils.RxjavaUtils;
import com.tapc.update.utils.ShowInforUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class UninstallAppFragment extends BaseFragment {
    @BindView(R.id.uninstall_app_lv)
    RecyclerView mUninstallAppLv;
    @BindView(R.id.uninstall_all_chk)
    CheckBox mAllCheck;
    @BindView(R.id.show_system_app)
    CheckBox mShowSystemApp;

    private UninstallAdpater mAdapter;
    private List<AppInfoEntity> mApkInfoList = new ArrayList<AppInfoEntity>();
    private List<AppInfoEntity> mAllLstAppInfo;
    private boolean isHasGetSystemApp = false;
    private Disposable mDisposable;

    @Override
    public int getContentView() {
        return R.layout.fragment_uninstall_app;
    }

    @Override
    public void initView() {
        mHandler = new Handler();

        //全部选项勾选
        mAllCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mApkInfoList != null && mApkInfoList.size() > 0) {
                    for (int index = 0; index < mApkInfoList.size(); index++) {
                        mApkInfoList.get(index).setChecked(isChecked);
                    }
                    notifyChanged();
                }
            }
        });

        //系统选项勾选
        mShowSystemApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mAllLstAppInfo != null) {
                    isHasGetSystemApp = isChecked;
                    if (isHasGetSystemApp) {
                        mAllLstAppInfo = AppUtil.getAllAppInfo(mContext, isHasGetSystemApp);
                    }
                    mApkInfoList = getShowListApp(mAllLstAppInfo, isHasGetSystemApp);
                }
                if (mApkInfoList == null) {
                    mApkInfoList = new ArrayList<>();
                }
                mAdapter.notifyDataSetChanged(mApkInfoList);
            }
        });

        mDisposable = RxjavaUtils.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                mAllLstAppInfo = AppUtil.getAllAppInfo(mContext, false);
                mApkInfoList = getShowListApp(mAllLstAppInfo, false);
                if (mApkInfoList == null) {
                    mApkInfoList = new ArrayList<>();
                }
                e.onNext("show list");
                e.onComplete();
            }
        }, new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                mAdapter = new UninstallAdpater(mApkInfoList);
                mAdapter.setListener(new UninstallAdpater.Listener() {
                    @Override
                    public void onStart(int position) {
                        List<AppInfoEntity> list = new ArrayList<AppInfoEntity>();
                        AppInfoEntity item = mApkInfoList.get(position);
                        list.add(item);
                        item.setChecked(true);
                        notifyChanged();
                        startUninstallApp(list);
                    }
                });
                mUninstallAppLv.setLayoutManager(new LinearLayoutManager(mContext));
                mUninstallAppLv.setAdapter(mAdapter);
            }
        }, null);
    }

    /**
     * 功能描述 : 刷选App显示列表
     *
     * @param : list 列表
     * @param : isShowSystemApp 是否显示系统app
     */
    private List<AppInfoEntity> getShowListApp(List<AppInfoEntity> list, boolean isShowSystemApp) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        List<AppInfoEntity> showList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSystemApp() == false) {
                showList.add(list.get(i));
            }
        }
        if (isShowSystemApp) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isSystemApp()) {
                    showList.add(list.get(i));
                }
            }
        }
        return showList;
    }

    /**
     * 功能描述 : 卸载应用
     *
     * @param : appInfoEntity App信息
     * @param : isNeedChecked 是否需要检查App已勾选
     */
    private void uninstallApp(final AppInfoEntity appInfoEntity, boolean isNeedChecked) {
        if (isNeedChecked && appInfoEntity.isChecked() == false) {
            return;
        }
        final String pkgName = appInfoEntity.getPkgName();
        appInfoEntity.setInstallStatus("");
        AppUtil.unInstallApk(mContext, pkgName, new AppUtil.ProgressListener() {
            @Override
            public void onCompleted(boolean isSuccessed, String message) {
                if (isSuccessed) {
                    mApkInfoList.remove(appInfoEntity);
                }
                ShowInforUtil.send(mContext, appInfoEntity.getAppLabel(), getString(R.string.uninstall), isSuccessed,
                        "");
            }
        });
    }

    private void startUninstallApp(final List<AppInfoEntity> list) {
        RxjavaUtils.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                startUpdate();
                for (AppInfoEntity appInfoEntity : list) {
                    uninstallApp(appInfoEntity, true);
                    notifyChanged();
                }
                stopUpdate();
                e.onComplete();
            }
        }, new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
            }
        }, null);
    }

    /**
     * 功能描述 : 一键卸载
     *
     * @param :
     */
    @OnClick(R.id.uninstall_all_app_btn)
    void uninstallAllApp(View v) {
        if (mApkInfoList != null && !mApkInfoList.isEmpty()) {
            startUninstallApp(mApkInfoList);
        }
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
        RxjavaUtils.dispose(mDisposable);
        mDisposable = null;
    }
}
