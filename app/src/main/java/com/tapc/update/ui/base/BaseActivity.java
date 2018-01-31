package com.tapc.update.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/9/5.
 */

public abstract class BaseActivity extends Activity {
    protected Context mContext;

    protected abstract int getLayoutResID();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int rid = getLayoutResID();
        if (rid != 0) {
            setContentView(rid);
            ButterKnife.bind(this);
        }
        mContext = this;
        initView();
    }

    protected void initView() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
