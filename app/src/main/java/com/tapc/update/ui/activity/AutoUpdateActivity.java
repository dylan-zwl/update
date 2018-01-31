package com.tapc.update.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.application.TapcApp;
import com.tapc.update.ui.presenter.AppPresenter;
import com.tapc.update.ui.presenter.McuPresenter;
import com.tapc.update.ui.presenter.OsPresenter;
import com.tapc.update.ui.presenter.UpdateConttract;
import com.tapc.update.ui.presenter.UpdateInfor;
import com.tapc.update.ui.view.CustomTextView;
import com.tapc.update.ui.widget.UpdateProgress;
import com.tapc.update.utils.FileUtil;
import com.tapc.update.utils.ShowInforUtil;

import java.io.File;
import java.io.FilenameFilter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/19.
 */

public class AutoUpdateActivity extends Activity implements UpdateProgress.Listener {
    @BindView(R.id.auto_update_infor)
    CustomTextView mInfor;
    @BindView(R.id.auto_update_progress)
    UpdateProgress mProgress;
    @BindView(R.id.auto_update_exit)
    LinearLayout mLinearLayout;

    private static String TAG;
    private Context mContext;
    private Handler mHandler = new Handler();
    private String mUpdateFilePath;
    boolean isCopySuccessed = false;
    boolean isUpdateSuccess = false;
    private StringBuilder stringBuilder;
    private int mTaskNumber;

    private AppPresenter mAppPresenter;
    private McuPresenter mMcuPresenter;
    private OsPresenter mOsPresenter;

    private UpdateInfor mUpdateInfor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_update);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        TAG = getClass().getName();
        mContext = this;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TapcApp.getInstance().setMenuBarVisibility(false);
            }
        }, 1000);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mUpdateInfor = (UpdateInfor) bundle.get("update_infor");
            }
        }

        if (mUpdateInfor == null) {
            String originFile = Config.MOUNTED_PATH + Config.SAVEFILE_PATH + "/" + Config.UPDATE_APP_NAME + ".zip";
            if (new File(originFile).exists()) {
                mUpdateInfor = new UpdateInfor();
                mUpdateInfor.setFileType(UpdateInfor.FileType.APP);
                mUpdateInfor.setUpdateType(UpdateInfor.UpdateType.LOCAL);
            } else {
                String osFileName = Config.UPDATE_OS_NAME;
                String osPath = Config.MOUNTED_PATH + Config.SAVEFILE_PATH;
                if (new File(osPath, osFileName).exists()) {
                    mUpdateInfor = new UpdateInfor();
                    mUpdateInfor.setFileType(UpdateInfor.FileType.OS);
                    mUpdateInfor.setUpdateType(UpdateInfor.UpdateType.LOCAL);
                    mUpdateInfor.setPath(Config.MOUNTED_PATH + Config.SAVEFILE_PATH + "/");
                    mUpdateInfor.setFileName(osFileName);
                }
            }
        }
        if (mUpdateInfor == null) {
            addInforShow(getString(R.string.no_file));
            stopUpdate(false);
            return;
        }

        mProgress.setListener(this);
        switch (mUpdateInfor.getFileType()) {
            case APP:
//                mAppPresenter = new UpdatePresenter(mContext, new UpdateConttract.View() {
//                    @Override
//                    public void updateProgress(int percent, String msg) {
//
//                    }
//
//                    @Override
//                    public void updateCompleted(final boolean isSuccess, final String msg) {
//                        String configPath = mUpdateFilePath + "/" + Config.APP_CONFIG_NAME;
//                        if (!TextUtils.isEmpty(configPath) && new File(configPath).exists() && isSuccess) {
//                            String appConfigPath = Config.APP_CONFIG_PATH + "/" + Config.APP_CONFIG_NAME;
//                            FileUtil.copyFile(configPath, appConfigPath, new FileUtil.ProgressCallback() {
//                                @Override
//                                public void onProgress(int progress) {
//
//                                }
//
//                                @Override
//                                public void onCompeleted(boolean isSuccessd, String msg) {
//                                    boolean isConfigCopyResult = isSuccessd;
//                                    String showMsg = msg;
//                                    if (!isSuccessd) {
//                                        showMsg = msg + "," + getString(R.string.app_config) + getString(R
//                                                .string.copy) + getString(R.string.failed);
//                                    }
//                                    String text = ShowInforUtil.getInforText(mContext, "APP", getString(R
//                                            .string.update), isSuccess && isConfigCopyResult, showMsg);
//                                    addInforShow(text);
//                                    decTask();
//                                    stopUpdate(isSuccess && isConfigCopyResult);
//                                }
//                            });
//                        } else {
//                            String text = ShowInforUtil.getInforText(mContext, "APP", getString(R.string.update),
//                                    isSuccess, msg);
//                            addInforShow(text);
//                            decTask();
//                            stopUpdate(isSuccess);
//                        }
//                    }
//                });
                mMcuPresenter = new McuPresenter(mContext, new UpdateConttract.View() {
                    @Override
                    public void updateProgress(int percent, String msg) {
                        updateProgressUi(percent);
                    }

                    @Override
                    public void updateCompleted(boolean isSuccess, String msg) {
                        String text = ShowInforUtil.getInforText(mContext, "MCU", getString(R.string.update), isSuccess,
                                msg);
                        addInforShow(text);
                        decTask();
                        stopUpdate(isSuccess);
                    }
                });
                break;
            case OS:
                mOsPresenter = new OsPresenter(mContext, new UpdateConttract.View() {
                    @Override
                    public void updateProgress(int percent, String msg) {
                        updateProgressUi(percent);
                    }

                    @Override
                    public void updateCompleted(boolean isSuccess, String msg) {
                        String text = ShowInforUtil.getInforText(mContext, "OS", getString(R.string.copy), isSuccess,
                                msg);
                        addInforShow(text);
                        stopUpdate(isSuccess);
                    }
                });
                break;
        }

