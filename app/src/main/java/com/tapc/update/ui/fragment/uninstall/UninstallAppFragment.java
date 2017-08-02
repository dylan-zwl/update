package com.tapc.update.ui.fragment.uninstall;

import android.content.pm.IPackageDeleteObserver;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.tapc.update.R;
import com.tapc.update.ui.adpater.BaseAppAdpater;
import com.tapc.update.ui.entity.AppInfoEntity;
import com.tapc.update.ui.fragment.BaseFragment;
import com.tapc.update.ui.view.CustomTextView;
import com.tapc.update.utils.AppUtil;
import com.tapc.update.utils.IntentUtil;
import com.tapc.update.utils.ShowInforUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UninstallAppFragment extends BaseFragment {
    @BindView(R.id.uninstall_app_lv)
    ListView mUninstallAppLv;
    @BindView(R.id.uninstall_all_chk)
    CheckBox mAllCheck;
    @BindView(R.id.show_system_app)
    CheckBox mShowSystemApp;

    private Thread mThread;
    private BaseAppAdpater mAdapter;
    private List<AppInfoEntity> mListApkInfor = new ArrayList<AppInfoEntity>();
    private List<AppInfoEntity> mAllLstAppInfo;
    private boolean isHasGetSystemApp = false;

    @Override
    public int getContentView() {
        return R.layout.fragment_uninstall_app;
    }

    @Override
    public void initView() {
        mHandler = new Handler();
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mAllLstAppInfo = AppUtil.getAllAppInfo(mContext, false);
                mListApkInfor = getShowListApp(mAllLstAppInfo, false);
                if (mListApkInfor == null || mListApkInfor.isEmpty()) {
                    return;
                }
                mAdapter = new BaseAppAdpater(mListApkInfor, new BaseAppAdpater.Listener() {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        return initItemView(position, convertView);
                    }
                });
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mUninstallAppLv != null) {
                            mUninstallAppLv.setAdapter(mAdapter);
                            notifyChanged();
                        }
                    }
                });
            }
        });
        mThread.start();

        mAllCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mListApkInfor != null) {
                    for (int index = 0; index < mListApkInfor.size(); index++) {
                        mListApkInfor.get(index).setChecked(isChecked);
                    }
                    notifyChanged();
                }
            }
        });

        mShowSystemApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mAllLstAppInfo != null) {
                    if (isChecked && isHasGetSystemApp == false) {
                        isHasGetSystemApp = true;
                        mAllLstAppInfo = AppUtil.getAllAppInfo(mContext, true);
                    }
                    mListApkInfor = getShowListApp(mAllLstAppInfo, isChecked);
                }
                if (mListApkInfor == null || mListApkInfor.isEmpty()) {
                    return;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged(mListApkInfor);
                    }
                });
            }
        });
    }

    private List<AppInfoEntity> getShowListApp(List<AppInfoEntity> list, boolean isShowSystemApp) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        List<AppInfoEntity> showList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSystemApp() == false) {
                showList.add(list.get(i));
            }
        }
        if (isShowSystemApp) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isSystemApp()) {
                    showList.add(list.get(i));
                }
            }
        }
        return showList;
    }

    private Runnable mUninstallAppRunnable = new Runnable() {
        public void run() {
            for (int index = 0; index < mListApkInfor.size(); index++) {
                uninstallApp(index, true);
            }
            notifyChanged();
            startUpdate();
        }
    };

    private void uninstallApp(final int index, boolean isNeedChecked) {
        final AppInfoEntity appInfoEntity = mListApkInfor.get(index);
        if (isNeedChecked && appInfoEntity.isChecked() == false) {
            return;
        }
        appInfoEntity.setInstallStatus("");
        incTask();

        final String pkgName = appInfoEntity.getPkgName();
        boolean result = AppUtil.unInstallApk(mContext, pkgName, new IPackageDeleteObserver.Stub() {
            @Override
            public void packageDeleted(String s, int i) throws RemoteException {
                boolean isSuccess;
                decTask();
                if (i == 1) {
                    mListApkInfor.remove(appInfoEntity);
                    if (mTaskNumber <= 0) {
                        notifyChanged();
                    }
                    isSuccess = true;
                } else {
                    isSuccess = false;
                }
                ShowInforUtil.send(mContext, appInfoEntity.getAppLabel(), getString(R.string.uninstall), isSuccess, "");
                stopUpdate();
            }
        });
        if (result == false) {
            ShowInforUtil.send(mContext, appInfoEntity.getAppLabel(), getString(R.string.uninstall), false, "");
            decTask();
            stopUpdate();
        }
    }

    @OnClick(R.id.uninstall_all_app_btn)
    void uninstallAllApp(View v) {
        new Thread(mUninstallAppRunnable).start();
    }

    class ViewHolder {
        @BindView(R.id.uninstall_rl)
        RelativeLayout mUninstallRl;
        @BindView(R.id.uninstall_chk)
        CheckBox checkBox;
        @BindView(R.id.uninstall_app_ic)
        ImageView icon;
        @BindView(R.id.uninstall_app_name)
        CustomTextView name;
        @BindView(R.id.open_app_btn)
        Button openApp;
        @BindView(R.id.uninstall_start_btn)
        Button start;
    }

    private void notifyChanged() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private View initItemView(final int position, View convertView) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_uninstall_app, null);
            viewHolder = new ViewHolder();
            ButterKnife.bind(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final AppInfoEntity item = mListApkInfor.get(position);
        viewHolder.checkBox.setChecked(item.isChecked());
        viewHolder.icon.setImageDrawable(item.getAppIcon());
        viewHolder.name.setText(item.getAppLabel());
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListApkInfor.get(position).setChecked(isChecked);
            }
        });
        viewHolder.mUninstallRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !mListApkInfor.get(position).isChecked();
                mListApkInfor.get(position).setChecked(isChecked);
                viewHolder.checkBox.setChecked(isChecked);
            }
        });
        viewHolder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uninstallApp(position, false);
                notifyChanged();
                startUpdate();
            }
        });
        viewHolder.openApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IntentUtil.startApp(mContext, mListApkInfor.get(position).getPkgName());
                } catch (Exception e) {
                    Log.d("uninstall", "start app : " + mListApkInfor.get(position).getAppLabel() + " failed");
                }
            }
        });
        return convertView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mThread != null) {
            mThread.interrupt();
        }
        mHandler.removeCallbacksAndMessages(null);
    }
}
