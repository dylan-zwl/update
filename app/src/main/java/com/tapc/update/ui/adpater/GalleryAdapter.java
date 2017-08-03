/**
 * GalleryAdapter.java[v 1.0.0]
 * classes:com.jht.tapc.platform.adpater.GalleryAdapter
 * fch Create of at 2015��3��17�� ����6:05:13
 */
package com.tapc.update.ui.adpater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tapc.platform.model.vaplayer.PlayEntity;
import com.tapc.update.R;
import com.tapc.update.ui.view.CustomTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NewApi")
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> implements
        OnClickListener {

    private ArrayList<PlayEntity> mList;

    private Context mContext;
    private OnItemClickListener<PlayEntity> mOnItemClickListener;

    public GalleryAdapter(Context context, ArrayList<PlayEntity> list) {
        mContext = context;
        mList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.play_gallery_img)
        ImageView img;
        @BindView(R.id.play_gallery_name)
        CustomTextView fileName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return (mList != null) ? mList.size() : 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewholder, int index) {
        viewholder.itemView.setTag(mList.get(index));
        String path = mList.get(index).getPath();
        viewholder.img.setImageBitmap(BitmapFactory.decodeFile(path + "/" + mList.get(index).still));
        viewholder.fileName.setText(mList.get(index).name);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_scene_gallery, arg0, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        holder.img = (ImageView) view.findViewById(R.id.play_gallery_img);
        holder.fileName = (CustomTextView) view.findViewById(R.id.play_gallery_name);
        return holder;
    }

    public void setOnItemClickListener(OnItemClickListener<PlayEntity> listhener) {
        mOnItemClickListener = listhener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            PlayEntity entity = (PlayEntity) v.getTag();
            mOnItemClickListener.onItemClick(v, entity);
        }
    }

    public interface OnItemClickListener<Entity> {
        void onItemClick(View view, Entity entity);
    }

    public void notifyDataSetChanged(ArrayList<PlayEntity> list) {
        mList = list;
        notifyDataSetChanged();
    }
}
