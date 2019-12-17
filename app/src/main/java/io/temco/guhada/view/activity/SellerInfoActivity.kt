package io.temco.guhada.view.activity

import android.os.Build
import android.text.TextUtils
import android.view.View
import android.webkit.*
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.ProductOrderType
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.seller.SellerStore
import io.temco.guhada.data.viewmodel.SellerInfoViewModel
import io.temco.guhada.databinding.ActivitySellerstoreBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.SellerInfoProductAdapter
import io.temco.guhada.view.fragment.ListBottomSheetFragment

/**
 * 셀러 스토어(셀러 회원 정보 화면) Activity
 * @author Hyeyeon Park
 * @since 2019.08.30
 */
class SellerInfoActivity : BindActivity<ActivitySellerstoreBinding>() {
    private lateinit var mViewModel: SellerInfoViewModel
    private lateinit var mFilterFragment: ListBottomSheetFragment
    private lateinit var mMenuFragment: ListBottomSheetFragment

    override fun getBaseTag(): String = SellerInfoActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_sellerstore

    override fun getViewType(): Type.View = Type.View.SELLER_INFO

    override fun onDestroy() {
        super.onDestroy()
        if (::mMenuFragment.isInitialized) mMenuFragment.dismiss()
        if (::mFilterFragment.isInitialized) mFilterFragment.dismiss()
    }

    override fun init() {
        initViewModel()
        initHeader()

        mBinding.imagebuttonSellerstoreMore.setOnClickListener {
            if (!::mMenuFragment.isInitialized) {
                mMenuFragment = ListBottomSheetFragment(this).apply {
                    mTitle = this@SellerInfoActivity.getString(R.string.common_more)
                    mList = mutableListOf(
                            this@SellerInfoActivity.getString(R.string.sellerstore_menu1),
                            this@SellerInfoActivity.getString(R.string.sellerstore_menu2),
                            this@SellerInfoActivity.getString(R.string.sellerstore_menu3),
                            this@SellerInfoActivity.getString(R.string.sellerstore_menu4),
                            this@SellerInfoActivity.getString(R.string.sellerstore_menu5),
                            this@SellerInfoActivity.getString(R.string.sellerstore_menu6))
                    mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                        override fun onItemClick(position: Int) {
                            when (position) {
                                SellerInfoViewModel.SellerInfoMore.SELLER_STORE.pos -> {
                                }
                                SellerInfoViewModel.SellerInfoMore.SELLER_INFO.pos -> {
                                }
                                SellerInfoViewModel.SellerInfoMore.REVIEW.pos -> {
                                    ToastUtil.showMessage(getString(R.string.common_message_ing))
                                }
                                SellerInfoViewModel.SellerInfoMore.BOARD.pos -> {
                                    ToastUtil.showMessage(getString(R.string.common_message_ing))
                                }
                                SellerInfoViewModel.SellerInfoMore.COMMENT.pos -> {
                                    ToastUtil.showMessage(getString(R.string.common_message_ing))
                                }
                                SellerInfoViewModel.SellerInfoMore.REPORT.pos -> {
                                    ToastUtil.showMessage(getString(R.string.common_message_ing))
                                }
                            }
                        }

                        override fun onClickClose() {
                            this@apply.dismiss()
                        }
                    }
                }
            }
            mMenuFragment.show(supportFragmentManager, baseTag)
        }
        mBinding.linearlayoutSellerstoreFilter.setOnClickListener {
            if (!::mFilterFragment.isInitialized) {
                mFilterFragment = ListBottomSheetFragment(this@SellerInfoActivity).apply {
                    mList = mutableListOf(ProductOrderType.DATE.label, ProductOrderType.SCORE.label, ProductOrderType.PRICE_ASC.label, ProductOrderType.PRICE_DESC.label)
                    mTitle = this@SellerInfoActivity.getString(R.string.product_order_title)
                    mListener = object : ListBottomSheetFragment.ListBottomSheetListener {
                        override fun onItemClick(position: Int) {
                            when (position) {
                                ProductOrderType.DATE.position -> {
                                    mViewModel.mOrder = ProductOrderType.DATE.type
                                    mBinding.textviewSellerstoreFilter.text = ProductOrderType.DATE.label
                                }
                                ProductOrderType.SCORE.position -> {
                                    mViewModel.mOrder = ProductOrderType.SCORE.type
                                    mBinding.textviewSellerstoreFilter.text = ProductOrderType.SCORE.label
                                }
                                ProductOrderType.PRICE_ASC.position -> {
                                    mViewModel.mOrder = ProductOrderType.PRICE_ASC.type
                                    mBinding.textviewSellerstoreFilter.text = ProductOrderType.PRICE_ASC.label
                                }
                                ProductOrderType.PRICE_DESC.position -> {
                                    mViewModel.mOrder = ProductOrderType.PRICE_DESC.type
                                    mBinding.textviewSellerstoreFilter.text = ProductOrderType.PRICE_DESC.label
                                }
                            }

                            mViewModel.mPage = mViewModel.INITIAL_PAGE
                            mViewModel.getSellerProductList()
                        }

                        override fun onClickClose() {
                            this@apply.dismiss()
                        }
                    }
                }
            }
            mFilterFragment.show(supportFragmentManager, baseTag)
        }
        mBinding.scrollviewSellerstore.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (v != null && scrollY == (v.getChildAt(0)?.measuredHeight!! - v.measuredHeight) && mBinding.scrollviewSellerstore.visibility == View.VISIBLE) {
                mViewModel.onClickMore()
            }
        }

        mViewModel.getSellerInfo()
        mViewModel.getSellerStoreInfo()
        mViewModel.getSellerBookMark()
        mViewModel.getSellerProductList()

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
        mViewModel = SellerInfoViewModel().apply {
            intent.getLongExtra("sellerId", -1).let {
                if (it > 0)
                    this.mSellerId = it
                else {
                    ToastUtil.showMessage(this@SellerInfoActivity.getString(R.string.common_message_error))
                    finish()
                }
            }
        }
