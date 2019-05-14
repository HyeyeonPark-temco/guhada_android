package io.temco.guhada.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    // -------- LOCAL VALUE --------
    private static Context mApplicationContext;
    // Key
    private static final String KEY_AUTO_LOGIN = "auto_login";
    // -----------------------------

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private static SharedPreferences getPreferences() {
        if (mApplicationContext == null) {
            return null;
        }
        return mApplicationContext.getSharedPreferences(Info.APP_TAG, Activity.MODE_PRIVATE);
    }

    private static void putString(String key, String value) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            return;
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static void putBoolean(String key, boolean value) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            return;
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static void putInt(String key, int value) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            return;
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private static void putLong(String key, long value) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            return;
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    private static String getString(String key) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            return "";
        }
        return pref.getString(key, "");
    }

    private static boolean getBoolean(String key) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            return false;
        }
        return pref.getBoolean(key, false);
    }

    private static boolean getBoolean(String key, boolean isDefault) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            return false;
        }
        return pref.getBoolean(key, isDefault);
    }

    private static int getInt(String key, int defaultValue) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            return 0;
        }
        return pref.getInt(key, defaultValue);
    }

    private static long getLong(String key, int defaultValue) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            return 0;
        }
        return pref.getLong(key, defaultValue);
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public static void init(Context context) {
        mApplicationContext = context;
    }

    public static void clear() {
        Preferences.setAutoLogin(false);
    }

    // Auto Login
    public static void setAutoLogin(boolean isOn) {
        putBoolean(KEY_AUTO_LOGIN, isOn);
    }

    public static boolean getAutoLogin() {
        return getBoolean(KEY_AUTO_LOGIN);
    }

    ////////////////////////////////////////////////
}