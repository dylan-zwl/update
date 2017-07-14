package com.tapc.update.ui.adpater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */

public class BaseAppAdpater<T> extends BaseAdapter {
    private List<T> mList;
    private Listener mListener;

    public BaseAppAdpater(List<T> list, Listener listener) {
        this.mList = list;
        mListener = listener;
    }

    @Override
    public int getCount() {
        if (mList.size() > 0) {
            return mList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mListener != null) {
            convertView = mListener.getView(position, convertView, parent);
        }
        return convertView;
    }

    public interface Listener {
        View getView(int position, View convertView, ViewGroup parent);
    }
}
