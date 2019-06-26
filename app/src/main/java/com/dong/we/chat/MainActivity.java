package com.dong.we.chat;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dong.we.chat.adapter.MenuAdapter;
import com.dong.we.chat.beans.MenuItemBean;
import com.dong.we.chat.utils.PermissionUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.dong.we.chat.constant.WeChatTextWrapper.MY_PACKAGENAME;
import static com.dong.we.chat.constant.WeChatTextWrapper.WECAHT_PACKAGENAME;

public class MainActivity extends AppCompatActivity implements BaseQuickAdapter.OnItemClickListener {
    private static final int REQUEST_OVERLAY = 4444;

    RecyclerView list;
    WindowManager windowManager;

    /**
     * 获取sdk版本号
     */
    private static final int SDKVERSION = Build.VERSION.SDK_INT;

    /**
     * 浮动按钮布局参数
     */
    private WindowManager.LayoutParams layoutParams;

    /**
     * 浮动按钮布局
     */
    private View moveMenus;

    private View centerMenus;

    /**
     * 顶部状态栏高度
     */
    private int top;
    /**
     * 浮动窗原始位置
     */
    private float startPositionX = 0;
    private float startPositionY = 0;

    private long mDownTime;
    private long mUpTime;
    /**
     * 屏幕宽高
     */
    private int contentWidth;
    private int contentHeight;

    private float lastX;
    private float lastY;
    private float mTouchStartX;
    private float mTouchStartY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = findViewById(R.id.wc_recycler);
        initData();
    }

    private void initData() {
        moveMenus = LayoutInflater.from(this).inflate(R.layout.layout_menu, null);
        centerMenus = LayoutInflater.from(this).inflate(R.layout.layout_menu_buttons, null);
        //适配器相关
        List<MenuItemBean> md = new ArrayList<>();
        MenuItemBean first = new MenuItemBean();
        first.setMenuColor(R.color.backBlack);
        first.setMenuName("加群好友");
        first.setMenuRes(R.mipmap.group_add);
        md.add(first);
        MenuAdapter menuAdapter = new MenuAdapter(md);
        list.setLayoutManager(new GridLayoutManager(this, 4));
        list.setAdapter(menuAdapter);
        menuAdapter.setOnItemClickListener(this);
        //windowManager初始化
        windowManager = this.getWindowManager();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        requestOverlayPermission();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //获取整个布局的宽高
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        contentWidth = size.x;
        contentHeight = size.y;
        Rect rect = new Rect();
        //取得整个视图部分,注意，如果你要设置标题样式，这个必须出现在标题样式之后，否则会出错
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        top = rect.top;//状态栏的高度，所以rect.height,rect.width分别是系统的高度的宽度
        Log.d(TAG, "onWindowFocusChanged Width: " + contentWidth + "   Height:" + contentHeight + "   Top:" + top);
    }


    private static final String TAG = "MainActivity";


    public void startCenterMenu() {
        if (layoutParams == null) {
            layoutParams = new WindowManager.LayoutParams();
        }
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        //调整悬浮窗至左上角
        layoutParams.gravity = Gravity.CENTER;
        //设置屏幕左上角为原点，设置下x，y初始值
        layoutParams.x = 0;
        layoutParams.y = 0;
        //设置悬浮窗口的宽高
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowManager.addView(moveMenus, layoutParams);
    }


    private boolean isShowMenu = false;

    public void showMenu() {
        if (!isShowMenu) {
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
            layoutParams.x = 0;
            layoutParams.y = 0;
            //设置悬浮窗口的宽高
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            windowManager.addView(moveMenus, layoutParams);
            isShowMenu = true;
            final View topView = moveMenus.findViewById(R.id.menu_top);
            final View bottomView = moveMenus.findViewById(R.id.menu_bottom);
            moveMenus.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    lastX = event.getRawX();
                    lastY = event.getRawY() - top;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mTouchStartX = event.getX();
                            mTouchStartY = event.getY();
                            //记录悬浮窗原始位置
                            startPositionX = layoutParams.x;
                            startPositionY = layoutParams.y;
                            mDownTime = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            //计算新的位置
                            layoutParams.x = (int) (lastX - mTouchStartX);
                            layoutParams.y = (int) (lastY - mTouchStartY);
                            windowManager.updateViewLayout(moveMenus, layoutParams);
                            break;
                        case MotionEvent.ACTION_UP:
                            mUpTime = System.currentTimeMillis();
                            if (mUpTime - mDownTime > 200) {
                                return true;
                            } else {
                                if (event.getY() <= topView.getHeight()) {
                                    topClick();
                                }
                                if (event.getY() >= (moveMenus.getHeight() - bottomView.getHeight())) {
                                    bottomClick();
                                }
                            }
                    }
                    return false;
                }
            });
        }
    }

    private void topClick() {
        Log.d(TAG, "topClick: 点击了");
    }

    private void bottomClick() {
        Intent weChatIntent = this.getPackageManager().getLaunchIntentForPackage(MY_PACKAGENAME);
        startActivity(weChatIntent);
    }


    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY);
            } else {
                checkPermission();
            }
        }
    }

    public void checkPermission() {
        if (!PermissionUtil.isAccessibilitySettingsOn(this)) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        } else {
            if (PermissionUtil.checkPermission(this)) {
                showMenu();
                //跳转到微信
                Intent weChatIntent = this.getPackageManager().getLaunchIntentForPackage(WECAHT_PACKAGENAME);
                startActivity(weChatIntent);
            } else {
                PermissionUtil.applyPermission(this);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_OVERLAY) {
            if (Settings.canDrawOverlays(this)) {
                checkPermission();
            } else {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
