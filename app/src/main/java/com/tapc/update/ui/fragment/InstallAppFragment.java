package com.tapc.update.ui.fragment;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.ui.adpater.InstallAdpater;
import com.tapc.update.ui.entity.AppInfoEntity;
import com.tapc.update.ui.presenter.InstallPresenter;
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

public class InstallAppFragment extends BaseFragment {
    @BindView(R.id.install_app_lv)
    RecyclerView mInstallAppLv;
    @BindView(R.id.install_all_chk)
    CheckBox mAllCheck;

    private InstallPresenter mInstallPresenter;
    private InstallAdpater mAdapter;
    private List<AppInfoEntity> mListApkInfo = new ArrayList<>();
    private Disposable mDisposable;

    @Override
    public int getContentView() {
        return R.layout.fragment_install_app;
    }

    @Override
    public void initView() {
        mHandler = new Handler();
        mInstallPresenter = new InstallPresenter(mContext);

        //全部选项勾选
        mAllCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mListApkInfo != null && !mListApkInfo.isEmpty()) {
                    for (int index = 0; index < mListApkInfo.size(); index++) {
                        mListApkInfo.get(index).setChecked(isChecked);
                    }
                    notifyChanged();
                }
            }
        });

        mDisposable = RxjavaUtils.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                mListApkInfo = mInstallPresenter.getAppList(Config.INSTALL_APP_PATH);
                if (mListApkInfo != null) {
                    e.onNext("start show");
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
                        List<AppInfoEntity> list = new ArrayList<>();
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

    private void startInstallApp(final List<AppInfoEntity> list) {
        RxjavaUtils.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                startUpdate();
                for (final AppInfoEntity appInfoEntity : list) {
                    mInstallPresenter.installApp(appInfoEntity, true, new AppUtil.ProgressListener() {
                        @Override
                        public void onCompleted(boolean isSuccessed, String message) {
                            if (isSuccessed) {
                                appInfoEntity.setInstallStatus(getResources().getString(R.string.app_install_success));
                            } else {
                                appInfoEntity.setInstallStatus(getResources().getString(R.string.app_install_fail));

                            }
                            ShowInforUtil.send(mContext, appInfoEntity.getAppLabel(), getString(R.string.install),
                                    isSuccessed, "");
                        }
                    });
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
        RxjavaUtils.dispose(mDisposable);
        mDisposable = null;
    }
}
