package com.tapc.update.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tapc.update.application.TapcApp;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/7/12.
 */

public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    protected Handler mHandler;
    private Unbinder mUnbinder;

    abstract public int getContentView();

    abstract public void initView();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getContentView(), container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mHandler = new Handler();
        initView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * onDestroyView中进行解绑操作
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected int mTaskNumber;

    public void updateProgressUi(final int progress) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                TapcApp.getInstance().getUpdateProgress().setProgress(progress);
            }
        });
    }

    public void startUpdate() {
        if (mTaskNumber > 0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TapcApp.getInstance().getUpdateProgress().setUpdateProgressVisibility(View.VISIBLE, false);
                }
            });
        }
    }

    public void stopUpdate() {
        if (mTaskNumber <= 0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TapcApp.getInstance().getUpdateProgress().setUpdateProgressVisibility(View.GONE, false);
                }
            });
        }
    }

    public void decTask() {
        mTaskNumber--;
        if (mTaskNumber < 0) {
            mTaskNumber = 0;
        }
    }

    public void incTask() {
        mTaskNumber++;
    }

    public void setTask(int number) {
        mTaskNumber = number;
    }
}
