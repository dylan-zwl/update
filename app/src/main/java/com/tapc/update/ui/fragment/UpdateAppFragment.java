package com.tapc.update.ui.fragment;

import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.ui.presenter.AppPresenter;
import com.tapc.update.ui.presenter.McuPresenter;
import com.tapc.update.ui.presenter.UpdateConttract;
import com.tapc.update.ui.view.UpdateItem;
import com.tapc.update.utils.AppUtil;
import com.tapc.update.utils.RxjavaUtils;
import com.tapc.update.utils.ShowInforUtil;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * Created by Administrator on 2017/7/10.
 */

public class UpdateAppFragment extends BaseFragment {
    @BindView(R.id.func_name_tx)
    TextView mTitle;
    @BindView(R.id.func_start_btn)
    Button mStartUpdate;

    @BindView(R.id.update_app)
    UpdateItem mUpdateItemApp;
    @BindView(R.id.update_mcu)
    UpdateItem mUpdateItemMcu;
    @BindView(R.id.update_restore)
    UpdateItem mUpdateItemRestore;

    private static final String TAG = UpdateAppFragment.class.getSimpleName();
    private String mUpdateFilePath;
    private Disposable mDisposable;
    private AppPresenter mAppPresenter;
    private McuPresenter mMcuPresenter;

    @Override
    public int getContentView() {
        return R.layout.fragment_app;
    }

    @Override
    public void initView() {
        mTitle.setText(getString(R.string.func_app));
        mStartUpdate.setText(getString(R.string.a_key_update));

        initVersionShow();
    }

    private void initVersionShow() {
        MachineController.getInstance().sendCtlVersionCmd(null);
        SystemClock.sleep(500);
        String appVersion = AppUtil.getVersionName(mContext, Config.APP_PACKGGE);
        String mcuVersion = MachineController.getInstance().getCtlVersionValue();
        if (TextUtils.isEmpty(appVersion)) {
            appVersion = "";
        }
        if (TextUtils.isEmpty(mcuVersion)) {
            mcuVersion = "";
        }
        mUpdateItemApp.setTitle(getString(R.string.app) + String.format(getString(R.string.version), appVersion));
        mUpdateItemMcu.setTitle(getString(R.string.mcu) + String.format(getString(R.string.version), mcuVersion));

        mUpdateItemRestore.setTitle(getString(R.string.app) + "  " + getString(R.string.mcu));
    }

    @OnClick(R.id.func_start_btn)
    void start() {
        startUpdateThead(Mode.ALL, true);
    }

    @OnClick(R.id.update_app)
    void startUpdateApp() {
        startUpdateThead(Mode.ONLY_APP, true);
    }

    @OnClick(R.id.update_mcu)
    void startUpdateMcu() {
        startUpdateThead(Mode.ONLY_MCU, true);
    }

    @OnClick(R.id.update_restore)
    void startRestore() {
        startUpdateThead(Mode.ALL, false);
    }

    private enum Mode {
        ALL,
        ONLY_APP,
        ONLY_MCU
    }

    private void startUpdateThead(final Mode mode, final boolean isCopyFile) {
        mDisposable = RxjavaUtils.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("start");

                //app 升级
                if (isCopyFile) {
                    mUpdateFilePath = AppPresenter.initUpdate(mContext, Config.APP_PACKGGE, new AppUtil
                            .ProgressListener() {

                        @Override
                        public void onCompleted(boolean isSuccessed, String message) {
                            ShowInforUtil.send(mContext, getString(R.string.app), getString(R.string.uninstall),
                                    isSuccessed, message);
                        }
                    });
                } else {
                    mUpdateFilePath = Config.SAVEFILE_TARGET_PATH + Config.UPDATE_APP_NAME;
                }

                switch (mode) {
                    case ONLY_APP:
                        appStartUpdate();
                        break;
                    case ONLY_MCU:
                        mcuStartUpdate();
                        break;
                    case ALL:
                        appStartUpdate();
                        mcuStartUpdate();
                        break;
                }
                e.onNext("stop");
                SystemClock.sleep(4000);
                e.onNext("show version");
                e.onComplete();
            }
        }, new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                switch ((String) o) {
                    case "start":
                        startUpdate();
                        break;
                    case "stop":
                        stopUpdate();
                        break;
                    case "show version":
                        initVersionShow();
                        break;
                }
            }
        }, null);
    }

    private void appStartUpdate() {
        if (mAppPresenter == null) {
            mAppPresenter = new AppPresenter(mContext, new UpdateConttract.View() {
                @Override
                public void updateProgress(int percent, String msg) {

                }

                @Override
                public void updateCompleted(final boolean isSuccess, final String msg) {
                    ShowInforUtil.send(mContext, getString(R.string.app), getString(R.string.update), isSuccess, msg);
                }
            });
        }
        mAppPresenter.update(mUpdateFilePath);
    }

    private void mcuStartUpdate() {
        if (mMcuPresenter == null) {
            mMcuPresenter = new McuPresenter(mContext, new UpdateConttract.View() {
                @Override
                public void updateProgress(int percent, String msg) {
                    updateProgressUi(percent);
                }

                @Override
                public void updateCompleted(boolean isSuccess, String msg) {
                    ShowInforUtil.send(mContext, getString(R.string.mcu), getString(R.string.update), isSuccess, msg);
                }
            });
        }
        mMcuPresenter.update(mUpdateFilePath);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxjavaUtils.dispose(mDisposable);
        mDisposable = null;
    }
}
