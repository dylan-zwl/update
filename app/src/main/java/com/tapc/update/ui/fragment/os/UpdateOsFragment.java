package com.tapc.update.ui.fragment.os;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.tapc.update.R;
import com.tapc.update.ui.fragment.BaseFragment;
import com.tapc.update.ui.view.UpdateItem;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/7/10.
 */

public class UpdateOsFragment extends BaseFragment {
    @BindView(R.id.func_name_tx)
    TextView mTitle;
    @BindView(R.id.func_start_btn)
    Button mStartUpdate;

    @BindView(R.id.update_os)
    UpdateItem mUpdateItemOS;

    @Override
    public int getContentView() {
        return R.layout.fragment_os;
    }

    @Override
    public void initView() {
        mTitle.setText(getString(R.string.func_os));
        mStartUpdate.setText(getString(R.string.a_key_update));

        String osVersion = android.os.Build.DISPLAY;
        if (!TextUtils.isEmpty(osVersion)) {
            mUpdateItemOS.setTitle(String.format(mContext.getString(R.string.update_os_title), osVersion));
        }
    }
}
