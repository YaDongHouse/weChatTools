package com.dong.we.chat.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dong.we.chat.R;
import com.dong.we.chat.adapter.holders.MenuHolder;
import com.dong.we.chat.beans.MenuItemBean;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends BaseQuickAdapter<MenuItemBean,MenuHolder> {

    private List<MenuItemBean> mData = new ArrayList<>();

    public MenuAdapter(@Nullable List<MenuItemBean> data) {
        super(R.layout.item_menu, data);
    }
    @Override
    protected void convert(MenuHolder helper, MenuItemBean item) {
        helper.setText(item.getMenuName());
        helper.setBackgroudColor(item.getMenuColor());
        helper.setImageRes(item.getMenuRes());
    }


}
