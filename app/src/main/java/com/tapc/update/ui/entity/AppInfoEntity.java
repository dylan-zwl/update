package com.tapc.update.ui.entity;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppInfoEntity {
    private String appLabel;
    private Drawable appIcon;
    private Intent intent;
    private String pkgName;
    private String path;
    private int type;
    private boolean isSystemApp;
    private boolean isChecked;
    private String installStatus;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public AppInfoEntity() {
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getInstallStatus() {
        return installStatus;
    }

    public void setInstallStatus(String installStatus) {
        this.installStatus = installStatus;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    public String getAppLabel() {
        return appLabel;
    }
}
