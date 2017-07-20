package com.tapc.update.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.tapc.update.R;
import com.tapc.update.ui.fragment.app.UpdateAppFragment;
import com.tapc.update.ui.fragment.install.InstallAppFragment;
import com.tapc.update.ui.fragment.os.UpdateOsFragment;
import com.tapc.update.ui.fragment.uninstall.UninstallAppFragment;
import com.tapc.update.ui.fragment.vacopy.VaCopyFragment;
import com.tapc.update.ui.view.FunctionItem;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity {
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
    private Map<Item, Fragment> mFragmentList;
    private Fragment mCurrentfragment;

//    private UpdateAppFragment mUpdateAppFragment;
//    private UpdateOsFragment mUpdateOsFragment;
//    private VaCopyFragment mVaCopyFragment;
//    private InstallAppFragment mInstallAppFragment;
//    private UninstallAppFragment mUninstallAppFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentList = new HashMap<>();
        mFragmentList.put(Item.APP, new UpdateAppFragment());
        mFragmentList.put(Item.OS, new UpdateOsFragment());
        mFragmentList.put(Item.VACOPY, new VaCopyFragment());
        mFragmentList.put(Item.INSTALL, new InstallAppFragment());
        mFragmentList.put(Item.UNINSTALL, new UninstallAppFragment());
        setCheckedFunc(Item.VACOPY);
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

    private void setCheckedFunc(final Item item) {
        mFuncItemApp.setChecked(false);
        mFuncItemOs.setChecked(false);
        mFuncItemVacopy.setChecked(false);
        mFuncItemInstall.setChecked(false);
        mFuncItemUninstall.setChecked(false);
        switch (item) {
            case APP:
                mFuncItemApp.setChecked(true);
//                replaceFragment(Fragment.instantiate(this, UpdateAppFragment.class.getName()));
                break;
            case OS:
                mFuncItemOs.setChecked(true);
//                replaceFragment(Fragment.instantiate(this, UpdateOsFragment.class.getName()));
                break;
            case VACOPY:
                mFuncItemVacopy.setChecked(true);
//                replaceFragment(Fragment.instantiate(this, VaCopyFragment.class.getName()));
                break;
            case INSTALL:
                mFuncItemInstall.setChecked(true);
//                replaceFragment(Fragment.instantiate(this, InstallAppFragment.class.getName()));
                break;
            case UNINSTALL:
                mFuncItemUninstall.setChecked(true);
//                replaceFragment(Fragment.instantiate(this, UninstallAppFragment.class.getName()));
                break;
        }
        replaceFragment(mFragmentList.get(item));
    }

    //    private void replaceFragment(Fragment fragment) {
//        FragmentTransaction ft = mFragmentManager.beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
//        ft.replace(R.id.fragment, fragment);
//        ft.commit();
//    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (mCurrentfragment != null) {
            ft.hide(mCurrentfragment);
        }
        Fragment showFragment = mFragmentManager.findFragmentByTag(fragment.getClass().getName());
        if (showFragment == null) {
            showFragment = fragment;
        }
        if (!showFragment.isAdded()) {
            ft.add(R.id.fragment, showFragment, fragment.getClass().getName());
        } else {
            ft.show(showFragment);
        }
        mCurrentfragment = showFragment;
        ft.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                return true;
            case KeyEvent.KEYCODE_BACK:
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
