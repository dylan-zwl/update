package com.tapc.update.service.binder;

import android.app.Service;
import android.os.Binder;


public class LocalBinder extends Binder {
    private Service mService;

    public LocalBinder(Service service) {
        this.mService = service;
    }

    public void setService(Service service) {
        this.mService = service;
    }

    public Service getService() {
        return mService;
    }
}
