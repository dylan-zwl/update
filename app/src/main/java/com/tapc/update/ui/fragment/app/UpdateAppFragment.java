package com.tapc.update.ui.fragment.app;

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

import java.io.File;
import java.io.FilenameFilter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/10.
 */

public class UpdateAppFragment extends BaseFragment implements UpdateConttract.View {
    @BindView(R.id.func_name_tx)
    TextView mTitle;
    @BindView(R.id.func_start_btn)
    Button mStartUpdate;

    @BindView(R.id.update_app)
    UpdateItem mUpdateItemApp;
    @BindView(R.id.update_mcu)
    UpdateItem mUpdateItemMcu;

    private String mSavePath = "tapc";
    private String updateFile = "update_app";
    private String updateOriginPath = "";
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

        String appVersion = AppUtil.getVersionName(mContext, mContext.getPackageName());
        String mcuVersion = MachineController.getInstance().getCtlVersionValue();
        if (!TextUtils.isEmpty(appVersion)) {
            mUpdateItemApp.setTitle(String.format(mContext.getString(R.string.update_app_title), appVersion));
        }
        if (!TextUtils.isEmpty(mcuVersion)) {
            mUpdateItemMcu.setTitle(String.format(mContext.getString(R.string.update_app_title), mcuVersion));
        }
    }

    @OnClick(R.id.func_start_btn)
    void start() {
        startCopyUpdateFile();
        appStartUpdate();
        mcuStartUpdate();
    }

    @OnClick(R.id.update_app)
    void startUpdateApp() {
        startCopyUpdateFile();
        appStartUpdate();
    }

    @OnClick(R.id.update_mcu)
    void startUpdateMcu() {
        startCopyUpdateFile();
        mcuStartUpdate();
    }

    private void startCopyUpdateFile() {
        if (isCopySuccessed == false) {
            isCopySuccessed = copyUpdateFile();
        }
    }

    private void appStartUpdate() {
        String appFileName = getUpdateFile(mUpdateFilePath, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith("APP") && name.endsWith(".apk")) {
                    return true;
                }
                return false;
            }
        });
        if (!TextUtils.isEmpty(appFileName)) {
            mAppPresenter = new AppPresenter(mContext, new UpdateConttract.View() {
                @Override
                public void updateProgress(int percent, String msg) {

                }

                @Override
                public void updateCompleted(boolean isSuccess, String msg) {
                    Log.d(TAG, "app update : " + isSuccess);
                }

                @Override
                public void reboot() {

                }
            });
            final UpdateInfor updateInfor = new UpdateInfor();
            updateInfor.setFileType(UpdateInfor.FileType.APP);
            updateInfor.setUpdateType(UpdateInfor.UpdateType.LOCAL);
            updateInfor.setFileName(appFileName);
            updateInfor.setPath(mUpdateFilePath);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mAppPresenter.update(updateInfor);
                }
            }).start();
        } else {
            Log.d(TAG, "没有升级文件");
        }
    }


    private void mcuStartUpdate() {
        String mcuFileName = getUpdateFile(mUpdateFilePath, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith("ROM") && name.endsWith(".bin")) {
                    return true;
                }
                return false;
            }
        });
        if (!TextUtils.isEmpty(mcuFileName)) {
            mMcuPresenter = new McuPresenter(mContext, new UpdateConttract.View() {
                @Override
                public void updateProgress(int percent, String msg) {

                }

                @Override
                public void updateCompleted(boolean isSuccess, String msg) {
                    Log.d(TAG, "mcu update : " + isSuccess);
                }

                @Override
                public void reboot() {

                }
            });
            final UpdateInfor updateInfor = new UpdateInfor();
            updateInfor.setFileType(UpdateInfor.FileType.MCU);
            updateInfor.setUpdateType(UpdateInfor.UpdateType.LOCAL);
            updateInfor.setPath(mUpdateFilePath);
            updateInfor.setFileName(mcuFileName);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mMcuPresenter.update(updateInfor);
                }
            }).start();
        } else {
            Log.d(TAG, "没有升级文件");
        }
    }

    private String getUpdateFile(String path, FilenameFilter filter) {
        File files = new File(path);
        String[] list = files.list(filter);
        if (list != null && list.length > 0) {
            return list[0];
        }
        return null;
    }

    private boolean copyUpdateFile() {
        try {
            String originFile = Config.MOUNTED_PATH + mSavePath + "/" + updateFile + ".zip";
            if (!new File(originFile).exists()) {
                return false;
            }

            mUpdateFilePath = Config.IN_SD_FILE_PATH + "/" + mSavePath + "/" + updateFile;
            File saveFile = new File(mUpdateFilePath);
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
    public void updateProgress(int percent, String msg) {

    }

    @Override
    public void updateCompleted(boolean isSuccess, String msg) {

    }

    @Override
    public void reboot() {

    }
}
