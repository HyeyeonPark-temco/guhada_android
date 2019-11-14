package io.temco.guhada.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.temco.guhada.R;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.NetworkUtil;
import io.temco.guhada.common.util.ToastUtil;
import io.temco.guhada.data.model.Verification;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.server.UserServer;
import io.temco.guhada.databinding.ActivityVerifyphoneBinding;
import io.temco.guhada.view.activity.base.BindActivity;

/**
 * 본인인증(PASS)
 * [VP001] 토큰 호출 에러
 *
 * @author Hyeyeon Park
 */
public class VerifyPhoneActivity extends BindActivity<ActivityVerifyphoneBinding> {
    private String TOKEN_ERROR = "[VP001]";

    @Override
    protected String getBaseTag() {
        return VerifyPhoneActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_verifyphone;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.VERIFY_PHONE;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void init() {
        initHeader();
        getToken();
    }

    private void initHeader() {
        mBinding.includeVerifyphoneHeader.setOnClickCloseButton(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void getToken() {
        if (NetworkUtil.INSTANCE.isNetworkConnected(this)) {
            UserServer.getVerifyPhoneToken((success, o) -> {
                if (success && o instanceof BaseModel) {
                    BaseModel model = (BaseModel) o;
                    if (model.resultCode == Flag.ResultCode.SUCCESS) {
                        String token = (String) model.data;
                        setWebView(token);
                    } else {
                        ToastUtil.showMessage(TOKEN_ERROR + ((BaseModel) o).message);
                        finish();
                    }
                } else {
                    String message;
                    if (o instanceof String)
                        message = TOKEN_ERROR + (String) o;
                    else
                        message = TOKEN_ERROR + getString(R.string.common_message_servererror);

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            ToastUtil.showMessage(getString(R.string.common_message_networkerror));
            finish();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView(String token) {
        if (NetworkUtil.INSTANCE.isNetworkConnected(this)) {
            String URL = "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb?EncodeData=" + token + "&m=checkplusSerivce";
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);

            WebViewClient client = new WebViewClient() {
                @Override
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);

                    String comparedUrl = url.split("\\?")[0];
                    if (comparedUrl.contains("phone-certification-result")) {
                        mBinding.webviewVerifyphone.setVisibility(View.GONE);
                        getVerifyInfo(url);
                    }
                }
            };

            mBinding.webviewVerifyphone.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            mBinding.webviewVerifyphone.getSettings().setJavaScriptEnabled(true);
            mBinding.webviewVerifyphone.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            mBinding.webviewVerifyphone.getSettings().setSupportMultipleWindows(true);
            mBinding.webviewVerifyphone.setWebViewClient(client);
            mBinding.webviewVerifyphone.loadUrl(URL);
        } else {
            ToastUtil.showMessage(getString(R.string.common_message_networkerror));
            finish();
        }
    }

    private void getVerifyInfo(String url) {
        Log.e("본인인증", url);
        Uri uri = Uri.parse(url);
        Set<String> params = uri.getQueryParameterNames();
        Map<String, String> map = new HashMap<>();
        for (String key : params) {
            String value = uri.getQueryParameter(key);

            if (value != null) {
                map.put(key, value);
            }
        }

        String name = map.get("sName");
        String phoneNumber = map.get("sMobileNo");
        String di = map.get("sDueInfo");

        String authType = map.get("sAuthType");
        String gender = map.get("sGender");
        String birth = map.get("sBirthDate");
        String nationalInfo = map.get("sNationalInfo");
        String mobileCo = map.get("sMobileCo");
        String requestNumber = map.get("sRequestNumber");
        String responseNumber = map.get("sResponseNumber");

        Intent intent = getIntent();
        intent.putExtra("name", name);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("di", di);
        if (birth != null)
            intent.putExtra("birth", birth.substring(0, 4) + "-" + birth.substring(4, 6) + "-" + birth.substring(6, 8));
        if (gender != null)
            intent.putExtra("gender", gender.equals(Verification.Gender.FEMALE.getCode()) ? Verification.Gender.FEMALE.getLabel() : Verification.Gender.MALE.getLabel());
        setResult(RESULT_OK, intent);
        finish();
    }

}

