package com.tapc.update.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.tapc.update.ui.entity.WidgetShowStatus;
import com.tapc.update.utils.WindowManagerUtils;


/**
 * Created by Administrator on 2017/10/27.
 */

public abstract class BaseSystemView extends BaseView {
    protected WindowManager mWindowManager;
    private boolean mAddViewed;

    public BaseSystemView(Context context) {
        super(context.getApplicationContext());
    }

    public BaseSystemView(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
    }

    @Override
    protected void initView() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        super.initView();
    }

    public void setShowStatus(WidgetShowStatus status) {
        switch (status) {
            case VISIBLE:
                addViewToWindow();
                setVisibility(View.VISIBLE);
                break;
            case INVISIBLE:
                if (mAddViewed) {
                    setVisibility(View.INVISIBLE);
                }
                break;
            case GONE:
                if (mAddViewed) {
                    setVisibility(View.GONE);
                }
                break;
            case REMOVE:
                if (mAddViewed) {
                    mWindowManager.removeView(this);
                    onDestroy();
                    mAddViewed = false;
                }
                break;
        }
    }

    public void addViewToWindow() {
        if (!mAddViewed) {
            WindowManager.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams != null) {
                WindowManagerUtils.addView(mWindowManager, this, getLayoutParams());
                mAddViewed = true;
                setShowStatus(WidgetShowStatus.GONE);
            }
        }
    }

    public void show() {
        setShowStatus(WidgetShowStatus.VISIBLE);
    }

    public void hide() {
        setShowStatus(WidgetShowStatus.GONE);
    }

    public void dismiss() {
        setShowStatus(WidgetShowStatus.REMOVE);
    }

    public abstract WindowManager.LayoutParams getLayoutParams();
}