//        mViewModel.mSeller.observe(this@SellerInfoActivity, Observer {
//            mBinding.businessSeller = it
//            initHeader(it)
//        })
        mViewModel.mSellerStore.observe(this@SellerInfoActivity, Observer {
            if (CustomLog.flag) CustomLog.L("mBusinessSeller", "it", it)
            mBinding.sellerStore = it
            mBinding.includeSellerstoreInfo.sellerStore = it
            setStoreIntroDetail(it)
            mViewModel.getBusinessSellerInfo()
        })
        mViewModel.mSellerBookMark.observe(this@SellerInfoActivity, Observer { mBinding.bookMark = it })
        mViewModel.mSellerSatisfaction.observe(this@SellerInfoActivity, Observer { mBinding.satisfaction = it })
        mViewModel.mSellerFollowerCount.observe(this@SellerInfoActivity, Observer {
            mBinding.textviewSellerstoreFollowercount.text = String.format(this@SellerInfoActivity.getString(R.string.common_format_people), it.bookmarkCount)
        })
        mViewModel.mBusinessSeller.observe(this, Observer {
            mBinding.businessSeller = it
            mBinding.includeSellerstoreInfo.businessSeller = it
            if (TextUtils.isEmpty(it.sellerUser.user.nickname))
                mBinding.includeSellerstoreHeader.title = it.sellerUser.user.nickname
            if (CustomLog.flag) CustomLog.L("mBusinessSeller", "it", it)
        })
        mViewModel.mSeller.observe(this, Observer {
            if (CustomLog.flag) CustomLog.L("mSeller", "it", it)
            mBinding.includeSellerstoreHeader.textviewSellerstoreTitle.text = it.user.nickname
            mBinding.seller = it
        })

        mViewModel.mSellerProductList.observe(this@SellerInfoActivity, Observer {
            mViewModel.misLast = it.deals.isEmpty()
            if (mViewModel.mPage == 1) {// first
                mBinding.recyclerivewSellerstoreProductlist.adapter = SellerInfoProductAdapter().apply {
                    mList = it.deals
                    this@apply.mViewModel = this@SellerInfoActivity.mViewModel
                }
            } else { // more
                (mBinding.recyclerivewSellerstoreProductlist.adapter as SellerInfoProductAdapter).mList.addAll(it.deals)
                (mBinding.recyclerivewSellerstoreProductlist.adapter as SellerInfoProductAdapter).notifyDataSetChanged()

                if ((mBinding.recyclerivewSellerstoreProductlist.adapter as SellerInfoProductAdapter).mList.size == it.countOfDeals)
                    mBinding.linearlayoutSellerstoreMore.visibility = View.GONE
            }
        })
    }

    private fun initHeader() {
        mBinding.includeSellerstoreHeader.setOnClickBack { finish() }
        mBinding.includeSellerstoreHeader.setOnClickMenu { CommonUtil.startMenuActivity(this@SellerInfoActivity, Flag.RequestCode.SIDE_MENU) }
        mBinding.includeSellerstoreHeader.setOnClickCart { CommonUtil.startCartActivity(this@SellerInfoActivity) }
        mBinding.includeSellerstoreHeader.setOnClickSearch { CommonUtil.startSearchWordActivity(this@SellerInfoActivity, null, true) }
    }


    private fun setStoreIntroDetail(sellerStroe: SellerStore) {
        if (!TextUtils.isEmpty(sellerStroe.storeIntroductionDetail)) {
            val data = StringBuilder()
            data.append("<style>img{display: inline;height: auto;max-width: 100%;}" +
                    "body{word-break: break-all; word-break: break-word}" +
                    "h1{font-size:large; word-break: break-all; word-break: break-word}" +
                    "h2{font-size:medium; word-break: break-all; word-break: break-word}</style>")
            data.append(sellerStroe.storeIntroductionDetail!!.replace("\"//www", "\"https://www"))
            mBinding.includeSellerstoreInfo.webviewSellerstoreContent.settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                setSupportMultipleWindows(true)
                allowFileAccess = true
                pluginState = WebSettings.PluginState.ON
                pluginState = WebSettings.PluginState.ON_DEMAND
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                loadsImagesAutomatically = true
                defaultFontSize = baseContext?.resources?.getDimension(R.dimen.text_4)?.toInt()
                        ?: 20
                setAppCacheEnabled(true)
                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                if (Build.VERSION.SDK_INT >= 26) safeBrowsingEnabled = false
            }
            mBinding.includeSellerstoreInfo.webviewSellerstoreContent.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    //return super.shouldOverrideUrlLoading(view, request)
                    view?.webChromeClient = WebChromeClient()
                    if (CustomLog.flag) CustomLog.L("CommunityDetailContentsFragment", request?.url!!.toString())
                    return true
                }
            }
            mBinding.includeSellerstoreInfo.webviewSellerstoreContent.loadData(data.toString(), "text/html", null)
        }
    }
}

