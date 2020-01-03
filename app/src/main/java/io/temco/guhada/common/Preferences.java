package io.temco.guhada.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CommonUtilKotlin;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.common.util.TextUtil;
import io.temco.guhada.common.util.ToastUtil;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.Token;

public class Preferences {

    // -------- LOCAL VALUE --------
    private static Context mApplicationContext;
    // Key
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_AUTO_LOGIN = "auto_login";
    private static final String KEY_CATEGORY_DATA = "category_data";
    private static final String KEY_BRAND_DATA = "brand_data";

    private static final String KEY_USER_TOKEN = "token";
    private static final String KEY_IS_ID_SAVED = "is_id_saved";
    private static final String KEY_SAVED_ID = "saved_id";
    private static final String KEY_USERINFO_PASSWORD_CONFIRM = "userinfo_password_confirm";
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
        putJsonObject(KEY_USER_TOKEN, data);
    }

    public static Token getToken() {
        return new Gson().fromJson(getString(KEY_USER_TOKEN), new TypeToken<Token>() {
        }.getType());
    }

    public static void clearToken(boolean isShowToast, BaseApplication application) {
        Preferences.setPasswordConfirm(false);
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            return;
        }

        Token token = Preferences.getToken();
        if(token != null && token.getAccessToken() != null && application.getFcmToken() != null){
            String t = token.getAccessToken()+"";
            if(CustomLog.getFlag())CustomLog.L("Preferences clearToken","t",t);
            if(CustomLog.getFlag())CustomLog.L("Preferences clearToken","getFcmToken",application.getFcmToken());
            CommonUtilKotlin.deleteDevice(t, application.getFcmToken());
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.remove(KEY_USER_TOKEN);
        editor.apply();
        if(isShowToast){
            ToastUtil.showMessage("로그아웃 되었습니다.");
            CommonUtil.debug("FINISH CLEAR TOKEN");
        }
    }


    /**
     * @author park jungho
     * 토큰 클리어시 마이페이지 갱신을 위한 값 설정
     * 사이드 메뉴-로그아웃 시 호출
     *
     * @param isShowToast
     * @param application
     */
    public static void clearTokenMain(boolean isShowToast, BaseApplication application) {
        application.setInitUserMaypage(true);
        Preferences.setPasswordConfirm(false);
        SharedPreferences pref = getPreferences();
        if (pref == null) {
            return;
        }

        Token token = Preferences.getToken();
        if(token != null && token.getAccessToken() != null && application.getFcmToken() != null){
            String t = token.getAccessToken()+"";
            if(CustomLog.getFlag())CustomLog.L("Preferences clearToken clearTokenMain","t",t);
            if(CustomLog.getFlag())CustomLog.L("Preferences clearToken clearTokenMain","getFcmToken",application.getFcmToken());
            CommonUtilKotlin.deleteDevice(t, application.getFcmToken());
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.remove(KEY_USER_TOKEN);
        editor.apply();
        if(isShowToast){
            ToastUtil.showMessage("로그아웃 되었습니다.");
            CommonUtil.debug("FINISH CLEAR TOKEN");
        }
    }

    // is id saved
    public static void setIsIdSaved(boolean isSaved) {
        putBoolean(KEY_IS_ID_SAVED, isSaved);
    }

    public static boolean isIdSaved() {
        return getBoolean(KEY_IS_ID_SAVED, false);
    }

    // Id
    public static void setSavedId(String id) {
        putString(KEY_SAVED_ID, id);
    }

    public static String getSavedId() {
        return getString(KEY_SAVED_ID);
    }


    // is id saved
    public static void setMainBannerViewDialog(String key,String date) {
        putString(key, date);
    }

    public static String getMainBannerViewDialog(String key) {
        return getString(key);
    }

    // Language
    public static void setLanguage(Type.Language type) {
        putString(KEY_LANGUAGE, Type.Language.get(type));
    }

    public static String getLanguage() {
        String lang = getString(KEY_LANGUAGE);
        if (TextUtils.isEmpty(lang)) {
            return Type.Language.get(Type.Language.KOREA);
        }
        return lang;
    }



    // is id saved
    public static void setPasswordConfirm(boolean flag) {
        putBoolean(KEY_USERINFO_PASSWORD_CONFIRM, flag);
    }
    public static boolean getPasswordConfirm() {
        return getBoolean(KEY_USERINFO_PASSWORD_CONFIRM, false);
    }

    ////////////////////////////////////////////////

}