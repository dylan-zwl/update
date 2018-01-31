package com.tapc.update.ui.adpater;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tapc.update.R;
import com.tapc.update.ui.base.BaseRecyclerViewAdapter;
import com.tapc.update.ui.entity.MenuInfo;
import com.tapc.update.ui.view.CustomTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/25.
 */

public class MenuInfoAdapter extends BaseRecyclerViewAdapter<MenuInfoAdapter.ViewHolder, MenuInfo> implements View
        .OnClickListener {

    public MenuInfoAdapter(List<MenuInfo> list) {
        super(list);
    }

    @Override
    public int getContentView() {
        return R.layout.item_info;
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        MenuInfo item = mDatas.get(position);
        switch (item.getInforType()) {
            case INFO:
                holder.text.setTextColor(mContext.getResources().getColor(R.color.tx_nomal3));
                break;
            case ERROR:
                holder.text.setTextColor(mContext.getResources().getColor(R.color.tx_nomal5));
                break;
        }
        holder.text.setText(item.getText());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.infor_text)
        CustomTextView text;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
