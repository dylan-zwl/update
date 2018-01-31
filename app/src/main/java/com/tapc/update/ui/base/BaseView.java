package com.tapc.update.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/28.
 */

public abstract class BaseView extends LinearLayout {
    protected Context mContext;

    protected abstract int getLayoutResID();

    public BaseView(Context context) {
        super(context);
        init(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initView(attrs);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(getLayoutResID(), this, true);
        ButterKnife.bind(this);
        mContext = context;
        initView();
    }

    protected void initView() {
    }

    protected void initView(AttributeSet attrs) {
    }

    public void onDestroy() {
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
