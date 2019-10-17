package com.tapc.update.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.tapc.update.R;
import com.tapc.update.application.Config;
import com.tapc.update.application.TapcApp;
import com.tapc.update.service.MenuService;
import com.tapc.update.ui.fragment.InstallAppFragment;
import com.tapc.update.ui.fragment.UninstallAppFragment;
import com.tapc.update.ui.fragment.UpdateAppFragment;
import com.tapc.update.ui.fragment.UpdateOsFragment;
import com.tapc.update.ui.fragment.VaCopyFragment;
import com.tapc.update.ui.fragment.VersionFragment;
import com.tapc.update.ui.view.FunctionItem;
import com.tapc.update.ui.widget.MenuBar;
import com.tapc.update.utils.IntentUtil;

import java.io.File;

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
    @BindView(R.id.func_setting)
    FunctionItem mFuncItemSetting;
    @BindView(R.id.func_version)
    FunctionItem mFuncItemVersion;
    @BindView(R.id.fragment)
    FrameLayout mFragment;

    protected FragmentManager mFragmentManager;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String manualPath = Config.SAVEFILE_ORIGIN__PATH + "manual";
        if (!new File(manualPath).exists()) {
            IntentUtil.startActivity(this, AutoUpdateActivity.class, null,
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            return;
        }

        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        TapcApp.getInstance().startUpdate();
        initView();
    }

    private void initView() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MenuService menuServie = TapcApp.getInstance().getService();
                if (menuServie != null) {
                    menuServie.setMenuBarVisibility(true);
                    menuServie.getMenuBar().setExitListener(new MenuBar.ExitListener() {
                        @Override
                        public void exit() {
                            TapcApp.getInstance().stopUpdate();
                            MainActivity.this.finish();
                        }
                    });
                }
            }
        }, 1500);

        mFragmentManager = getSupportFragmentManager();
        setCheckedFunc(Item.APP);
    }

    private enum Item {
        APP,
        VACOPY,
        INSTALL,
        UNINSTALL,
        OS,
        SETTING,
        VERSION
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

    @OnClick(R.id.func_version)
    void version() {
        setCheckedFunc(Item.VERSION);
    }

    private void setCheckedFunc(final Item item) {
        mFuncItemApp.setChecked(false);
        mFuncItemOs.setChecked(false);
        mFuncItemVacopy.setChecked(false);
        mFuncItemInstall.setChecked(false);
        mFuncItemUninstall.setChecked(false);
        mFuncItemSetting.setChecked(false);
        mFuncItemVersion.setChecked(false);
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
            case VERSION:
                mFuncItemVersion.setChecked(true);
                replaceFragment(Fragment.instantiate(this, VersionFragment.class.getName()));
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        ft.replace(R.id.fragment, fragment);
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
