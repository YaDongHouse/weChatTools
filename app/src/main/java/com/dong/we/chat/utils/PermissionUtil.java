package com.dong.we.chat.utils;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.dong.we.chat.WeChatService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static com.dong.we.chat.utils.MeizuUtils.commonROMPermissionApplyInternal;

public class PermissionUtil {

    private static final String TAG = "PermissionUtil";

    /**
     * 悬浮窗权限判断
     *
     * @param context 上下文
     * @return [ true, 有权限 ][ false, 无权限 ]
     */
    public static boolean checkPermission(Context context) {
        //vivo要单独处理
        if (RomUtils.checkIsVivo()) {
            return VivoUtils.checkFloatWindowPermission(context);
        }
        //6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.checkIsMiuiRom()) {
                return MiuiUtils.checkFloatWindowPermission(context);
            } else if (RomUtils.checkIsMeizuRom()) {
                return MeizuUtils.checkFloatWindowPermission(context);
            } else if (RomUtils.checkIsHuaweiRom()) {
                return HuaweiUtils.checkFloatWindowPermission(context);
            } else if (RomUtils.checkIs360Rom()) {
                return QikuUtils.checkFloatWindowPermission(context);
            } else if (RomUtils.checkIsOppoRom()) {
                return OppoUtils.checkFloatWindowPermission(context);
            }
        }
        return commonROMPermissionCheck(context);
    }


    /**
     * vivo手机这个方法始终返回true
     *
     * @param context
     * @return
     */
    private static boolean commonROMPermissionCheck(Context context) {
        //最新发现魅族6.0的系统这种方式不好用，天杀的，只有你是奇葩，没办法，单独适配一下
        if (RomUtils.checkIsMeizuRom()) {
            return MeizuUtils.checkFloatWindowPermission(context);
        } else {
            Boolean result = true;
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    Class clazz = Settings.class;
                    Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                    result = (Boolean) canDrawOverlays.invoke(null, context);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
            return result;
        }
    }


    /**
     * 判断辅助服务权限是否已经已经设置
     *
     * @param mContext
     * @return
     */
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        // TestService为对应的服务
        final String service = mContext.getPackageName() + "/" + WeChatService.class.getCanonicalName();
        Log.i(TAG, "service:" + service);
        // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }

    /**
     * 申请权限
     *
     * @param context
     */
    public static void applyPermission(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.checkIsMiuiRom()) {
                MiuiUtils.applyMiuiPermission(context);
            } else if (RomUtils.checkIsMeizuRom()) {
                MeizuUtils.applyPermission(context);
            } else if (RomUtils.checkIsHuaweiRom()) {
                HuaweiUtils.applyPermission(context);
            } else if (RomUtils.checkIs360Rom()) {
                QikuUtils.applyPermission(context);
            } else if (RomUtils.checkIsOppoRom()) {
                OppoUtils.applyOppoPermission(context);
            }
        } else {
            commonROMPermissionApply(context);
        }
    }

    /**
     * 通用 rom 权限申请
     */
    private static void commonROMPermissionApply(final Context context) {
        //这里也一样，魅族系统需要单独适配
        if (RomUtils.checkIsMeizuRom()) {
            MeizuUtils.applyPermission(context);
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    commonROMPermissionApplyInternal(context);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }

    }

    /**
     * Android 6.0 版本及之后的跳转权限申请界面
     *
     * @param context 上下文
     */
    private void highVersionJump2PermissionActivity(Context context) {
        try {
            Class clazz = Settings.class;
            Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
            Intent intent = new Intent(field.get(null).toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * [19-23]之间版本通过[AppOpsManager]的权限判断
     *
     * @param context 上下文
     * @param op
     * @return [ true, 有权限 ][ false, 无权限 ]
     */
    private static boolean opPermissionCheck(Context context, int op) {
        try {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Class clazz = AppOpsManager.class;
            Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
            return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
        } catch (Exception e) {
            Log.e(TAG, "opPermissionCheck: ", e);
        }
        return false;
    }

    /**
     * Android 6.0 版本及之后的权限判断
     *
     * @param context 上下文
     * @return [ true, 有权限 ][ false, 无权限 ]
     */
    private static boolean highVersionPermissionCheck(Context context) {
        if (RomUtils.checkIsMeizuRom()) {// 魅族6.0的系统单独适配
            return opPermissionCheck(context, 24);
        }
        try {
            Class clazz = Settings.class;
            Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
            return (Boolean) canDrawOverlays.invoke(null, context);
        } catch (Exception e) {
            Log.e(TAG, "highVersionPermissionCheck: ", e);
        }
        return false;
    }
}
