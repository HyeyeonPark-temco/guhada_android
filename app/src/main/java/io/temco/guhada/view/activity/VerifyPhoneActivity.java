package io.temco.guhada.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.server.LoginServer;
import io.temco.guhada.databinding.ActivityVerifyphoneBinding;
import io.temco.guhada.view.activity.base.BindActivity;

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
        LoginServer.getVerifyPhoneToken((success, o) -> {
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
        mBinding.webviewVerifyphone.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        WebViewClient client = new WebViewClient() {
            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                super.onReceivedClientCertRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = Uri.parse(request.getUrl().toString());
                Set<String> params = uri.getQueryParameterNames();
                Map<String, String> map = new HashMap<>();
                for (String key : params) {
                    String value = uri.getQueryParameter(key);
                    if (value != null) {
                        map.put(key, value);
                    }
                }

                /**
                 *  <휴대폰번호> 는 업체에 따로 신청해야해서 추후에 필드값 넘어올 예정 (05.24)
                 */
                if (map.get("sName") != null) {
                    mBinding.webviewVerifyphone.setVisibility(View.GONE);

                    String name = map.get("sName");
                    String phoneNumber = map.get("sMobileNo");
                    String authType = map.get("sAuthType");
                    String gender = map.get("sGender");
                    String nationalInfo = map.get("sNationalInfo");

                    String di = map.get("sDueInfo");
                    String mobileCo = map.get("sMobileCo");
                    String requestNumber = map.get("sRequestNumber");
                    String responseNumber = map.get("sResponseNumber");

                    Intent intent = getIntent();
                    intent.putExtra("name", name);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("di", di);
                    setResult(RESULT_OK, intent);
                    finish();

                    return false;
                } else {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        };

        mBinding.webviewVerifyphone.getSettings().setJavaScriptEnabled(true);
        mBinding.webviewVerifyphone.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mBinding.webviewVerifyphone.getSettings().setSupportMultipleWindows(true);
        mBinding.webviewVerifyphone.setWebViewClient(client);

        mBinding.webviewVerifyphone.loadUrl(URL);
    }


}

