package com.tapc.update.ui.entity;

/**
 * Created by Administrator on 2017/7/18.
 */

public class MenuInfor {
    public enum inforType {
        INFOR,
        ERROR
    }

    private String text;
    private inforType inforType;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MenuInfor.inforType getInforType() {
        return inforType;
    }

    public void setInforType(MenuInfor.inforType inforType) {
        this.inforType = inforType;
    }

}
