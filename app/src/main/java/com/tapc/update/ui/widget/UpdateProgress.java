package com.tapc.update.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.tapc.update.R;
import com.tapc.update.ui.view.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/18.
 */

public class UpdateProgress extends RelativeLayout {
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

    public UpdateProgress(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_update_progress, this);
        ButterKnife.bind(this, view);
    }

    public UpdateProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_update_progress, this);
        ButterKnife.bind(this, view);
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
