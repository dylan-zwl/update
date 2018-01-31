package com.tapc.update.ui.entity;

/**
 * Created by Administrator on 2017/7/18.
 */

public class MenuInfo {
    public enum inforType {
        INFO,
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

    public MenuInfo.inforType getInforType() {
        return inforType;
    }

    public void setInforType(MenuInfo.inforType inforType) {
        this.inforType = inforType;
    }

}
