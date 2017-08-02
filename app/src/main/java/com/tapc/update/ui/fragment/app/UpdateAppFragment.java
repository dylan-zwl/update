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
import com.tapc.update.ui.update.AppPresenter;
import com.tapc.update.ui.update.McuPresenter;
import com.tapc.update.ui.update.UpdateConttract;
import com.tapc.update.ui.update.UpdateInfor;
import com.tapc.update.ui.view.UpdateItem;
import com.tapc.update.utils.AppUtil;
import com.tapc.update.utils.FileUtil;
import com.tapc.update.utils.ShowInforUtil;

import java.io.File;
import java.io.FilenameFilter;

import butterknife.BindView;
import butterknife.OnClick;

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

    protected static String TAG;
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
        TAG = getClass().getName();
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
        mAppPresenter = new AppPresenter(mContext, new UpdateConttract.View() {
            @Override
            public void updateProgress(int percent, String msg) {

            }

            @Override
            public void updateCompleted(final boolean isSuccess, final String msg) {
                String configPath = mUpdateFilePath + "/" + Config.APP_CONFIG_NAME;
                if (!TextUtils.isEmpty(configPath) && new File(configPath).exists() && isSuccess) {
                    String appConfigPath = Config.APP_CONFIG_PATH + "/" + Config.APP_CONFIG_NAME;
                    FileUtil.copyFile(configPath, appConfigPath, new FileUtil.ProgressCallback() {
                                @Override
                                public void onProgress(int progress) {

                                }

                                @Override
                                public void finish(int error) {
                                    decTask();
                                    stopUpdate();
                                    boolean isConfigCopyResult = false;
                                    String showMsg = msg;
                                    if (error == 0) {
                                        isConfigCopyResult = true;
                                    } else {
                                        showMsg = msg + "," + getString(R.string.app_config) + getString(R.string
                                                .copy) + getString(R.string.failed);
                                    }
                                    ShowInforUtil.send(mContext, "APP", getString(R.string.update), isSuccess &&
                                            isConfigCopyResult, showMsg);
                                }
                            }
                    );
                } else {
                    decTask();
                    stopUpdate();
                    ShowInforUtil.send(mContext, "APP", getString(R.string.update), isSuccess, msg);
                }
            }
        });

        mMcuPresenter = new McuPresenter(mContext, new UpdateConttract.View() {
            @Override
            public void updateProgress(int percent, String msg) {
                decTask();
                updateProgressUi(percent);
            }

            @Override
            public void updateCompleted(boolean isSuccess, String msg) {
                decTask();
                stopUpdate();
                ShowInforUtil.send(mContext, "MCU", getString(R.string.update), isSuccess, msg);
            }
        });
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
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                startUpdate();
            }
        }).start();
    }

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
                if (name.startsWith("APP") && name.endsWith(".apk")) {
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
        incTask();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAppPresenter.update(updateInfor);
            }
        }).start();
    }

    private void mcuStartUpdate() {
        String mcuFileName = FileUtil.getFilename(mUpdateFilePath, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith("ROM") && name.endsWith(".bin")) {
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
        incTask();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMcuPresenter.update(updateInfor);
            }
        }).start();
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
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                initVersionShow();
            }
        });
    }
}
