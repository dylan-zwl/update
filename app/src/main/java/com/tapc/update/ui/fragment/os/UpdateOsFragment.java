package com.tapc.update.ui.fragment.os;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.ui.fragment.BaseFragment;
import com.tapc.update.ui.presenter.OsPresenter;
import com.tapc.update.ui.presenter.UpdateConttract;
import com.tapc.update.ui.presenter.UpdateInfor;
import com.tapc.update.ui.view.UpdateItem;
import com.tapc.update.utils.ShowInforUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/10.
 */

public class UpdateOsFragment extends BaseFragment {
    @BindView(R.id.func_name_tx)
    TextView mTitle;
    @BindView(R.id.func_start_btn)
    Button mStartUpdate;

    @BindView(R.id.update_os)
    UpdateItem mUpdateItemOS;

    private OsPresenter mOsPresenter;

    @Override
    public int getContentView() {
        return R.layout.fragment_os;
    }

    @Override
    public void initView() {
        mTitle.setText(getString(R.string.func_os));
        mStartUpdate.setText(getString(R.string.a_key_update));

        String osVersion = android.os.Build.DISPLAY;
        if (!TextUtils.isEmpty(osVersion)) {
            mUpdateItemOS.setTitle(String.format(mContext.getString(R.string.update_os_title), osVersion));
        }

        mOsPresenter = new OsPresenter(mContext, new UpdateConttract.View() {
            @Override
            public void updateProgress(int percent, String msg) {
                updateProgressUi(percent);
            }

            @Override
            public void updateCompleted(boolean isSuccess, String msg) {
                ShowInforUtil.send(mContext, "OS", getString(R.string.copy), isSuccess, msg);
                stopUpdate();
            }
        });
    }

    @OnClick(R.id.func_start_btn)
    void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startUpdate();
                String osPath = Config.MOUNTED_PATH + Config.SAVEFILE_PATH;
                UpdateInfor updateInfor = new UpdateInfor();
                updateInfor.setFileType(UpdateInfor.FileType.OS);
                updateInfor.setUpdateType(UpdateInfor.UpdateType.LOCAL);
                updateInfor.setPath(osPath);
                updateInfor.setFileName(Config.UPDATE_OS_NAME);
                mOsPresenter.update(updateInfor);
            }
        }).start();
    }

}
