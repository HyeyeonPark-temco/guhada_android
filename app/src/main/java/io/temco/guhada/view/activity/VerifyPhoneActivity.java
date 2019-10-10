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

import com.kakao.usermgmt.response.model.Gender;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.data.model.Verification;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.server.UserServer;
import io.temco.guhada.databinding.ActivityVerifyphoneBinding;
import io.temco.guhada.view.activity.base.BindActivity;

/**
 * 본인인증(PASS)
 *
 * @author Hyeyeon Park
 */
public class VerifyPhoneActivity extends BindActivity<ActivityVerifyphoneBinding> {

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
        UserServer.getVerifyPhoneToken((success, o) -> {
            if (success) {
                BaseModel model = (BaseModel) o;
                String token = (String) model.data;
                setWebView(token);
            } else {
                String message = (String) o;
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView(String token) {
        String URL = "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb?EncodeData=" + token + "&m=checkplusSerivce";
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        WebViewClient client = new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);

                String comparedUrl = url.split("\\?")[0];
                String resultUrl1 = getResources().getString(R.string.verifyphone_result_url_prod1);    // BASE_URL: www.guhada.com
                String resultUrl2 = getResources().getString(R.string.verifyphone_result_url_prod2);    // BASE_URL: web.guhada.com
                String resultUrl3 = getResources().getString(R.string.verifyphone_result_url_dev);      // BASE_URL: dev.guhada.com
                String resultUrl4 = getResources().getString(R.string.verifyphone_result_url_qa);       // BASE_URL: qa.guhada.com
                String resultUrl5 = getResources().getString(R.string.verifyphone_result_url_stg);      // BASE_URL: stg.guhada.com

                if (comparedUrl.equals(resultUrl1) || comparedUrl.equals(resultUrl2) || comparedUrl.equals(resultUrl3) || comparedUrl.equals(resultUrl4) || comparedUrl.equals(resultUrl5)) {
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
    }

    private void getVerifyInfo(String url) {
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
        String nationalInfo = map.get("sNationalInfo");
        String mobileCo = map.get("sMobileCo");
        String requestNumber = map.get("sRequestNumber");
        String responseNumber = map.get("sResponseNumber");

        Intent intent = getIntent();
        intent.putExtra("name", name);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("di", di);
        intent.putExtra("gender", gender.equals(Verification.Gender.FEMALE.getCode()) ? Verification.Gender.FEMALE.getLabel() : Verification.Gender.MALE.getLabel());
        setResult(RESULT_OK, intent);
        finish();
    }

}

