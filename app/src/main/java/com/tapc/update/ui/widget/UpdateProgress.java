package com.tapc.update.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.tapc.update.R;
import com.tapc.update.ui.base.BaseSystemView;
import com.tapc.update.ui.view.CustomTextView;
import com.tapc.update.utils.WindowManagerUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/18.
 */

public class UpdateProgress extends BaseSystemView {
    @BindView(R.id.update_progress_tx)
    CustomTextView mProgressTx;

    @BindView(R.id.update_progress_unit)
    CustomTextView mProgressUnit;

    @BindView(R.id.update_progress)
    ProgressBar mUpdateProgress;
    @BindView(R.id.update_success)
    Button mUpdateSuccess;
    @BindView(R.id.update_again)
    Button mUpdateAgain;

    @Override
    protected int getLayoutResID() {
        return R.layout.widget_update_progress;
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        return WindowManagerUtils.getLayoutParams(0, 0, WindowManager.LayoutParams.MATCH_PARENT, WindowManager
                .LayoutParams.MATCH_PARENT, Gravity.TOP | Gravity.CENTER_VERTICAL);
    }

    public UpdateProgress(Context context) {
        super(context);
    }

    public UpdateProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setProgress(int progress) {
        mProgressTx.setText("" + progress);
        mProgressTx.setVisibility(VISIBLE);
        mProgressUnit.setVisibility(VISIBLE);
    }

    public void setProgressTxVisibility(int visibility) {
        mProgressTx.setVisibility(visibility);
        mProgressUnit.setVisibility(visibility);
    }

    public void setUpdateProgressVisibility(int visibility, boolean isShowProgress) {
        setVisibility(visibility);
        if (isShowProgress) {
            setProgressTxVisibility(View.VISIBLE);
            mUpdateProgress.setVisibility(VISIBLE);
        } else {
            setProgressTxVisibility(View.GONE);
        }
    }

    public void setUpdateStatus(boolean isSuccess) {
        mUpdateProgress.setVisibility(GONE);
        if (isSuccess) {
            mUpdateSuccess.setVisibility(VISIBLE);
            mUpdateAgain.setVisibility(GONE);
        } else {
            mUpdateAgain.setVisibility(VISIBLE);
            mUpdateSuccess.setVisibility(GONE);
        }
    }

    public interface Listener {
        void updateAgainOnClick();
    }

    private Listener mListener;

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    @OnClick(R.id.update_again)
    void updateAgain() {
        if (mListener != null) {
            mUpdateProgress.setVisibility(VISIBLE);
            mUpdateSuccess.setVisibility(GONE);
            mUpdateAgain.setVisibility(GONE);
            mListener.updateAgainOnClick();
        }
    }
}
