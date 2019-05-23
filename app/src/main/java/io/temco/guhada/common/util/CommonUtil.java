package io.temco.guhada.common.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.temco.guhada.BuildConfig;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.listener.OnTimerListener;

public class CommonUtil {
    private static TimerTask mTimerTask;
    private static String timerSecond;
    private static String timerMinute;

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

    // SnackBar
    public static void showSnackBar(View parentView, String message, int bgColor, int marginTop) {
        Snackbar snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(2500);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);

        View view = snackbar.getView();
        view.setBackgroundColor(bgColor);

        TextView snackBarTextView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snackBarTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            snackBarTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.setMargins(0, marginTop, 0, 0);
        view.setLayoutParams(layoutParams);

        snackbar.show();
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

    private static void initTimer(OnTimerListener listener) {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                int second = Integer.parseInt(timerSecond);
                if (second > 0) {
                    if (second < 10) {
                        timerSecond = "0" + (second - 1);
                    } else {
                        timerSecond = String.valueOf(second - 1);
                    }

                    listener.changeSecond(timerSecond);
                } else {
                    int minute = Integer.parseInt(timerMinute);
                    if (minute > 0) {
                        timerMinute = "0" + (minute - 1);
                        timerSecond = "59";
                        listener.changeMinute(timerMinute);
                        listener.changeSecond(timerSecond);
                    } else {
                        timerMinute = "00";
                        timerSecond = "00";

                        listener.changeMinute(timerMinute);
                        listener.changeSecond(timerSecond);
                        mTimerTask.cancel();
                    }
                }

                listener.notifyMinuteAndSecond();
            }
        };
    }

    public static void startVerifyNumberTimer(String initialSecond, String initialMinute, OnTimerListener listener) {
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }

        timerSecond = initialSecond;
        timerMinute = initialMinute;
        initTimer(listener);

        // 1초마다 반복
        Timer timer = new Timer();
        timer.schedule(mTimerTask, 0, 1000);
    }

    public static void stopTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
    }

    public static boolean validatePassword(String password) {
        int length = password.length();
        if (length >= 8 && length <= 15) {
            final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#$%^&*])(?=.*[0-9!@#$%^&*]).{8,15}$";
            Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
            Matcher matcher = pattern.matcher(password);

            return matcher.matches();
        } else {
            return false;
        }
    }

    public static boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validateNumber(String text) {
        String NUMBER_REGEX = "^[0-9]*$";
        return text.trim().matches(NUMBER_REGEX);
    }
}