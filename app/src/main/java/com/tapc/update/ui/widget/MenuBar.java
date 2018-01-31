package com.tapc.update.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.WindowManager;

import com.tapc.platform.jni.Driver;
import com.tapc.update.R;
import com.tapc.update.ui.adpater.MenuInfoAdapter;
import com.tapc.update.ui.base.BaseSystemView;
import com.tapc.update.ui.entity.MenuInfo;
import com.tapc.update.utils.WindowManagerUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class MenuBar extends BaseSystemView {
    @BindView(R.id.infor_lv)
    RecyclerView mListView;

    private MenuInfoAdapter mAdapter;
    private List<MenuInfo> mInforList;
    private Handler mHandler = new Handler();

    public MenuBar(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.widget_menu;
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        int with = (int) getResources().getDimension(R.dimen.menu_w);
        return WindowManagerUtils.getLayoutParams(0, 0, with, WindowManager.LayoutParams.MATCH_PARENT, Gravity.RIGHT
                | Gravity.CENTER_HORIZONTAL);
    }

    @Override
    protected void initView() {
        super.initView();
        mInforList = new ArrayList<>();
        mAdapter = new MenuInfoAdapter(mInforList);
        mListView.setLayoutManager(new LinearLayoutManager(mContext));
        mListView.setAdapter(mAdapter);
    }

    @OnClick(R.id.menu_back)
    void backOnClick() {
        Driver.back();
    }

    @OnClick(R.id.menu_exit)
    void exitOnClick() {
        System.exit(0);
    }

    @OnClick(R.id.infor_clear)
    void inforClear() {
        mInforList.clear();
        notifyDataSetChanged();
    }

    public void addInfor(MenuInfo.inforType type, String text) {
        MenuInfo menuInfo = new MenuInfo();
        menuInfo.setInforType(type);
        menuInfo.setText(text);
        mInforList.add(menuInfo);
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
