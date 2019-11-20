package io.temco.guhada.view.activity

import android.os.Build
import android.text.TextUtils
import android.view.View
import android.webkit.*
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.databinding.ActivityCustomwebviewBinding
import io.temco.guhada.view.activity.base.BindActivity





class CustomWebViewActivity  : BindActivity<ActivityCustomwebviewBinding>() {
    private val JS_INTERFACE_NAME = "Android"

    private var title = ""
    private var url = ""
    private var param = ""

    ////////////////////////////////////////////////////////////////////////////////
    override fun getBaseTag(): String = ImageGetActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_customwebview
    override fun getViewType(): Type.View = Type.View.CUSTOM_WEBVIEW

    override fun init() {
        title = intent?.extras?.getString("title") ?: ""
        url = intent?.extras?.getString("url") ?: ""
        param = intent?.extras?.getString("param") ?: ""
        if(CustomLog.flag) CustomLog.L("CommunityDetailContentsFragment",url)
        if(TextUtils.isEmpty(url)) finish()
        mBinding.title = title
        mBinding.setOnClickBackButton { finish() }

        if(CustomLog.flag)CustomLog.L("CustomWebViewActivity","url",url)
        if(CustomLog.flag)CustomLog.L("CustomWebViewActivity","title",title)
        if(CustomLog.flag)CustomLog.L("CustomWebViewActivity","param",param)

        mBinding.webview.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
            loadWithOverviewMode = true
            useWideViewPort = true
            allowFileAccess = true
            pluginState = WebSettings.PluginState.ON
            pluginState = WebSettings.PluginState.ON_DEMAND
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            setAppCacheEnabled(true)
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            if (Build.VERSION.SDK_INT >= 26) safeBrowsingEnabled = false
        }
        mBinding.webview.addJavascriptInterface(AndroidBridge(), JS_INTERFACE_NAME)
        mBinding.webview.webViewClient = object  : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                //return super.shouldOverrideUrlLoading(view, request)
                view?.webChromeClient = WebChromeClient()
                if(CustomLog.flag) CustomLog.L("CommunityDetailContentsFragment",request?.url!!.toString())
                return true
            }
        }
        mBinding.webview.loadUrl(url)
        mBinding.buttonWebview.visibility = View.GONE
        if(!TextUtils.isEmpty(param)){
            mBinding.buttonWebview.visibility = View.VISIBLE
            mBinding.buttonWebview.setOnClickListener {
                if(!TextUtils.isEmpty(param)){
                    CommonUtilKotlin.moveEventPage(this,param,"",false,true)
                }
            }
        }
    }

    override fun onBackPressed() {
        if(mBinding.webview.canGoBack()){
            mBinding.webview.goBack()
        }else{
            super.onBackPressed()
        }
    }

    inner class AndroidBridge {
        @JavascriptInterface
        fun processData(arg1: String, arg2: String) {
            if(CustomLog.flag)CustomLog.L("CustomWebViewActivity","AndroidBridge processData",arg1)
            when(arg1){
                "join"->{
                    CommonUtil.startLoginPage(this@CustomWebViewActivity)
                    finish()
                }
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////

}
