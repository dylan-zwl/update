package com.tapc.update.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.application.TapcApp;
import com.tapc.update.service.MenuServie;
import com.tapc.update.ui.fragment.InstallAppFragment;
import com.tapc.update.ui.fragment.UninstallAppFragment;
import com.tapc.update.ui.fragment.UpdateAppFragment;
import com.tapc.update.ui.fragment.UpdateOsFragment;
import com.tapc.update.ui.fragment.VaCopyFragment;
import com.tapc.update.ui.view.FunctionItem;
import com.tapc.update.utils.XmlUtils;

import java.io.IOException;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tapc.update.application.Config.isCoverInstall;

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
    @BindView(R.id.func_setting)
    FunctionItem mFuncItemSetting;
    @BindView(R.id.fragment)
    FrameLayout mFragment;

    protected FragmentManager mFragmentManager;
    private Fragment mCurrentfragment;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initView();

        try {
            Map<String, String> configMap = XmlUtils.getXmlMap(getResources().getAssets().open("update_config.xml"));
            if (configMap != null) {
                Log.d("update_config.xml", configMap.toString());
            }
            String updateAppMode = configMap.get("update_app_mode");
            if (!TextUtils.isEmpty(updateAppMode) && updateAppMode.equals(1)) {
                isCoverInstall = true;
            }

            String vaOrginPath = configMap.get("va_origin_path");
            if (!TextUtils.isEmpty(vaOrginPath)) {
                Config.VA_ORIGIN_PATH = vaOrginPath;
            }
            String vaTargetPath = configMap.get("va_target_path");
            if (!TextUtils.isEmpty(vaTargetPath)) {
                Config.VA_TARGET_PATH = vaTargetPath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MenuServie menuServie = TapcApp.getInstance().getService();
                if (menuServie != null) {
                    menuServie.setMenuBarVisibility(true);
                }
            }
        }, 1000);

        mFragmentManager = getSupportFragmentManager();
        setCheckedFunc(Item.APP);
    }

    private enum Item {
        APP,
        VACOPY,
        INSTALL,
        UNINSTALL,
        OS,
        SETTING
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

    @OnClick(R.id.func_setting)
    void setting() {
        setCheckedFunc(Item.SETTING);
    }

    private void setCheckedFunc(final Item item) {
        mFuncItemApp.setChecked(false);
        mFuncItemOs.setChecked(false);
        mFuncItemVacopy.setChecked(false);
        mFuncItemInstall.setChecked(false);
        mFuncItemUninstall.setChecked(false);
        mFuncItemSetting.setChecked(false);
        switch (item) {
            case APP:
                mFuncItemApp.setChecked(true);
                replaceFragment(Fragment.instantiate(this, UpdateAppFragment.class.getName()));
                break;
            case OS:
                mFuncItemOs.setChecked(true);
                replaceFragment(Fragment.instantiate(this, UpdateOsFragment.class.getName()));
                break;
            case VACOPY:
                mFuncItemVacopy.setChecked(true);
                replaceFragment(Fragment.instantiate(this, VaCopyFragment.class.getName()));
                break;
            case INSTALL:
                mFuncItemInstall.setChecked(true);
                replaceFragment(Fragment.instantiate(this, InstallAppFragment.class.getName()));
                break;
            case UNINSTALL:
                mFuncItemUninstall.setChecked(true);
                replaceFragment(Fragment.instantiate(this, UninstallAppFragment.class.getName()));
                break;
            case SETTING:
                mFuncItemSetting.setChecked(true);
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                break;
        }
//        showFragment(mFragmentList.get(item));
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        ft.replace(R.id.fragment, fragment);
        ft.commit();
    }

    private void showFragment(Fragment fragment) {
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
