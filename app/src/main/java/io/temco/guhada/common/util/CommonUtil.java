package io.temco.guhada.common.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

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

    // SnackBar
    public static void showSnackBar(View parentView, String message, int bgColor, int marginTop) {
        Snackbar snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(2500);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);

        View view = snackbar.getView();
        view.setBackgroundColor(bgColor);

        TextView snackBarTextView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            snackBarTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }else {
            snackBarTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.setMargins(0, marginTop, 0, 0);
        view.setLayoutParams(layoutParams);

        snackbar.show();
    }
}