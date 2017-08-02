package com.tapc.update.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tapc.update.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/7/10.
 */

public class FunctionItem extends RelativeLayout {
    @BindView(R.id.func_line)
    ImageView mFuncItemLine;
    @BindView(R.id.func_ic)
    CheckBox mFuncItemIc;
    @BindView(R.id.func_name)
    TextView mFuncItemName;
    @BindView(R.id.func_loading)
    ProgressBar mFuncItemLoading;
    @BindView(R.id.func_arrows)
    ImageView mFuncItemArrows;

    private boolean mChecked = false;

    public FunctionItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.item_function, this);
        ButterKnife.bind(this, view);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FunctionItem);
        int btnDrawable = array.getResourceId(R.styleable.FunctionItem_ic, 0);
        String name = array.getString(R.styleable.FunctionItem_name);

        if (btnDrawable > 0) {
            mFuncItemIc.setButtonDrawable(btnDrawable);
        }

        if (!TextUtils.isEmpty(name)) {
            mFuncItemName.setText(name);
        }
        array.recycle();
    }

    public void setChecked(boolean checked) {
        if (mChecked == checked) {
            return;
        }
        if (checked) {
            mFuncItemLine.setVisibility(VISIBLE);
            mFuncItemName.setTextColor(getResources().getColor(R.color.tx_nomal1));
            mFuncItemLoading.setVisibility(VISIBLE);
            mFuncItemArrows.setVisibility(VISIBLE);
        } else {
            mFuncItemLine.setVisibility(GONE);
            mFuncItemName.setTextColor(getResources().getColor(R.color.tx_nomal2));
            mFuncItemLoading.setVisibility(GONE);
            mFuncItemArrows.setVisibility(GONE);
        }
        mFuncItemIc.setChecked(checked);
        mChecked = checked;
    }
}