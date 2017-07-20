package com.tapc.update.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tapc.update.R;
import com.tapc.update.application.TapcApp;
import com.tapc.update.ui.adpater.BaseAppAdpater;
import com.tapc.update.ui.entity.MenuInfor;
import com.tapc.update.ui.view.CustomTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MenuBar extends LinearLayout {
    @BindView(R.id.infor_lv)
    ListView mListView;

    private Context mContext;
    private BaseAppAdpater mAdapter;
    private List<MenuInfor> mInforList;
    private Handler mHandler = new Handler();

    public MenuBar(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_menu, this, true);
        ButterKnife.bind(this);
        mContext = context;
        initView();
    }

    @OnClick(R.id.menu_back)
    void backOnClick() {
        TapcApp.getInstance().getKeyboardEvent().backEvent();
    }

    @OnClick(R.id.menu_exit)
    void exitOnClick() {
        System.exit(0);
    }

    @OnClick(R.id.infor_clear)
    void inforClear() {
        mInforList.clear();
        notifyDataSetChanged();
    }

    public void addInfor(MenuInfor.inforType type, String text) {
        MenuInfor menuInfor = new MenuInfor();
        menuInfor.setInforType(type);
        menuInfor.setText(text);
        mInforList.add(menuInfor);
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initView() {
        mInforList = new ArrayList<>();
        mAdapter = new BaseAppAdpater(mInforList, new BaseAppAdpater.Listener() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.item_infor, null);
                    viewHolder = new ViewHolder();
                    ButterKnife.bind(viewHolder, convertView);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                MenuInfor item = mInforList.get(position);
                switch (item.getInforType()) {
                    case INFOR:
                        viewHolder.text.setTextColor(mContext.getResources().getColor(R.color.tx_nomal3));
                        break;
                    case ERROR:
                        viewHolder.text.setTextColor(mContext.getResources().getColor(R.color.tx_nomal5));
                        break;
                }
                viewHolder.text.setText(item.getText());
                return convertView;
            }
        });
        mListView.setAdapter(mAdapter);
    }

    class ViewHolder {
        @BindView(R.id.infor_text)
        CustomTextView text;
    }
}
