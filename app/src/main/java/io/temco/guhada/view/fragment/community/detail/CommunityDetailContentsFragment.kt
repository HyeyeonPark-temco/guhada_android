package io.temco.guhada.view.fragment.community.detail

import android.os.Build
import android.webkit.*
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.community.CommunityDetail
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.databinding.FragmentCommunityDetailContentBinding
import io.temco.guhada.view.fragment.base.BaseFragment



class CommunityDetailContentsFragment(val viewModel : CommunityDetailViewModel) : BaseFragment<FragmentCommunityDetailContentBinding>() {

    // -------- LOCAL VALUE --------
    private lateinit var mDetail : CommunityDetail
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String = CommunityDetailContentsFragment::class.java!!.getSimpleName()
    override fun getLayoutId(): Int = R.layout.fragment_community_detail_content
    override fun init() {
        mBinding.viewModel = viewModel
        mDetail = viewModel.communityDetail.value!!
        mBinding.item = mDetail
        setDetailView()
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////


    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    fun setDetailView(){
        var header = ""
        if(mDetail.categoryFilterId > 0){
            loop1@ for (filter in viewModel.info.communityCategorySub.categoryFilterList){
                if(filter.id == mDetail.categoryFilterId){
                    header = "["+filter.name+"] "
                    break@loop1
                }
            }
        }
        CommonViewUtil.setTextViewImageTextEnd(context!!, R.drawable.drawable_icon_new,
                (header+mDetail.title!!),mBinding.textviewCommunitydetailcontentsTitle)

        mBinding.webviewCommunitydetailContent.settings.apply {
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
        mBinding.webviewCommunitydetailContent.webViewClient = object  : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                //return super.shouldOverrideUrlLoading(view, request)
                view?.webChromeClient = WebChromeClient()
                if(CustomLog.flag)CustomLog.L("CommunityDetailContentsFragment",request?.url!!.toString())
                return true
            }
        }

        val data = StringBuilder()
        data.append("<HTML><HEAD><LINK href=\"community.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body>")
        data.append(mDetail.contents!!.replace("\"//www","\"https://www"))
        data.append("</body></HTML>")
        mBinding.webviewCommunitydetailContent.loadDataWithBaseURL("file:///android_asset/", data.toString(),"text/html; video/mpeg", "utf-8", null)
    }


    ////////////////////////////////////////////////




}
