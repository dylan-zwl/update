package com.tapc.update.ui.fragment.app;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.ui.fragment.BaseFragment;
import com.tapc.update.ui.presenter.AppPresenter;
import com.tapc.update.ui.presenter.McuPresenter;
import com.tapc.update.ui.presenter.UpdateConttract;
import com.tapc.update.ui.presenter.UpdateInfor;
import com.tapc.update.ui.view.UpdateItem;
import com.tapc.update.utils.AppUtil;
import com.tapc.update.utils.FileUtil;
import com.tapc.update.utils.RxjavaUtils;
import com.tapc.update.utils.ShowInforUtil;

import java.io.File;
import java.io.FilenameFilter;

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

    @Override
    public int getContentView() {
        return R.layout.fragment_app;
    }

    @Override
    public void initView() {
        mTitle.setText(getString(R.string.func_app));
        mStartUpdate.setText(getString(R.string.a_key_update));

        initVersionShow();
        initPresenter();
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
        mUpdateItemApp.setTitle(String.format(mContext.getString(R.string.update_app_title), appVersion));
        mUpdateItemMcu.setTitle(String.format(mContext.getString(R.string.update_mcu_title), mcuVersion));
    }

    private void initPresenter() {

    }

    @OnClick(R.id.func_start_btn)
    void start() {
        startUpdateThead(mode.ALL);
    }

    @OnClick(R.id.update_app)
    void startUpdateApp() {
        startUpdateThead(mode.ONLY_APP);
    }

    @OnClick(R.id.update_mcu)
    void startUpdateMcu() {
        startUpdateThead(mode.ONLY_MCU);
    }

    private enum mode {
        ALL,
        ONLY_APP,
        ONLY_MCU
    }

    private void startUpdateThead(final mode mode) {
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
            isCopySuccessed = copyUpdateFile(originFile, mUpdateFilePath);
        }
    }

    private void appStartUpdate() {
        String appFileName = FileUtil.getFilename(mUpdateFilePath, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                if (name.startsWith("app") && name.endsWith(".apk")) {
                    return true;
                }
                return false;
            }
        });
        final UpdateInfor updateInfor = new UpdateInfor();
        updateInfor.setFileType(UpdateInfor.FileType.APP);
        updateInfor.setUpdateType(UpdateInfor.UpdateType.LOCAL);
        updateInfor.setFileName(appFileName);
        updateInfor.setPath(mUpdateFilePath);
        if (mAppPresenter == null) {
            mAppPresenter = new AppPresenter(mContext, new UpdateConttract.View() {
                @Override
                public void updateProgress(int percent, String msg) {

                }

                @Override
                public void updateCompleted(final boolean isSuccess, final String msg) {
                    ShowInforUtil.send(mContext, "APP", getString(R.string.update), isSuccess, msg);
                }
            });
        }
        mAppPresenter.update(updateInfor);
    }

    private void mcuStartUpdate() {
        String mcuFileName = FileUtil.getFilename(mUpdateFilePath, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                if (name.startsWith("rom") && name.endsWith(".bin")) {
                    return true;
                }
                return false;
            }
        });
        final UpdateInfor updateInfor = new UpdateInfor();
        updateInfor.setFileType(UpdateInfor.FileType.MCU);
        updateInfor.setUpdateType(UpdateInfor.UpdateType.LOCAL);
        updateInfor.setPath(mUpdateFilePath);
        updateInfor.setFileName(mcuFileName);

        if (mMcuPresenter == null) {
            mMcuPresenter = new McuPresenter(mContext, new UpdateConttract.View() {
                @Override
                public void updateProgress(int percent, String msg) {
                    updateProgressUi(percent);
                }

                @Override
                public void updateCompleted(boolean isSuccess, String msg) {
                    ShowInforUtil.send(mContext, "MCU", getString(R.string.update), isSuccess, msg);
                }
            });
        }
        mMcuPresenter.update(updateInfor);
    }

    private boolean copyUpdateFile(String originFile, String savePath) {
        try {
            if (TextUtils.isEmpty(originFile) || !new File(originFile).exists()) {
                return false;
            }
            File saveFile = new File(savePath);
            if (saveFile.exists()) {
                FileUtil.RecursionDeleteFile(saveFile);
            } else {
                saveFile.mkdirs();
            }

            FileUtil.upZipFile(originFile, saveFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            Log.d(TAG, "copy file : " + e.getMessage());
        }
        return false;
    }

    @Override
    public void stopUpdate() {
        super.stopUpdate();
        initVersionShow();
    }
}
