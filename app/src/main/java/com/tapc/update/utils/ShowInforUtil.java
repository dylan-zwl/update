package com.tapc.update.utils;

import android.content.Context;
import android.text.TextUtils;

import com.tapc.update.R;
import com.tapc.update.application.TapcApp;
import com.tapc.update.ui.entity.MenuInfo;

/**
 * Created by Administrator on 2017/7/19.
 */

public class ShowInforUtil {
    public static void send(Context context, String title, String updateType, boolean isSuccess, String msg) {
        String infor = title + " " + updateType + " ";
        if (isSuccess) {
            infor = infor + context.getString(R.string.success);
            if (!TextUtils.isEmpty(msg)) {
                infor = infor + " : " + msg;
            }
            TapcApp.getInstance().addInfor(MenuInfo.inforType.INFO, infor);
        } else {
            infor = infor + context.getString(R.string.failed);
            if (!TextUtils.isEmpty(msg)) {
                infor = infor + " : " + msg;
            }
            TapcApp.getInstance().addInfor(MenuInfo.inforType.ERROR, infor);
        }
    }

    public static String getInforText(Context context, String title, String updateType, boolean isSuccess, String msg) {
        String infor = title + " " + updateType + " ";
        if (isSuccess) {
            infor = infor + context.getString(R.string.success);
        } else {
            infor = infor + context.getString(R.string.failed);
        }
        if (!TextUtils.isEmpty(msg)) {
            infor = infor + " : " + msg;
        }
        return infor;
    }
}
