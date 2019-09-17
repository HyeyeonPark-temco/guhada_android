package io.temco.guhada.view.activity

import android.os.Build
import android.text.TextUtils
import android.webkit.*
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.databinding.ActivityCustomwebviewBinding
import io.temco.guhada.view.activity.base.BindActivity

class CustomWebViewActivity  : BindActivity<ActivityCustomwebviewBinding>() {

    private var title = ""
    private var url = ""

    ////////////////////////////////////////////////////////////////////////////////
    override fun getBaseTag(): String = ImageGetActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_customwebview
    override fun getViewType(): Type.View = Type.View.CUSTOM_WEBVIEW

    override fun init() {
        title = intent?.extras?.getString("title") ?: ""
        url = intent?.extras?.getString("url") ?: ""
        if(TextUtils.isEmpty(url)) finish()
        mBinding.title = title
        mBinding.setOnClickBackButton { finish() }

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
        mBinding.webview.webViewClient = object  : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                //return super.shouldOverrideUrlLoading(view, request)
                view?.webChromeClient = WebChromeClient()
                if(CustomLog.flag) CustomLog.L("CommunityDetailContentsFragment",request?.url!!.toString())
                return true
            }
        }
        mBinding.webview.loadUrl(url)

    }

    override fun onBackPressed() {
        if(mBinding.webview.canGoBack()){
            mBinding.webview.goBack()
        }else{
            super.onBackPressed()
        }
    }

////////////////////////////////////////////////////////////////////////////////

}
