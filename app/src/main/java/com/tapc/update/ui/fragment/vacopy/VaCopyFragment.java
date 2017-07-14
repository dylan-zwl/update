package com.tapc.update.ui.fragment.vacopy;

import android.widget.Button;
import android.widget.TextView;

import com.tapc.update.R;
import com.tapc.update.ui.fragment.BaseFragment;
import com.tapc.update.ui.view.UpdateItem;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/7/10.
 */

public class VaCopyFragment extends BaseFragment {
    @BindView(R.id.func_name_tx)
    TextView mTitle;
    @BindView(R.id.func_start_btn)
    Button mStartUpdate;

    @BindView(R.id.va_from_path)
    UpdateItem mVaFromPath;
    @BindView(R.id.va_target_path)
    UpdateItem mVaTargetPath;

    @Override
    public int getContentView() {
        return R.layout.fragment_vacopy;
    }

    @Override
    public void initView() {
        mTitle.setText(getString(R.string.func_vacopy));
        mStartUpdate.setText(getString(R.string.va_copy));
    }
}
