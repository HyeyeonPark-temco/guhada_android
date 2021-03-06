package io.temco.guhada.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.databinding.ActivitySearchzipwebviewBinding
import io.temco.guhada.view.activity.base.BindActivity
import kotlinx.android.synthetic.main.activity_searchzipwebview.*

/**
 * 우편번호 검색 Activity
 * @author Hyeyeon Park
 */
class SearchZipWebViewActivity : BindActivity<ActivitySearchzipwebviewBinding>() {
    private val JS_INTERFACE_NAME = "Android"

    private lateinit var handler: android.os.Handler

    override fun getBaseTag(): String = SearchZipWebViewActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_searchzipwebview

    override fun getViewType(): Type.View = Type.View.SEARCH_ZIP_WEBVIEW

    override fun init() {
        initWebView()
        mBinding.includeSearchzipwebviewHeader.title = ""
        mBinding.includeSearchzipwebviewHeader.setOnClickCloseButton {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        val settings = webview_searchzipwebview.settings
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true

        webview_searchzipwebview.addJavascriptInterface(AndroidBridge(), JS_INTERFACE_NAME)
        webview_searchzipwebview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // javascript:execDaumPostcode(); : sdk22 미작동
//                webview_searchzipwebview.loadUrl("javascript:execDaumPostcode();")
            }
        }

        if (Type.isTempServer) webview_searchzipwebview.loadUrl(resources.getString(R.string.searchzip_loadurl_prod))
        else webview_searchzipwebview.loadUrl(resources.getString(R.string.searchzip_loadurl_dev))
    }

    inner class AndroidBridge {
        @JavascriptInterface
        fun processData(zip: String, address: String) {
            intent.putExtra("zip", zip)
            intent.putExtra("address", address)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

}