//        mDownloadPresenter = new DownloadPresenter(mContext, new UpdateConttract.View() {
//            @Override
//            public void updateProgress(int percent, String msg) {
//                updateProgressUi(percent);
//            }
//
//            @Override
//            public void updateCompleted(boolean isSuccess, String msg) {
//                String text = ShowInforUtil.getInforText(mContext, getString(R.string.download), "", isSuccess, msg);
//                addInforShow(text);
//                startUpdateThead();
//            }
//        });

        startUpdate();
    }

    private void startUpdate() {
        if (mUpdateInfor == null) {
            return;
        }
        mLinearLayout.setVisibility(View.GONE);
        switch (mUpdateInfor.getUpdateType()) {
            case NETWORK:
                downloadUpdateFile(mUpdateInfor);
                break;
            case LOCAL:
                startUpdateThead();
                break;
        }
    }

    private void startUpdateThead() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (mUpdateInfor.getFileType()) {
                    case APP:
                        startCopyUpdateFile();
                        appStartUpdate();
                        mcuStartUpdate();
                        break;
                    case OS:
                        osStartUpdate();
                        break;
                }
            }
        }).start();
    }

    private void downloadUpdateFile(final UpdateInfor updateInfor) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                mDownloadPresenter.update(updateInfor);
            }
        }).start();
    }

    private void startCopyUpdateFile() {
        if (isCopySuccessed == false) {
            String originFile = "";
            switch (mUpdateInfor.getUpdateType()) {
                case NETWORK:
                    originFile = mUpdateInfor.getPath() + "/" + mUpdateInfor.getFileName() + ".zip";
                    break;
                case LOCAL:
                    originFile = Config.MOUNTED_PATH + Config.SAVEFILE_PATH + "/" + Config.UPDATE_APP_NAME + ".zip";
                    break;
            }
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

    private void osStartUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mOsPresenter.update(mUpdateInfor);
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

    private void addInforShow(String msg) {
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder();
        }
        stringBuilder.append(msg + "\n");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mInfor.setText(stringBuilder.toString());
            }
        });
    }

    public void updateProgressUi(final int progress) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgress.setProgress(progress);
            }
        });
    }

    public void stopUpdate(boolean isSuccess) {
        if (isUpdateSuccess == true) {
            isUpdateSuccess = isSuccess;
        }
        if (mTaskNumber <= 0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mProgress.setUpdateStatus(isUpdateSuccess);
                    mLinearLayout.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void decTask() {
        mTaskNumber--;
        if (mTaskNumber < 0) {
            mTaskNumber = 0;
        }
    }

    public void incTask() {
        mTaskNumber++;
    }

    @OnClick(R.id.auto_update_exit)
    void exit() {
        finish();
    }

    @Override
    public void updateAgainOnClick() {
        startUpdate();
    }
}
