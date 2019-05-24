package io.temco.guhada.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
                    mBinding.webviewVerifyphone.setVisibility(View.GONE);
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (url.equals("http://13.209.10.68/phoneCertification/success")) {
                    mBinding.webviewVerifyphone.evaluateJavascript("(function(){return window.document.body.outerHTML})();",
                            html -> {
                                String s = StringEscapeUtils.unescapeJava(html);
                                Document document = Jsoup.parse(s);
                                Element table = document.select("table").first();
                                Elements trs = table.select("tr");
                                String name = trs.get(4).select("td").get(1).text();
                                String birth = trs.get(7).select("td").get(1).text();
                                String phoneNumber = trs.get(10).select("td").get(1).text();

                                /**
                                 *  <휴대폰번호> 는 업체에 따로 신청해야해서 추후에 필드값 넘어올 예정 (05.24)
                                 */

                                Log.e("본인인증 완료", "name: " + name + "; birth: " + birth + "; phoneNumber: " + phoneNumber);
                                Intent intent = getIntent();
                                intent.putExtra("name", name);
                                intent.putExtra("phoneNumber", phoneNumber);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                    );
                }
            }
        };

        mBinding.webviewVerifyphone.getSettings().setJavaScriptEnabled(true);
        mBinding.webviewVerifyphone.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mBinding.webviewVerifyphone.getSettings().setSupportMultipleWindows(true);
        mBinding.webviewVerifyphone.setWebViewClient(client);

        mBinding.webviewVerifyphone.loadUrl(URL);
    }


}

