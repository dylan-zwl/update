package com.tapc.update.ui.fragment;

import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.ui.presenter.AppPresenter;
import com.tapc.update.ui.presenter.CopyFilePresenter;
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

    private static final String TAG = UpdateAppFragment.class.getSimpleName();
    private String mUpdateFilePath;
    boolean isCopySuccessed = false;

    private AppPresenter mAppPresenter;
    private McuPresenter mMcuPresenter;
    private CopyFilePresenter mCopyFilePresenter;

    @Override
    public int getContentView() {
        return R.layout.fragment_app;
    }

    @Override
    public void initView() {
        mTitle.setText(getString(R.string.func_app));
        mStartUpdate.setText(getString(R.string.a_key_update));
        mUpdateItemApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdateApp();
            }
        });
        mUpdateItemMcu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdateMcu();
            }
        });

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
    }

    @OnClick(R.id.func_start_btn)
    void start() {
        startUpdateThead(Mode.ALL);
    }

    @OnClick(R.id.update_app)
    void startUpdateApp() {
        startUpdateThead(Mode.ONLY_APP);
    }

    @OnClick(R.id.update_mcu)
    void startUpdateMcu() {
        startUpdateThead(Mode.ONLY_MCU);
    }

    private enum Mode {
        ALL,
        ONLY_APP,
        ONLY_MCU
    }

    private void startUpdateThead(final Mode mode) {
        RxjavaUtils.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("start");
                startCopyUpdateFile();
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
                }
            }
        }, null);
    }

    /**
     * 功能描述 : 复制升级文件
     */
    private void startCopyUpdateFile() {
        if (isCopySuccessed == false) {
            String originFile = Config.MOUNTED_PATH + Config.SAVEFILE_PATH + "/" + Config.UPDATE_APP_NAME + ".zip";
            mUpdateFilePath = Config.IN_SD_FILE_PATH + "/" + Config.SAVEFILE_PATH + "/" + Config.UPDATE_APP_NAME;
            if (mCopyFilePresenter == null) {
                mCopyFilePresenter = new CopyFilePresenter();
            }
            isCopySuccessed = mCopyFilePresenter.copyUpdateFile(originFile, mUpdateFilePath);
        }
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
    public void stopUpdate() {
        super.stopUpdate();
        initVersionShow();
    }
}