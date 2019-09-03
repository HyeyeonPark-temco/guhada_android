package io.temco.guhada.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.temco.guhada.BuildConfig;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.Token;
import io.temco.guhada.view.activity.CartActivity;
import io.temco.guhada.view.activity.ImageGetActivity;
import io.temco.guhada.view.activity.LoginActivity;
import io.temco.guhada.view.activity.ProductFilterListActivity;
import io.temco.guhada.view.activity.ProductFragmentDetailActivity;
import io.temco.guhada.view.activity.ReportActivity;
import io.temco.guhada.view.activity.ReviewPointDialogActivity;
import io.temco.guhada.view.activity.SearchWordActivity;
import io.temco.guhada.view.activity.SideMenuActivity;

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

    // Delay
    public static void delayRunnable(Runnable run) {
        delayRunnable(run, 100);
    }

    public static void delayRunnable(Runnable run, long delayMillis) {
        if (run != null) {
            new Handler(Looper.getMainLooper()).postDelayed(run, delayMillis);
        }
    }

    // Country Code
    public static String getSystemCountryCode() {
        return BaseApplication.getInstance().getApplicationContext().getResources().getConfiguration().locale.getCountry();
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

    /**
     * Convert TimeStamp(ms) to Date String (Format: yyyy.MM.dd)
     *
     * @param timeStamp type: Long
     * @return dateString type: String
     * @author Hyeyeon Park
     */
    public static String convertTimeStampToDate(long timeStamp) {
        DateTime dateTime = new DateTime(timeStamp);
//        dateTime.withZone(DateTimeZone.forID("Asia/Seoul"));
        return dateTime.toString("yyyy.MM.dd");
    }


    /**
     * Conver Date String to TimeStamp(ms)
     *
     * @param dateString type: String
     * @return timeStamp type : Long
     * @author Hyeyeon Park
     */
    public static Long convertDateToTimeStamp(String dateString, String operator) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy" + operator + "MM" + operator + "dd");
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateString);
        return dateTime.getMillis();
    }


    ////////////////////////////////////////////////
    // KEYBOARD
    ////////////////////////////////////////////////

    public static void showKeyboard(Context context, View view) {
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
            final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,15}.$";
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
        getCategory(hierarchies[hierarchies.length - 1], data, null);
        return mCurrentCategory;
    }

    private static void getCategory(int id, List<Category> categories, Category parent) {
        if (categories != null && categories.size() > 0) {
            for (Category c : categories) {
                if (c.id == id) {
                    if (c.children != null) {
                        mCurrentCategory = c;
                        mCurrentCategory.selectId = -1;
                    } else {
                        mCurrentCategory = parent;
                        mCurrentCategory.selectId = id;
                    }
                    break;
                } else {
                    getCategory(id, c.children, c);
                }
            }
        }
    }

    public static Category createAllCategoryData(String title, String depth, int id, int[] hierarchies) {
        Category all = new Category();
        all.type = Type.Category.ALL;
        all.id = id;
        // all.name = title;
        all.title = title;
        all.fullDepthName = depth;
        all.hierarchies = hierarchies;
        return all;
    }

    public static boolean checkSelectCategoryData(Category parent, Category child) {
        boolean isSelect = false;
        for (Category c : parent.children) {
            if (c.id == child.id) {
                c.isSelected = child.isSelected;
            }
            isSelect = c.isSelected;
        }
        return isSelect;
    }


    /**
     * @param act
     * @author park jungho
     * 검색어 입력 화면
     */
    public static void startSearchWordActivity(Activity act, String text, boolean isNewAct) {
        Intent intent = new Intent(act, SearchWordActivity.class);
        if(text!=null && !"".equals(text))  intent.putExtra("searchWord",text);
        else intent.putExtra("searchWord","");
        intent.putExtra("isNewActivity",isNewAct);
        act.startActivityForResult(intent, Flag.RequestCode.SEARCH_WORD);
    }


    /**
     * @param act
     * @author park jungho
     * 장바구니 화면
     */
    public static void startCartActivity(Activity act) {
        Intent intent = new Intent(act, CartActivity.class);
        act.startActivityForResult(intent, Flag.RequestCode.BASE);
    }

    /**
     * @param act
     * @author park jungho
     * 사이드 메뉴 화면
     */
    public static void startMenuActivity(Activity act, int res) {
        Intent intent = new Intent(act, SideMenuActivity.class);
        act.startActivityForResult(intent, res);
    }

    /**
     * @param act
     * @author park jungho
     * 상품 리스트 (카테고리, 브랜드)
     */
    public static void startProductActivity(Activity act, Long dealId) {
        Intent intent = new Intent(act, ProductFragmentDetailActivity.class);
        intent.putExtra("dealId", dealId);
        act.startActivityForResult(intent, Flag.RequestCode.PRODUCT_DETAIL);
    }

    public static void startCategoryScreen(Activity act,Type.Category type, int[] hierarchies, boolean isFinish) {
        Intent intent = new Intent(act, ProductFilterListActivity.class);
        intent.putExtra("type", Type.ProductListViewType.CATEGORY);
        intent.putExtra("hierarchies", hierarchies);
        intent.putExtra("categoryType", type);
        act.startActivityForResult(intent,Flag.RequestCode.BASE);
        if(isFinish){
            act.overridePendingTransition(0,0);
            act.finish();
            act.overridePendingTransition(0,0);
        }
    }

    public static void startBrandScreen(Activity act, Brand brand, boolean isFinish){
        Intent intent = new Intent(act, ProductFilterListActivity.class);
        intent.putExtra("type", Type.ProductListViewType.BRAND);
        intent.putExtra("brand", brand);
        act.startActivityForResult(intent,Flag.RequestCode.BASE);
        if(isFinish){
            act.setResult(Activity.RESULT_FIRST_USER);
            act.overridePendingTransition(0,0);
            act.finish();
        }
    }

    public static void startImageGallery(Activity act){
        Intent intent = new Intent(act, ImageGetActivity.class);
        act.startActivityForResult(intent,Flag.RequestCode.IMAGE_GALLERY);
    }


    /**
     *
     * @param act activity
     * @param type - 0 : 구매확정, 1 : 리뷰작성, 2 : 사이즈 등록
     */
    public static void startPointDialogActivity(Activity act, int type){
        Intent intent = new Intent(act, ReviewPointDialogActivity.class);
        intent.putExtra("type", type);
        act.startActivityForResult(intent,Flag.RequestCode.POINT_RESULT_DIALOG);
    }

    public static Boolean checkToken() {
        Token token = Preferences.getToken();
        if(token == null) return false;
        else{
            int current = (int) (System.currentTimeMillis() / 1000L);
            try{
                Claim exp = new JWT(token.getAccessToken()).getClaim("exp");
                if (exp.asInt() > current) {
                    return true;
                } else {
                    Preferences.clearToken();
                    return false;
                }
            }catch (Exception e){
                Preferences.clearToken();
                return false;
            }
        }
    }



    public static long checkUserId() {
        Token token = Preferences.getToken();
        if(token == null) return -1;
        else{
            int current = (int) (System.currentTimeMillis() / 1000L);
            try{
                Claim exp = new JWT(token.getAccessToken()).getClaim("exp");
                if (exp.asInt() > current) {
                    String id =  new JWT(token.getAccessToken()).getClaim("userId").asString();
                    return Long.parseLong(id);
                } else {
                    Preferences.clearToken();
                    return -1;
                }
            }catch (Exception e){
                if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.E(e);
                Preferences.clearToken();
                return -1;
            }
        }
    }

    public static String getUserIp(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("getUserIp", "***** IP="+ inetAddress.getHostAddress());
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.E(ex);
            return "111.111.111.111";
        }
        return null;
    }


    public static void moveLoginPage(Activity activity){
        activity.startActivityForResult(new Intent(activity, LoginActivity.class), Flag.RequestCode.LOGIN);
    }


    /**
     *
     * @param activity current Activity notNull
     * @param type report type notNull
     * @param data report data notNull
     * @param data2 nullable report communityDetail (type:0 셀러이름, type:3 게시글상세)
     */
    public static void startReportActivity(Activity activity, int type, Serializable data, @Nullable Serializable data2){
        if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("startReportActivity","type",type);
        Intent intent = new Intent(activity, ReportActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("data", data);
        if(data2!=null) intent.putExtra("data2", data2);
        activity.startActivityForResult(intent,Flag.RequestCode.REPORT);
    }





}