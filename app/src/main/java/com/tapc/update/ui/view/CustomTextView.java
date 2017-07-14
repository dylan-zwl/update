package com.tapc.update.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/3/20.
 */

public class CustomTextView extends TextView {
    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(context, 0);
    }

    private void setTypeface(Context context, int type) {
        Typeface fontFace = Typeface.createFromAsset(context.getAssets(), "fonts/CONSTOM_GBK.TTF");
        setTypeface(fontFace);
    }
}