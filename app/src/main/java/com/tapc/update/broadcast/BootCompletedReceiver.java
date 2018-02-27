package com.tapc.update.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tapc.update.service.BootService;
import com.tapc.update.utils.IntentUtil;

public class BootCompletedReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            IntentUtil.startService(context, BootService.class);
        }
    }
}
