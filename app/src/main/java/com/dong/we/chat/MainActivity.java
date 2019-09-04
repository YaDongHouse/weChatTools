package com.dong.we.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dong.we.chat.adapter.MenuAdapter;
import com.dong.we.chat.beans.MenuItemBean;
import com.dong.we.chat.utils.PermissionUtil;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.anim.AppFloatDefaultAnimator;
import com.lzf.easyfloat.anim.DefaultAnimator;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.lzf.easyfloat.interfaces.OnInvokeView;
import com.lzf.easyfloat.utils.logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.dong.we.chat.constant.WeChatTextWrapper.WECAHT_PACKAGENAME;

public class MainActivity extends AppCompatActivity implements BaseQuickAdapter.OnItemClickListener, OnInvokeView, View.OnClickListener {
    private static final int REQUEST_OVERLAY = 4444;

    RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = findViewById(R.id.wc_recycler);
        initData();
    }

    private void initData() {
        // 初始化
//        WindowViewManager.getInstance().init(this);
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
    }

    private static final String TAG = "MainActivity";

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//        requestOverlayPermission();
        EasyFloat.with(this)
                .setLayout(R.layout.layout_menu, this)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                .setTag("testFloat")
                .setDragEnable(true)
                .setLocation(10, 10)
                .setGravity(Gravity.END | Gravity.CENTER_VERTICAL)
                .setMatchParent(false, false)
                .setAnimator(new DefaultAnimator())
                .setAppFloatAnimator(new AppFloatDefaultAnimator())
                .show();
    }

    @Override
    public void invoke(View view) {
        view.findViewById(R.id.menu_top).setOnClickListener(this);
        view.findViewById(R.id.menu_bottom).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_top:
                Log.e(TAG, "onClick: EasyFloat");
                break;
            case R.id.menu_bottom:
                Log.e(TAG, "onClick: menu_bottom");
                break;
            default:
        }
    }
//
//    private void requestOverlayPermission() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (!Settings.canDrawOverlays(this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                        Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, REQUEST_OVERLAY);
//            } else {
//                checkPermission();
//            }
//        }else{
//            checkPermission();
//        }
//    }
//
//    public void checkPermission() {
//        if (PermissionUtil.isAccessibilitySettingsOn(this)) {
//            if (PermissionUtil.checkPermission(this)) {
//               WindowViewManager.getInstance().startMenu();
//                //跳转到微信
//                Intent weChatIntent = this.getPackageManager().getLaunchIntentForPackage(WECAHT_PACKAGENAME);
//                startActivity(weChatIntent);
//            } else {
//                PermissionUtil.applyPermission(this);
//            }
//        } else {
//            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//        }
//    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == REQUEST_OVERLAY) {
//            if (Settings.canDrawOverlays(this)) {
//                checkPermission();
//            } else {
//                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
