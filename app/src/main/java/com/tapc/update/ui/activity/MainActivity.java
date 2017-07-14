package com.tapc.update.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.FrameLayout;

import com.tapc.update.R;
import com.tapc.update.ui.fragment.app.UpdateAppFragment;
import com.tapc.update.ui.fragment.install.InstallAppFragment;
import com.tapc.update.ui.fragment.os.UpdateOsFragment;
import com.tapc.update.ui.fragment.uninstall.UninstallAppFragment;
import com.tapc.update.ui.fragment.vacopy.VaCopyFragment;
import com.tapc.update.ui.update.AppPresenter;
import com.tapc.update.ui.update.McuPresenter;
import com.tapc.update.ui.update.UpdateConttract;
import com.tapc.update.ui.view.FunctionItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity implements UpdateConttract.View {
    @BindView(R.id.func_app)
    FunctionItem mFuncItemApp;
    @BindView(R.id.func_os)
    FunctionItem mFuncItemOs;
    @BindView(R.id.func_vacopy)
    FunctionItem mFuncItemVacopy;
    @BindView(R.id.func_install)
    FunctionItem mFuncItemInstall;
    @BindView(R.id.func_uninstall)
    FunctionItem mFuncItemUninstall;
    @BindView(R.id.fragment)
    FrameLayout mFragment;

    private Context mContext;
    protected FragmentManager mFragmentManager;
    AppPresenter appPresenter;
    McuPresenter mcuPresenter;
    private String mSavePath = "tapc";
    private String updateFile = "update_file";

    private UpdateAppFragment mUpdateAppFragment;
    private UpdateOsFragment mUpdateOsFragment;
    private VaCopyFragment mVaCopyFragment;
    private InstallAppFragment mInstallAppFragment;
    private UninstallAppFragment mUninstallAppFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mFragmentManager = getSupportFragmentManager();
        setCheckedFunc(Item.INSTALL);
    }

    private enum Item {
        APP,
        OS,
        VACOPY,
        INSTALL,
        UNINSTALL
    }

    @OnClick(R.id.func_app)
    void app() {
        setCheckedFunc(Item.APP);
    }

    @OnClick(R.id.func_os)
    void os() {
        setCheckedFunc(Item.OS);
    }

    @OnClick(R.id.func_vacopy)
    void vacopy() {
        setCheckedFunc(Item.VACOPY);
    }

    @OnClick(R.id.func_install)
    void install() {
        setCheckedFunc(Item.INSTALL);
    }

    @OnClick(R.id.func_uninstall)
    void uninstall() {
        setCheckedFunc(Item.UNINSTALL);
    }

    private void setCheckedFunc(Item item) {
        mFuncItemApp.setChecked(false);
        mFuncItemOs.setChecked(false);
        mFuncItemVacopy.setChecked(false);
        mFuncItemInstall.setChecked(false);
        mFuncItemUninstall.setChecked(false);
        switch (item) {
            case APP:
                if (mUpdateAppFragment == null) {
                    mUpdateAppFragment = new UpdateAppFragment();
                }
                mFuncItemApp.setChecked(true);
                replaceFragment(mUpdateAppFragment);
                break;
            case OS:
                if (mUpdateOsFragment == null) {
                    mUpdateOsFragment = new UpdateOsFragment();
                }
                mFuncItemOs.setChecked(true);
                replaceFragment(mUpdateOsFragment);
                break;
            case VACOPY:
                if (mVaCopyFragment == null) {
                    mVaCopyFragment = new VaCopyFragment();
                }
                mFuncItemVacopy.setChecked(true);
                replaceFragment(mVaCopyFragment);
                break;
            case INSTALL:
                if (mInstallAppFragment == null) {
                    mInstallAppFragment = new InstallAppFragment();
                }
                mFuncItemInstall.setChecked(true);
                replaceFragment(mInstallAppFragment);
                break;
            case UNINSTALL:
                if (mUninstallAppFragment == null) {
                    mUninstallAppFragment = new UninstallAppFragment();
                }
                mFuncItemUninstall.setChecked(true);
                replaceFragment(mUninstallAppFragment);
                break;
        }
    }


    @Override
    public void updateProgress(int percent, String msg) {
        Log.d("Progress" + percent, "" + msg);
    }

    @Override
    public void updateCompleted(boolean isSuccess, String msg) {
        Log.d("Completed", "" + isSuccess + "  :  " + msg);
    }

    @Override
    public void reboot() {

    }


    public void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        ft.replace(R.id.fragment, fragment);
        ft.commit();
    }
}
