package io.temco.guhada.common.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import io.temco.guhada.BuildConfig;
import io.temco.guhada.common.Info;

public class CommonUtil {

    ////////////////////////////////////////////////
    // COMMON
    ////////////////////////////////////////////////

    // Debug
    public static void debug(String message) {
        debug(Info.APP_TAG, message);
    }

    public static void debug(String tag, String message) {
        if (BuildConfig.DEBUG &&
                !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message)) {
            Log.d(tag, message);
        }
    }

    // Status Bar Height
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    ////////////////////////////////////////////////
    // KEYBOARD
    ////////////////////////////////////////////////

    public void showKeyboard(Context context, View view) {
        if (context != null && view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        }
    }

    public static void hideKeyboard(Context context, View view) {
        if (context != null && view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    ////////////////////////////////////////////////
}