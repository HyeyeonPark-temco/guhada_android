package io.temco.guhada.view.activity

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.databinding.ActivitySearchzipwebviewBinding
import io.temco.guhada.view.activity.base.BindActivity
import kotlinx.android.synthetic.main.activity_searchzipwebview.*


class SearchZipWebViewActivity : BindActivity<ActivitySearchzipwebviewBinding>() {
    private lateinit var handler: android.os.Handler

    override fun getBaseTag(): String = SearchZipWebViewActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_searchzipwebview

    override fun getViewType(): Type.View = Type.View.SEARCH_ZIP_WEBVIEW

    override fun init() {


        handler = android.os.Handler()

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        val settings = webview_searchzipwebview.settings
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true

        webview_searchzipwebview.webChromeClient = WebChromeClient()
        webview_searchzipwebview.loadUrl("http://dmaps.daum.net/map_js_init/postcode.v2.js?autoload=false")
    }

    open inner class AndroidBridge {
        @JavascriptInterface
        open fun setAddress(arg1: String, arg2: String, arg3: String) {
            handler.post {
                initWebView()
            }
        }
    }


}