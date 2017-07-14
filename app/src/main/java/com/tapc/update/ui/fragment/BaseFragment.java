package com.tapc.update.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/7/12.
 */

public abstract class BaseFragment extends Fragment {
    protected static String TAG;
    protected Context mContext;

    abstract public int getContentView();

    abstract public void initView();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getContentView(), container, false);
        ButterKnife.bind(this, view);
        initView();
        TAG = getClass().getName();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
