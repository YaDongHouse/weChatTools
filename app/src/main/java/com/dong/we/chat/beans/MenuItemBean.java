package com.dong.we.chat.beans;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

public class MenuItemBean {
    /**
     * 图标名字
     */
    String menuName;
    /**
     * 图标索引图片
     */
    @DrawableRes
    int menuRes;
    /**
     * 图标背景色
     */
    @ColorInt
    int menuColor;

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuRes() {
        return menuRes;
    }

    public void setMenuRes(int menuRes) {
        this.menuRes = menuRes;
    }

    public int getMenuColor() {
        return menuColor;
    }

    public void setMenuColor(int menuColor) {
        this.menuColor = menuColor;
    }

    @Override
    public String toString() {
        return "MenuItemBean{" +
                "menuName='" + menuName + '\'' +
                ", menuRes=" + menuRes +
                ", menuColor=" + menuColor +
                '}';
    }
}
