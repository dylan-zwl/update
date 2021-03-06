package com.tapc.update.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tapc.update.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/7/12.
 */

public class UpdateItem extends RelativeLayout {
    @BindView(R.id.update_item_tx)
    TextView mTitle;
    @BindView(R.id.update_item_right_tx)
    TextView mRightTx;
    @BindView(R.id.update_item_start_btn)
    Button mStartBtn;

    public UpdateItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.item_update_item, this);
        ButterKnife.bind(this, view);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.UpdateItem);
        String title = array.getString(R.styleable.UpdateItem_title);
        String btnTx = array.getString(R.styleable.UpdateItem_btn_tx);
        boolean isShowBtn = array.getBoolean(R.styleable.UpdateItem_is_show_btn, true);

        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
        if (!TextUtils.isEmpty(btnTx)) {
            mStartBtn.setText(btnTx);
        }
        if (isShowBtn) {
            mStartBtn.setVisibility(VISIBLE);
        } else {
            mStartBtn.setVisibility(GONE);
        }
        array.recycle();
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setRightTx(String tx) {
        mRightTx.setText(tx);
        mRightTx.setVisibility(VISIBLE);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
//        super.setOnClickListener(l);
        mStartBtn.setOnClickListener(l);
    }
}
