package com.tapc.update.ui.fragment.install;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.ui.adpater.BaseAppAdpater;
import com.tapc.update.ui.entity.AppInfoEntity;
import com.tapc.update.ui.view.CustomTextView;
import com.tapc.update.utils.AppUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InstallAppFragment extends Fragment {
    @BindView(R.id.install_app_lv)
    ListView mInstallAppLv;
    @BindView(R.id.install_all_chk)
    CheckBox mAllCheck;

    private Context mContext;
    private Handler mHandler;
    private BaseAppAdpater mAdapter;
    private List<String> mListFilePath = new ArrayList<String>();
    private List<AppInfoEntity> mListApkInfor = new ArrayList<AppInfoEntity>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_install_app, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initView() {
        mHandler = new Handler();
        getFiles(Config.INSTALL_APP_FILE_PATH, ".apk", true);
        if (mListFilePath != null && mListFilePath.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getAppList();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter = new BaseAppAdpater(mListApkInfor, new BaseAppAdpater.Listener() {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    return initItemView(position, convertView);
                                }
                            });
                            mInstallAppLv.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();
        }
        mAllCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int index = 0; index < mListApkInfor.size(); index++) {
                    mListApkInfor.get(index).setChecked(isChecked);
                    notifyChanged();
                }
            }
        });
    }

    class ViewHolder {
        @BindView(R.id.install_chk)
        CheckBox checkBox;
        @BindView(R.id.install_app_ic)
        ImageView icon;
        @BindView(R.id.install_app_name)
        CustomTextView name;
        @BindView(R.id.install_app_status)
        CustomTextView installStatus;
        @BindView(R.id.install_start_btn)
        Button start;
    }

    private View initItemView(final int position, View convertView) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_install_app, null);
            viewHolder = new ViewHolder();
            ButterKnife.bind(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final AppInfoEntity item = mListApkInfor.get(position);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListApkInfor.get(position).setChecked(isChecked);
            }
        });
        viewHolder.checkBox.setChecked(item.isChecked());
        viewHolder.icon.setImageDrawable(item.getAppIcon());
        viewHolder.name.setText(item.getAppLabel());
        viewHolder.installStatus.setText(item.getInstallStatus());
        viewHolder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installApp(position, false);
            }
        });
        return convertView;
    }

    // 搜索目录，扩展名，是否进入子文件夹
    private void getFiles(String Path, String Extension, boolean IsIterative) {
        File[] files = new File(Path).listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isFile()) {
                    if (f.getName().contains(Extension)) {
                        mListFilePath.add(f.getPath());
                    }
                }
            }
        }
    }

    private void getAppList() {
        for (String apkPath : mListFilePath) {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                AppInfoEntity appEntity = new AppInfoEntity();
                // 得到安装包名称
                appInfo.sourceDir = apkPath;
                appInfo.publicSourceDir = apkPath;
                appEntity.setInstallStatus("");
                appEntity.setPath(apkPath);
                try {
                    appEntity.setAppLabel(appInfo.loadLabel(pm).toString());
                    appEntity.setAppIcon(appInfo.loadIcon(pm));
                } catch (OutOfMemoryError e) {
                    Log.e("ApkIconLoader", e.toString());
                }
                Log.d("app file", "" + appEntity.getAppLabel());
                mListApkInfor.add(appEntity);
            }
        }
    }

    private Runnable mInstallAppRunnable = new Runnable() {
        public void run() {
            for (int index = 0; index < mListApkInfor.size(); index++) {
                installApp(index, true);
            }
        }
    };

    private void installApp(int index, boolean isNeedChecked) {
        AppInfoEntity appInfoEntity = mListApkInfor.get(index);
        if (isNeedChecked && appInfoEntity.isChecked() == false) {
            return;
        }
        appInfoEntity.setInstallStatus("");
        notifyChanged();

        final int listIndex = index;
        String path = appInfoEntity.getPath();
        boolean result = AppUtil.installApk(mContext, new File(path), new IPackageInstallObserver.Stub() {
            @Override
            public void packageInstalled(String s, int i) throws RemoteException {
                if (i == 0) {
                    mListApkInfor.get(listIndex).setInstallStatus(getResources().getString(R.string
                            .app_install_success));
                } else {
                    mListApkInfor.get(listIndex).setInstallStatus(getResources().getString(R.string
                            .app_install_fail));
                }
                notifyChanged();
            }
        });
        if (result == false) {
            mListApkInfor.get(listIndex).setInstallStatus(getResources().getString(R.string.app_install_fail));
        }
    }

    private void notifyChanged() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick(R.id.install_all_app_btn)
    void installAllApp(View v) {
        new Thread(mInstallAppRunnable).start();
    }
}
