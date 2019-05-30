package io.temco.guhada.common.util;

import android.content.Context;
import android.content.res.Resources;
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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.temco.guhada.BuildConfig;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.data.model.Category;

public class CommonUtil {

    // -------- LOCAL VALUE --------
    private static Category mCurrentCategory;
    // -----------------------------

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

    /**
     * Show snack bar
     * <p>
     * Fix background-color and margin-top of the snack bar
     *
     * @param parentView
     * @param message
     * @author Hyeyeon Park
     */
    public static void showSnackBar(View parentView, String message) {
        Snackbar snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(2500);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);

        Resources resources = BaseApplication.getInstance().getResources();
        View view = snackbar.getView();
        view.setBackgroundColor(resources.getColor(R.color.colorPrimary));

        TextView snackBarTextView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snackBarTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            snackBarTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.setMargins(0, (int) resources.getDimension(R.dimen.height_header), 0, 0);
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
    // VALIDATION
    ////////////////////////////////////////////////

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

    ////////////////////////////////////////////////
    // CATEGORY
    ////////////////////////////////////////////////

    public static Category getCategory(int[] hierarchies) {
        List<Category> data = Preferences.getCategories();
        if (data == null) return null;
        mCurrentCategory = null;
        getCategory(hierarchies[hierarchies.length - 1], data);
        return mCurrentCategory;
    }

    private static void getCategory(int id, List<Category> categories) {
        if (categories != null && categories.size() > 0) {
            for (Category c : categories) {
                if (c.id == id) {
                    mCurrentCategory = c;
                    break;
                } else {
                    getCategory(id, c.children);
                }
            }
        }
    }

    public static Category createAllCategoryData(Context context, int id, int[] hierarchies) {
        Category all = new Category();
        all.type = Type.Category.ALL;
        all.id = id;
        all.name = context.getString(R.string.category_all);
        all.hierarchies = hierarchies;
        return all;
    }

    ////////////////////////////////////////////////
}