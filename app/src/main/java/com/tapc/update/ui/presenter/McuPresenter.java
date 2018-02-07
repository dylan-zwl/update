package com.tapc.update.ui.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.tapc.platform.model.device.controller.IOUpdateController;
import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.update.R;
import com.tapc.update.utils.FileUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/3/17.
 */

public class McuPresenter implements UpdateConttract.UpdatePresenter {
    private Context mContext;
    private UpdateConttract.View mView;
    private MachineController mController;

    public McuPresenter(Context context, UpdateConttract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void update(String filePath) {
        String mcuFileName = FileUtil.getFilename(filePath, new FilenameFilter() {
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
        updateInfor.setPath(filePath);
        updateInfor.setFileName(mcuFileName);

        String fileName = updateInfor.getFileName();
        if (!TextUtils.isEmpty(fileName)) {
            final File file = new File(updateInfor.getPath(), fileName);
            if (file != null && file.exists()) {
                //开始升级
                mView.updateProgress(0, "");
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                mController = MachineController.getInstance();
                mController.updateMCU(file.getAbsolutePath(), new IOUpdateController.IOUpdateListener() {
                    @Override
                    public void onProgress(int process, String msg) {
                        mView.updateProgress(process, msg);
                    }

                    @Override
                    public void successful(String msg) {
                        mView.updateCompleted(true, msg);
                        countDownLatch.countDown();
                    }

                    @Override
                    public void failed(String msg) {
                        mView.updateCompleted(false, msg);
                        countDownLatch.countDown();
                    }
                });
                try {
                    boolean result = countDownLatch.await(180, TimeUnit.SECONDS);
                    if (!result) {
                        mView.updateCompleted(false, mContext.getString(R.string.time_out));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        mView.updateCompleted(false, mContext.getString(R.string.no_file));
    }
}
