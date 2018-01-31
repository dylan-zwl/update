package com.tapc.update.ui.adpater;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tapc.platform.model.vaplayer.PlayEntity;
import com.tapc.update.R;
import com.tapc.update.ui.base.BaseRecyclerViewAdapter;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/25.
 */

public class VaAdapter extends BaseRecyclerViewAdapter<VaAdapter.VaViewHolder, PlayEntity> implements View
        .OnClickListener {

    public VaAdapter(List<PlayEntity> list) {
        super(list);
    }

    @Override
    public int getContentView() {
        return R.layout.item_scene_gallery;
    }

    @Override
    public VaViewHolder getViewHolder(View view) {
        return new VaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VaViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        PlayEntity item = mDatas.get(position);
        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(this);
        String path = item.getPath() + "/" + item.getStill();
        File file = new File(path);
        if (file.exists()) {
            holder.icon.setImageBitmap(BitmapFactory.decodeFile(path));
        }
        String name = item.getName();
        if (!TextUtils.isEmpty(name)) {
            holder.name.setText(name);
        }
    }

    public class VaViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.play_gallery_img)
        ImageView icon;
        @BindView(R.id.play_gallery_name)
        TextView name;

        public VaViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
