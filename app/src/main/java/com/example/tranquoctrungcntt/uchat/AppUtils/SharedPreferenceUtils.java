package com.example.tranquoctrungcntt.uchat.AppUtils;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class SharedPreferenceUtils {

    private static final String appSharefKey = "Uchat";
    private static final int appSharefMode = MODE_PRIVATE;

    public static boolean isFirstOpenMain(Context context) {
        return context.getSharedPreferences(appSharefKey, appSharefMode).getBoolean("isFirstOpenMain", true);
    }

    public static void updateFirstOpenMain(Context context) {
        context.getSharedPreferences(appSharefKey, appSharefMode).edit().putBoolean("isFirstOpenMain", false).apply();
    }

    public static void updateNotificationsRequestcode(Context context) {
        context.getSharedPreferences(appSharefKey, appSharefMode).edit().putInt("NotificationRequestCode",
                getNotificationsRequestcode(context) + 1 == 2147483640 ? 2 : getNotificationsRequestcode(context) + 1).apply();
    }

    public static int getNotificationsRequestcode(Context context) {
        return context.getSharedPreferences(appSharefKey, appSharefMode).getInt("NotificationRequestCode", 2);
    }

    public static Map<String, String> getRememberedAccountMap(Context context) {
        Map<String, String> map = new HashMap<>();

        map.put("email", context.getSharedPreferences(appSharefKey, appSharefMode).getString("email", ""));
        map.put("password", context.getSharedPreferences(appSharefKey, appSharefMode).getString("password", ""));

        return map;
    }

    public static void setRememberAccountMap(Context context, String email, String password) {
        context.getSharedPreferences(appSharefKey, appSharefMode).edit().putString("email", email).apply();
        context.getSharedPreferences(appSharefKey, appSharefMode).edit().putString("password", password).apply();
    }

    public static boolean isAccountRemembered(Context context) {
        return context.getSharedPreferences(appSharefKey, appSharefMode).getBoolean("isAccountRemembered", false);
    }

    public static void updateRememberAccountStatus(Context context, boolean remember) {
        context.getSharedPreferences(appSharefKey, appSharefMode).edit().putBoolean("isAccountRemembered", remember).apply();
    }

    public static boolean isSinchTokenRegistered(Context context) {
        return context.getSharedPreferences(appSharefKey, appSharefMode).getBoolean("isSinchTokenRegistered", false);
    }

    public static void updatedSinchTokenRegisteredStatus(Context context, boolean isRegistered) {
        context.getSharedPreferences(appSharefKey, appSharefMode).edit().putBoolean("isSinchTokenRegistered", isRegistered).apply();
    }

    public static boolean shouldShowRequestPermissionWhenLogin(Context context) {
        return context.getSharedPreferences(appSharefKey, appSharefMode).getBoolean("shouldShowRequestPermissionWhenLogin", true);
    }

    public static void updateShouldShowRequestPermissionWhenLogin(Context context, boolean shouldShow) {
        context.getSharedPreferences(appSharefKey, appSharefMode).edit().putBoolean("shouldShowRequestPermissionWhenLogin", shouldShow).apply();
    }
}
