package com.dong.we.chat;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class WindowViewManager implements MenuManager.MenuClickListener {

    private Activity activity;
    private WindowManager windowManager;
    /**
     * 改浮动按钮布局参数
     */
    private WindowManager.LayoutParams layoutParams;

    private static WindowViewManager instance = new WindowViewManager();
    private View menuView;
    private MenuManager menuManager;

    private WindowViewManager() {
        initParams();
    }

    private void initParams() {
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        //调整悬浮窗至左上角
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        //设置屏幕左上角为原点，设置下x，y初始值
        layoutParams.x = 20;
        layoutParams.y = 20;
        //设置悬浮窗口的宽高
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public static WindowViewManager getInstance() {
        return instance;
    }


    //1 初始化
    public void init(Activity activity) {
        this.activity = activity;
        this.windowManager = activity.getWindowManager();
    }


    //2 开始展示
    public void startMenu() {
        menuManager = new MenuManager(activity);
        menuView = menuManager.getView();
        windowManager.addView(menuView, layoutParams);
        menuManager.setListener(this);

    }


    @Override
    public void onTopClick() {

    }

    @Override
    public void onBottomClick() {

    }

    private static final String TAG = "WindowViewManager";

    @Override
    public void onViewMove(int nx, int ny) {
        Log.d(TAG, "onViewMove: " + nx + "--" + ny);
        layoutParams.x = nx;
        layoutParams.y = ny;
        windowManager.updateViewLayout(menuView, layoutParams);
    }
}

