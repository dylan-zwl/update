package com.tapc.update.utils;

import android.content.Context;

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
            TapcApp.getInstance().addInfor(MenuInfo.inforType.INFO, infor);
        } else {
            infor = infor + context.getString(R.string.failed) + " : " + msg;
            TapcApp.getInstance().addInfor(MenuInfo.inforType.ERROR, infor);
        }
    }

    public static String getInforText(Context context, String title, String updateType, boolean isSuccess, String msg) {
        String infor = title + " " + updateType + " ";
        if (isSuccess) {
            infor = infor + context.getString(R.string.success);
        } else {
            infor = infor + context.getString(R.string.failed) + " : " + msg;
        }
        return infor;
    }
}
