package com.tapc.update.ui.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.ui.presenter.InstallPresenter;
import com.tapc.update.ui.view.UpdateItem;
import com.tapc.update.utils.AppUtil;
import com.tapc.update.utils.RxjavaUtils;
import com.tapc.update.utils.ShowInforUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2017/7/10.
 */

public class VersionFragment extends BaseFragment {
    @BindView(R.id.func_name_tx)
    TextView mTitle;
    @BindView(R.id.func_start_btn)
    Button mStartUpdate;

    @BindView(R.id.update_version)
    UpdateItem mUpdateItemVersion;

    private InstallPresenter mInstallPresenter;

    @Override
    public int getContentView() {
        return R.layout.fragment_version;
    }

    @Override
    public void initView() {
        mTitle.setText(getString(R.string.func_version));
        mStartUpdate.setText(getString(R.string.a_key_update));
        mInstallPresenter = new InstallPresenter(mContext);

        String appVersion = AppUtil.getVersionName(mContext, mContext.getPackageName());
        if (!TextUtils.isEmpty(appVersion)) {
            mUpdateItemVersion.setTitle(String.format(getString(R.string.app) + " " + getString(R.string.version),
                    appVersion));
        }
    }

    @OnClick(R.id.func_start_btn)
    void start() {
        RxjavaUtils.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                startUpdate();
                String path = Config.MOUNTED_PATH + Config.SAVEFILE_NAME + Config.UPDATE_NAME;
                ShowInforUtil.send(mContext, getString(R.string.app), getString(R.string.update_infor),
                        true, getString(R.string.reboot));
                File file = new File(path);
                if (file.exists()) {
                    //延时重启应用
                    final Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext
                            .getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 1000, pi);

                    switch (Config.DEVICE_TYPE) {
                        case RK3399:
                            String result = AppUtil.pmInstall(new File(path).getAbsolutePath());
                            break;
                        default:
                            AppUtil.installApk(mContext, new File(path), new AppUtil.ProgressListener() {
                                @Override
                                public void onCompleted(boolean isSuccessed, String message) {
                                    ShowInforUtil.send(mContext, getString(R.string.app), getString(R.string.install),
                                            isSuccessed, message);
                                    stopUpdate();
                                }
                            });
                            break;
                    }
                } else {
                    ShowInforUtil.send(mContext, getString(R.string.app), getString(R.string.update_infor),
                            false, getString(R.string.no_file));
                    stopUpdate();
                }
                e.onComplete();
            }
        }, new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception {

            }
        }, null);
    }
}
