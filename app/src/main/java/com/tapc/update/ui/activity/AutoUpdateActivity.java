package com.tapc.update.ui.activity;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tapc.platform.jni.Driver;
import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.ui.base.BaseActivity;
import com.tapc.update.ui.entity.AppInfoEntity;
import com.tapc.update.ui.presenter.AppPresenter;
import com.tapc.update.ui.presenter.CopyFilePresenter;
import com.tapc.update.ui.presenter.InstallPresenter;
import com.tapc.update.ui.presenter.McuPresenter;
import com.tapc.update.ui.presenter.UpdateConttract;
import com.tapc.update.ui.view.UpdateProgress;
import com.tapc.update.utils.AppUtil;
import com.tapc.update.utils.CopyFileUtils;
import com.tapc.update.utils.RxjavaUtils;
import com.tapc.update.utils.ShowInforUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.tapc.update.ui.presenter.CopyFilePresenter.check;

/**
 * Created by Administrator on 2018/2/1.
 */

public class AutoUpdateActivity extends BaseActivity implements UpdateProgress.Listener {
    @BindView(R.id.auto_update_infor)
    TextView mInfor;
    @BindView(R.id.auto_update_progress)
    UpdateProgress mProgress;
    @BindView(R.id.auto_update_exit)
    LinearLayout mLinearLayout;

    private Disposable mDisposable;
    private StringBuilder mStringBuilder;
    private Handler mHandler;
    private Context mContext;
    private List<Boolean> mUpdateStatusList;

    private String mUpdateFilePath;
    boolean isCopySuccessed = false;

    private AppPresenter mAppPresenter;
    private McuPresenter mMcuPresenter;
    private InstallPresenter mInstallPresenter;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_auto_update;
    }

    @Override
    public void initView() {
        AppUtil.exitApp(mContext, Config.APP_PACKGGE);
        mContext = this;
        mStringBuilder = new StringBuilder();
        mHandler = new Handler();
        mProgress.setListener(this);
        mInstallPresenter = new InstallPresenter(mContext);

        startUpdateThead();
    }

    private void startUpdateThead() {
        mDisposable = RxjavaUtils.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("start");
                mUpdateStatusList = new ArrayList<Boolean>();

                //app 升级
                mUpdateFilePath = CopyFilePresenter.startCopyUpdateFile();
                appstartUpdateThead();
                mcustartUpdateThead();

                //第三方应用安装
                String appPath = Config.TARGET_SAVEFILE_PATH + "/third_app";
                List<AppInfoEntity> list = mInstallPresenter.getAppList(appPath);
                if (list != null && list.size() > 0) {
                    for (final AppInfoEntity appInfoEntity : list) {
                        mInstallPresenter.installApp(appInfoEntity, false, new AppUtil.ProgressListener() {
                            @Override
                            public void onCompleted(boolean isSuccessed, String message) {
                                addInforShow(appInfoEntity.getAppLabel(), getString(R.string.install), isSuccessed,
                                        message);
                                mUpdateStatusList.add(isSuccessed);
                            }
                        });
                    }
                }

                String va = "va";
                String originFile = Config.ORIGIN_SAVEFILE_PATH + va;
                String targetFile = Config.TARGET_SAVEFILE_PATH + va;
                File file = new File(originFile);
                if (file.exists()) {
                    if (!check(originFile, targetFile)) {

                        long startTime = System.currentTimeMillis();
                        boolean result = new CopyFileUtils().copyFolder(originFile, targetFile, new CopyFileUtils
                                .ProgressCallback() {
                            @Override
                            public void onProgress(int progress) {
                                updateProgressUi(progress);
                            }

                            @Override
                            public void onCompeleted(boolean isSuccessed, String msg) {
                                addInforShow(getString(R.string.va), getString(R.string.copy), isSuccessed, msg);
                            }
                        });
                        if (result) {
                            result = CopyFilePresenter.check(originFile, targetFile);
                        }
                        mUpdateStatusList.add(result);

                        long usetime = (System.currentTimeMillis() - startTime) / 1000;
                        Log.d("copy progress", "  use time: " + usetime);
                    } else {
                        addInforShow(getString(R.string.va), getString(R.string.copy), true, "");
                    }
                }

                e.onNext("finished");

                e.onComplete();
            }
        }, new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                switch ((String) o) {
                    case "start":
                        mLinearLayout.setVisibility(View.GONE);
                        mProgress.setUpdateProgressVisibility(View.VISIBLE, false);
                        break;
                    case "finished":
                        mLinearLayout.setVisibility(View.VISIBLE);
                        mProgress.setUpdateStatus(checkUpdateStatus(mUpdateStatusList));
                        break;
                }
            }
        }, null);
    }

    private void appstartUpdateThead() {
        if (mAppPresenter == null) {
            mAppPresenter = new AppPresenter(mContext, new UpdateConttract.View() {
                @Override
                public void updateProgress(int percent, String msg) {

                }

                @Override
                public void updateCompleted(final boolean isSuccess, final String msg) {
                    addInforShow(getString(R.string.app), getString(R.string.update), isSuccess, msg);
                    mUpdateStatusList.add(isSuccess);
                }
            });
        }
        mAppPresenter.update(mUpdateFilePath);
    }

    private void mcustartUpdateThead() {
        if (mMcuPresenter == null) {
            mMcuPresenter = new McuPresenter(mContext, new UpdateConttract.View() {
                @Override
                public void updateProgress(int percent, String msg) {
                    updateProgressUi(percent);
                }

                @Override
                public void updateCompleted(boolean isSuccess, String msg) {
                    addInforShow(getString(R.string.mcu), getString(R.string.update), isSuccess, msg);
                    mUpdateStatusList.add(isSuccess);
                }
            });
        }
        mMcuPresenter.update(mUpdateFilePath);
    }

    /**
     * 功能描述 : 信息显示
     *
     * @param :
     */
    private void addInforShow(String title, String updateType, boolean isSuccess, String msg) {
        String info = ShowInforUtil.getInforText(mContext, title, updateType, isSuccess, msg);
        if (mStringBuilder == null) {
            mStringBuilder = new StringBuilder();
        }
        mStringBuilder.append(info + "\n");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String text = mStringBuilder.toString();
                if (text != null) {
                    mInfor.setText(text);
                }
            }
        });
    }

    private void updateProgressUi(final int progress) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgress.setProgress(progress);
            }
        });
    }

    private boolean checkUpdateStatus(List<Boolean> list) {
        for (boolean result : list) {
            if (!result) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void updateAgainOnClick() {
        mStringBuilder = new StringBuilder();
        mInfor.setText("");
        startUpdateThead();
    }

    @OnClick(R.id.auto_update_exit)
    void exit() {
        Driver.home();
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }
}
