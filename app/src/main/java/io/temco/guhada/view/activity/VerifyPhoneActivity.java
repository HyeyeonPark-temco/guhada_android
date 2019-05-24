package io.temco.guhada.view.activity;

import android.annotation.SuppressLint;
import android.webkit.ClientCertRequest;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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
                if (request.getUrl().toString().equals("https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb?m=checkplusSerivce_resultSend")) {
                    setResult(RESULT_OK);
                    finish();
                }
                return super.shouldOverrideUrlLoading(view, request);
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

