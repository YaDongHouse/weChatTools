package com.dong.we.chat;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.dong.we.chat.constant.WeChatTextWrapper;

public class WeChatService extends AccessibilityService {

    private static final String TAG = "WeChatService";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
    }

    @Override
    protected void onServiceConnected() {
        //系统成功连接到辅助功能服务时调用
        super.onServiceConnected();
        Log.d(TAG,"onServiceConnected");

    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        //当系统检测到与Accessibility服务指定的事件过滤参数
        // 匹配的AccessibilityEvent时调用
        switch (accessibilityEvent.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Log.d(TAG,"TYPE_NOTIFICATION_STATE_CHANGED");
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.d(TAG,"TYPE_WINDOW_STATE_CHANGED");
                String currentActivity = accessibilityEvent.getClassName().toString();
                if (currentActivity.equals(WeChatTextWrapper.WechatClass.WECHAT_CLASS_LAUNCHUI)) {
                    Log.d(TAG, "onAccessibilityEvent: 微信首页");
                } else if (currentActivity.equals(WeChatTextWrapper.WechatClass.WECHAT_CLASS_CONTACTINFOUI)) {
                    Log.d(TAG, "onAccessibilityEvent: 微信联系人");
                } else if (currentActivity.equals(WeChatTextWrapper.WechatClass.WECHAT_CLASS_CHATUI)) {
                    Log.d(TAG, "onAccessibilityEvent: 微信聊天界面");
                }
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

                break;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                Log.d(TAG,"TYPE_WINDOWS_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Log.d(TAG, "onAccessibilityEvent: "+accessibilityEvent.getClassName().toString());
                break;
            default:
//                Log.d(TAG,"EventType:"+accessibilityEvent.getEventType());
//                Log.d(TAG, "onAccessibilityEvent: "+accessibilityEvent.toString());
        }
    }

    @Override
    public void onInterrupt() {
        //当系统想要中断服务提供的反馈时调用
        Log.d(TAG, "onInterrupt");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        //当系统即将关闭辅助功能服务时调用
    }
}