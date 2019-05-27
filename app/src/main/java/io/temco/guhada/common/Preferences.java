package io.temco.guhada.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.Token;

public class Preferences {

    // -------- LOCAL VALUE --------
    private static Context mApplicationContext;
    // Key
    private static final String KEY_AUTO_LOGIN = "auto_login";
    private static final String KEY_CATEGORY_DATA = "category_data";
    private static final String KEY_BRAND_DATA = "brand_data";

    private static final String USER_TOKEN = "token";

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

    private static void putJsonObject(String key, Object o) {
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            return;
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, new Gson().toJson(o));
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

    // Category
    public static void setCategories(List<Category> data) {
        putJsonObject(KEY_CATEGORY_DATA, data);
    }

    public static List<Category> getCategories() {
        return new Gson().fromJson(getString(KEY_CATEGORY_DATA), new TypeToken<List<Category>>() {
        }.getType());
    }

    // Brand
    public static void setBrands(List<Brand> data) {
        putJsonObject(KEY_BRAND_DATA, data);
    }

    public static List<Brand> getBrands() {
        return new Gson().fromJson(getString(KEY_BRAND_DATA), new TypeToken<List<Brand>>() {
        }.getType());
    }

    // Token
    public static void setToken(Token data) {
        putJsonObject(USER_TOKEN, data);
    }

    public static Token getToken() {
        return new Gson().fromJson(getString(USER_TOKEN), new TypeToken<Token>() {
        }.getType());
    }

    ////////////////////////////////////////////////


}