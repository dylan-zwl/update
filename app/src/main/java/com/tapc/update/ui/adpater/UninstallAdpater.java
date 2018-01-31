package com.tapc.update.ui.adpater;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tapc.update.R;
import com.tapc.update.ui.entity.AppInfoEntity;
import com.tapc.update.ui.view.CustomTextView;
import com.tapc.update.utils.IntentUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/1/30.
 */

public class UninstallAdpater extends BaseRecyclerViewAdapter<UninstallAdpater.ViewHolder, AppInfoEntity> {
    private List<AppInfoEntity> mListApkInfor;

    public UninstallAdpater(List<AppInfoEntity> datas) {
        super(datas);
        mListApkInfor = datas;
    }

    @Override
    public int getContentView() {
        return R.layout.item_uninstall_app;
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        final AppInfoEntity item = mListApkInfor.get(position);
        holder.checkBox.setChecked(item.isChecked());
        holder.icon.setImageDrawable(item.getAppIcon());
        holder.name.setText(item.getAppLabel());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
            }
        });
        holder.mUninstallRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !item.isChecked();
                item.setChecked(isChecked);
                holder.checkBox.setChecked(isChecked);
            }
        });
        holder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onStart(position);
            }
        });
        holder.openApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IntentUtil.startApp(mContext, item.getPkgName());
                } catch (Exception e) {
                    Log.d("uninstall", "start app : " + item.getAppLabel() + " failed");
                }
            }
        });
    }

    public interface Listener {
        void onStart(int position);
    }

    private Listener mListener;

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.uninstall_rl)
        RelativeLayout mUninstallRl;
        @BindView(R.id.uninstall_chk)
        CheckBox checkBox;
        @BindView(R.id.uninstall_app_ic)
        ImageView icon;
        @BindView(R.id.uninstall_app_name)
        CustomTextView name;
        @BindView(R.id.open_app_btn)
        Button openApp;
        @BindView(R.id.uninstall_start_btn)
        Button start;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
