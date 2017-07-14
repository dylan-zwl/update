package com.tapc.update.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.tapc.update.R;

import butterknife.ButterKnife;


public class MenuBar extends LinearLayout {
    private Context mContext;

    public MenuBar(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_menu, this, true);
        ButterKnife.bind(this);
        mContext = context;
    }
}
