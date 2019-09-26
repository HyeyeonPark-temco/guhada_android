package io.temco.guhada.view.fragment.community.detail

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.webkit.*
import io.temco.guhada.R
import io.temco.guhada.common.Info
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.DateUtil
import io.temco.guhada.data.model.community.CommunityMobileDetail
import io.temco.guhada.data.model.community.CommunityMobileDetailImage
import io.temco.guhada.data.model.community.CommunityMobileDetailText
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.databinding.FragmentCommunityDetailContentBinding
import io.temco.guhada.view.adapter.CommunityMobileDetailAdapter
import io.temco.guhada.view.fragment.base.BaseFragment


class CommunityDetailContentsFragment(val viewModel : CommunityDetailViewModel) : BaseFragment<FragmentCommunityDetailContentBinding>() {

    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String = CommunityDetailContentsFragment::class.java!!.simpleName
    override fun getLayoutId(): Int = R.layout.fragment_community_detail_content
    override fun init() {
        if (CustomLog.flag) CustomLog.L("CommunityDetailContentsFragment", "init ---------------------")
        mBinding.viewModel = viewModel
        viewModel.communityDetailClientPlatformWeb.set(!"MOBILE".equals(viewModel.communityDetail.value!!.guhadaClientPlatform,true))

        mBinding.setClickBookmarkListener {
            viewModel.onClickBookMark()
        }
        setDetailView()
        viewModel.getBookMark()
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////


    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    fun setDetailView(){
        mBinding.item = viewModel.communityDetail.value!!
        if(CustomLog.flag)CustomLog.L("CommunityDetailContentsFragment","communityDetail",viewModel.communityDetail.value.toString())
        mBinding.recyclerviewCommunitydetailList.adapter = null

        var header = ""
        if(viewModel.communityDetail.value!!.categoryFilterId > 0){
            loop1@ for (filter in viewModel.info.communityCategorySub.categoryFilterList){
                if(filter.id == viewModel.communityDetail.value!!.categoryFilterId){
                    header = "["+filter.name+"] "
                    break@loop1
                }
            }
        }

        /*if(CustomLog.flag)CustomLog.L("CommunityDetailContentsFragment","getNowDateDiffMinute",DateUtil.getNowDateDiffMinute(viewModel.communityDetail.value?.createdTimestamp ?: 0))
        if(CustomLog.flag)CustomLog.L("CommunityDetailContentsFragment","getNowDateDiffHour",DateUtil.getNowDateDiffHour(viewModel.communityDetail.value?.createdTimestamp ?: 0))
        if(CustomLog.flag)CustomLog.L("CommunityDetailContentsFragment","getNowDateDiffDay",DateUtil.getNowDateDiffDay(viewModel.communityDetail.value?.createdTimestamp ?: 0))*/
        if(DateUtil.getNowDateDiffDay(viewModel.communityDetail.value?.createdTimestamp ?: 0) < 1){
            CommonViewUtil.setTextViewImageTextEnd(context!!, R.drawable.drawable_icon_new,
                    (header+viewModel.communityDetail.value!!.title!!+" "),mBinding.textviewCommunitydetailcontentsTitle)
        }else{
            mBinding.textviewCommunitydetailcontentsTitle.text = (header+viewModel.communityDetail.value!!.title!!)
        }

        mBinding.setClickShareListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, Info.SHARE_COMMUNITY_URL+viewModel.communityDetail.value!!.id)
                type = "text/plain"
            }
            (context as Activity).startActivity(Intent.createChooser(sendIntent, "공유"))
        }


        if(viewModel.communityDetailClientPlatformWeb.get()){
            val data = StringBuilder()
            data.append("<style>img{display: inline;height: auto;max-width: 100%;}" +
                    "body{word-break: break-all; word-break: break-word}" +
                    "h1{font-size:large; word-break: break-all; word-break: break-word}" +
                    "h2{font-size:medium; word-break: break-all; word-break: break-word}</style>")
            data.append(viewModel.communityDetail.value!!.contents!!.replace("\"//www","\"https://www"))
            mBinding.webviewCommunitydetailContent.settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                setSupportMultipleWindows(true)
                allowFileAccess = true
                pluginState = WebSettings.PluginState.ON
                pluginState = WebSettings.PluginState.ON_DEMAND
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                loadsImagesAutomatically = true
                defaultFontSize = context?.resources?.getDimension(R.dimen.text_4)?.toInt() ?: 20
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
            mBinding.webviewCommunitydetailContent.loadData(data.toString(), "text/html", null)
            /*mBinding.webviewCommunitydetailContent.settings.apply {
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
            data.append(viewModel.communityDetail.value!!.contents!!.replace("\"//www","\"https://www"))
            data.append("</body></HTML>")
            mBinding.webviewCommunitydetailContent.loadDataWithBaseURL("file:///android_asset/", data.toString(),"text/html; video/mpeg", "utf-8", null)*/
        }else{
            if (mBinding.recyclerviewCommunitydetailList.adapter == null) {
                var mobileList = arrayListOf<CommunityMobileDetail>()
                if(!viewModel.communityDetail.value!!.bbsImageList.isNullOrEmpty()){
                    for (value in viewModel.communityDetail.value!!.bbsImageList){
                        var image = CommunityMobileDetailImage()
                        image.image = value
                        mobileList.add(image)
                    }
                }
                if(!TextUtils.isEmpty(viewModel.communityDetail.value!!.contents)){
                    var text = CommunityMobileDetailText()
                    text.text = viewModel.communityDetail.value!!.contents!!
                    mobileList.add(text)
                }
                viewModel.mobileContentsList = mobileList
                mBinding.recyclerviewCommunitydetailList.adapter = CommunityMobileDetailAdapter().apply { mList = viewModel.mobileContentsList }
            } else {
                (mBinding.recyclerviewCommunitydetailList.adapter as CommunityMobileDetailAdapter).setItems(viewModel.mobileContentsList)
            }
        }
        mBinding.executePendingBindings()
    }


    ////////////////////////////////////////////////




}
