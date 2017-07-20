package com.tapc.update.utils;

import android.content.Context;

import com.tapc.update.R;
import com.tapc.update.application.TapcApp;
import com.tapc.update.ui.entity.MenuInfor;

/**
 * Created by Administrator on 2017/7/19.
 */

public class ShowInforUtil {
    public static void send(Context context, String title, String updateType, boolean isSuccess, String msg) {
        String infor = title + " " + updateType + " ";
        if (isSuccess) {
            infor = infor + context.getString(R.string.success);
            TapcApp.getInstance().addInfor(MenuInfor.inforType.INFOR, infor);
        } else {
            infor = infor + context.getString(R.string.failed) + " : " + msg;
            TapcApp.getInstance().addInfor(MenuInfor.inforType.ERROR, infor);
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
