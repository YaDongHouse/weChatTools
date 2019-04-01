package com.dong.we.chat.adapter.holders;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.dong.we.chat.R;

public class MenuHolder extends BaseViewHolder {
    private CardView parentView;
    private ImageView imgView;
    private TextView textView;

    public MenuHolder(@NonNull View itemView) {
        super(itemView);
        parentView = itemView.findViewById(R.id.item_parent);
        imgView = itemView.findViewById(R.id.item_img);
        textView = itemView.findViewById(R.id.item_text);
    }

    public void  setText(String msg){
        textView.setText(msg);
    }
    public void setBackgroudColor(@ColorInt int color){
        parentView.setCardBackgroundColor(color);
        //设置透明度
        parentView.getBackground().setAlpha(100);
    }

    public void setImageRes(@DrawableRes int imageRes){
        imgView.setImageResource(imageRes);
    }
}
