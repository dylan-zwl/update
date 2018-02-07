package com.tapc.update.ui.fragment;

import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.ui.presenter.OsPresenter;
import com.tapc.update.ui.presenter.UpdateConttract;
import com.tapc.update.ui.view.UpdateItem;
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
            mUpdateItemOS.setTitle(String.format(getString(R.string.os) + getString(R.string.version), osVersion));
        }
    }

    @OnClick(R.id.func_start_btn)
    void start() {
        RxjavaUtils.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                startUpdate();
                if (mOsPresenter == null) {
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
                mOsPresenter.update(Config.SAVEFILE_ORIGIN__PATH);
                e.onComplete();
            }
        }, new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception {

            }
        }, null);
    }

    @OnClick(R.id.restore_factory)
    void openRestoreFactory() {
        startActivity(new Intent(Settings.ACTION_PRIVACY_SETTINGS));
    }
}
