package com.dong.we.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dong.we.chat.adapter.MenuAdapter;
import com.dong.we.chat.beans.MenuItemBean;
import com.dong.we.chat.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

import static com.dong.we.chat.constant.WeChatTextWrapper.WECAHT_PACKAGENAME;

public class MainActivity extends AppCompatActivity implements BaseQuickAdapter.OnItemClickListener {
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
        WindowViewManager.getInstance().init(this);
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

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        requestOverlayPermission();
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
        }else{
            checkPermission();
        }
    }

    public void checkPermission() {
        if (PermissionUtil.isAccessibilitySettingsOn(this)) {
            if (PermissionUtil.checkPermission(this)) {
               WindowViewManager.getInstance().startMenu();
                //跳转到微信
                Intent weChatIntent = this.getPackageManager().getLaunchIntentForPackage(WECAHT_PACKAGENAME);
                startActivity(weChatIntent);
            } else {
                PermissionUtil.applyPermission(this);
            }
        } else {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
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
