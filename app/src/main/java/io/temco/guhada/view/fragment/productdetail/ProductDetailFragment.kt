package io.temco.guhada.view.fragment.productdetail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Info
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnMainListener
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.common.listener.OnProductDetailMenuListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.db.entity.RecentDealEntity
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.option.OptionAttr
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailMenuViewModel
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailViewModel
import io.temco.guhada.databinding.ActivityProductDetailBinding
import io.temco.guhada.view.activity.*
import io.temco.guhada.view.adapter.ImagePagerAdapter
import io.temco.guhada.view.adapter.productdetail.ProductDetailInfoAdapter
import io.temco.guhada.view.adapter.productdetail.ProductDetailTagAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.fragment.cart.AddCartResultFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * 상품 상세 Fragment
 * @author Hyeyeon Park
 */
class ProductDetailFragment : BaseFragment<ActivityProductDetailBinding>(), OnProductDetailListener {
    var dealId: Long = 0 // * required initially
    lateinit var mainListener: OnMainListener
    private val INVALID_DEAL_ID = -1
    private var animFlag = true
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    private lateinit var mViewModel: ProductDetailViewModel
    private lateinit var mClaimFragment: ProductDetailClaimFragment
    private lateinit var mMenuFragment: ProductDetailMenuFragment
    private lateinit var mHeaderMenuFragment: ProductDetailMenuFragment
    private lateinit var mReviewFragment: ProductDetailReviewFragment
    private lateinit var mAddCartResultFragment: AddCartResultFragment
    private lateinit var mStoreFragment: ProductDetailStoreFragment

    override fun getBaseTag(): String = ProductDetailFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_product_detail

    // room database init
    private val db: GuhadaDB by lazy { GuhadaDB.getInstance(this.context as Activity)!! }
    // rx Init
    private var mDisposable: CompositeDisposable = CompositeDisposable()

    override fun init() {
        initUtils()
        initViewModel()
        initTabListener()
        setDetectScrollView()

        mBinding.includeProductdetailHeader.viewModel = mViewModel
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initUtils() {
        //[2019.06.26]임시 브릿지
        if (context != null) mLoadingIndicatorUtil = LoadingIndicatorUtil(context!!)
        if (::mLoadingIndicatorUtil.isInitialized) mLoadingIndicatorUtil.show()
    }

    private fun initViewModel() {
        mViewModel = ProductDetailViewModel(this)
        mViewModel.dealId = dealId
        mViewModel.notifySellerStoreFollow = { bookMark -> mStoreFragment.setSellerBookMark(bookMark) }
        mViewModel.mExpectedCouponList.observe(this, Observer { list ->
            if (list.isEmpty()) {
                mBinding.includeProductdetailContentheader.linearlayoutProductdetailCoupon.visibility = View.GONE
            } else {
                var highestPrice = 0
                for (coupon in list)
                    if (coupon.discountPrice > highestPrice)
                        highestPrice = coupon.discountPrice

                // 정률 할인 쿠폰만 있는 경우
                var highestRate = 0.0
                if (highestPrice == 0)
                    for (coupon in list)
                        if (coupon.discountRate > highestRate)
                            highestRate = coupon.discountRate

                mBinding.includeProductdetailContentheader.linearlayoutProductdetailCoupon.visibility = View.VISIBLE
                mBinding.includeProductdetailContentheader.textviewProductdetailCoupon.text =
                        if (highestPrice > 0) String.format(context?.getString(R.string.productdetail_coupon_price)!!, highestPrice)
                        else String.format(context?.getString(R.string.productdetail_coupon_rate)!!, "$highestRate%")

                mBinding.includeProductdetailContentheader.linearlayoutProductdetailCoupon.setOnClickListener {
                    val intent = Intent(context, CouponDownloadDialogActivity::class.java)
                    intent.putParcelableArrayListExtra("couponList", ArrayList(list))
                    startActivity(intent)
                }
            }
        })
        mViewModel.product.observe(this, Observer<Product> { product ->
            // [상세정보|상품문의|셀러스토어] 탭 상단부, 컨텐츠 웹뷰 먼저 display
            mViewModel.getExpectedCoupon()
            initSummary()
            initContentHeader()
            /*mBinding.includeProductdetailContentbody.webviewProductdetailContent.loadData(product.desc, "text/html", null)*/


            mBinding.includeProductdetailContentbody.webviewProductdetailContent.settings.apply {
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
            mBinding.includeProductdetailContentbody.webviewProductdetailContent.webViewClient = object  : WebViewClient(){
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    //return super.shouldOverrideUrlLoading(view, request)
                    view?.webChromeClient = WebChromeClient()
                    if(CustomLog.flag)CustomLog.L("CommunityDetailContentsFragment",request?.url!!.toString())
                    return true
                }
            }

            val data = StringBuilder()
            data.append("<HTML><HEAD><LINK href=\"community.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body>")
            data.append(product.desc.replace("\"//www","\"https://www"))
            data.append("</body></HTML>")
            mBinding.includeProductdetailContentbody.webviewProductdetailContent.loadDataWithBaseURL("file:///android_asset/", data.toString(),"text/html; video/mpeg", "utf-8", null)


            hideLoadingIndicator()

            // [상세정보|상품문의|셀러스토어] 탭 하단부 display
            GlobalScope.launch {
                mBinding.includeProductdetailContentbody.viewModel = mViewModel
                mBinding.includeProductdetailContentinfo.viewModel = mViewModel
                mBinding.includeProductdetailContentshipping.viewModel = mViewModel
                mBinding.includeProductdetailContentnotifies.viewModel = mViewModel
                mViewModel.getSellerBookMark(Type.BookMarkTarget.SELLER.name)
                /**
                 * 북마크 여부 확인
                 */
                mViewModel.getBookMark(Type.BookMarkTarget.PRODUCT.name, mViewModel.product.value!!.productId)
                mViewModel.getSellerInfo()
                initOptionMenu()
                initClaims()
                initReview()
                initStore()
            }
            /**
             * @author park jungho
             * 19.07.25
             * 최근본상품의 상품 DB에 추가
             */
            if (product.productId > 0L) {
                mDisposable.add(Observable.just(product).subscribeOn(Schedulers.io()).subscribe {
                    db.recentDealDao().delete(dealId)
                    var recentDealEntity = RecentDealEntity()
                    recentDealEntity.initData(Calendar.getInstance().timeInMillis, dealId, Gson().toJson(it), "")
                    if (CustomLog.flag) CustomLog.L("initViewModel", recentDealEntity.toString())
                    db.recentDealDao().insert(recentDealEntity)
                    var list = db.recentDealDao().getAll(21)
                    if (list.size >= 21) {
                        db.recentDealDao().delete(list[list.size - 1])
                    }
                })
            }
        })

        if (mViewModel.dealId > INVALID_DEAL_ID) {
            mViewModel.getDetail()
        }
    }

    private fun initTabListener() {
        mBinding.includeProductdetailContentbody.tablayoutProductdetail.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                this@ProductDetailFragment.scrollToElement(tab?.position ?: 0)
            }
        })
    }

    private fun initSummary() {
        mViewModel.getSellerSatisfaction()
        mBinding.includeProductdetailContentsummary.viewModel = mViewModel
        mBinding.includeProductdetailContentsummary.imageviewProductdetailSellerprofile.setOnClickListener {
            val intent = Intent(this@ProductDetailFragment.context, SellerInfoActivity::class.java)
            intent.putExtra("sellerId", mViewModel.product.value?.sellerId)
            startActivity(intent)
        }
    }

    private fun initContentHeader() {
        mBinding.includeProductdetailContentheader.viewModel = mViewModel
        mBinding.includeProductdetailContentheader.viewpagerProductdetailImages.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                mViewModel.imagePos = position + 1
                mViewModel.notifyPropertyChanged(BR.imagePos)
            }
        })
        this@ProductDetailFragment.hideLoadingIndicator()
    }

    override fun redirectHome() {
        mainListener.removeProductFragment()
    }

    override fun showAddCartResult() {
        if (!::mAddCartResultFragment.isInitialized) mAddCartResultFragment = AddCartResultFragment(this)
        mAddCartResultFragment.show(fragmentManager!!, "addCartResult")
    }

    override fun dismissAddCartResult() {
        if (::mAddCartResultFragment.isInitialized)
            mAddCartResultFragment.dismiss()
    }

    override fun dismissOptionMenu() {
        mViewModel.menuVisibility = ObservableInt(View.GONE)
        mViewModel.notifyPropertyChanged(BR.menuVisibility)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mLoadingIndicatorUtil.isInitialized && mLoadingIndicatorUtil.isShowing) mLoadingIndicatorUtil.dismiss()
        if (::mClaimFragment.isInitialized) mClaimFragment.onDestroy()
        if (::mMenuFragment.isInitialized) mMenuFragment.onDestroy()
        if (::mHeaderMenuFragment.isInitialized) mHeaderMenuFragment.onDestroy()
        if (::mReviewFragment.isInitialized) mReviewFragment.onDestroy()

        if (mBinding.includeProductdetailContentheader.viewpagerProductdetailImages.adapter != null)
            (mBinding.includeProductdetailContentheader.viewpagerProductdetailImages.adapter as ImagePagerAdapter).clearItems()

        try {
            GuhadaDB.destroyInstance()
            // rx release
            mDisposable.dispose()
        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
        }
    }

    /**
     * 상품 문의 View 구성
     * productId 전달
     */
    private fun initClaims() {
        mClaimFragment = ProductDetailClaimFragment().apply {
            this.productId = mViewModel.product.value?.productId ?: 0
        }
        childFragmentManager.beginTransaction().let {
            it.add(mBinding.framelayoutProductdetailClaim.id, mClaimFragment)
            it.commitAllowingStateLoss()
        }
    }

    /**
     * 상품 리뷰 View 구성
     * productId 전달
     */
    private fun initReview() {
        mReviewFragment = ProductDetailReviewFragment()
        mReviewFragment.notifySummary = { averageReviewsRating ->
            mBinding.includeProductdetailContentsummary.averageReviewsRating = averageReviewsRating
            mBinding.executePendingBindings()
        }
        mReviewFragment.setProductId(productId = mViewModel.product.value?.productId ?: 0)

        childFragmentManager.beginTransaction().let {
            it.add(mBinding.framelayoutProductdetailReview.id, mReviewFragment)
            it.commitAllowingStateLoss()
        }
    }

    /**
     * 셀러 스토어 View 구성
     * productId 전달
     */
    private fun initStore() {
        mStoreFragment = ProductDetailStoreFragment().apply {
            this.mProductId = this@ProductDetailFragment.mViewModel.product.value?.productId ?: -1
            this.mSellerId = this@ProductDetailFragment.mViewModel.product.value?.sellerId ?: -1
            this.mProductDetailViewModel = this@ProductDetailFragment.mViewModel
        }
        childFragmentManager.beginTransaction().let {
            it.add(mBinding.framelayoutProductdetailStore.id, mStoreFragment)
            it.commitAllowingStateLoss()
        }
    }

    private fun initOptionMenu() {
        ProductDetailMenuViewModel(object : OnProductDetailMenuListener {
            override fun setColorName(optionAttr: OptionAttr, task: () -> Unit) = task()
            override fun closeMenu() {
                mViewModel.menuVisibility = ObservableInt(View.GONE)
                mViewModel.notifyPropertyChanged(BR.menuVisibility)
            }

            override fun showMessage(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

        }).apply {
            product = mViewModel.product.value ?: Product()
            this.closeButtonVisibility = View.VISIBLE
        }.let { menuViewModel ->
            mMenuFragment = ProductDetailMenuFragment().apply { this.mViewModel = menuViewModel }
        }

        ProductDetailMenuViewModel(object : OnProductDetailMenuListener {
            override fun setColorName(optionAttr: OptionAttr, task: () -> Unit) = task()
            override fun closeMenu() {
                mViewModel.menuVisibility = ObservableInt(View.GONE)
                mViewModel.notifyPropertyChanged(BR.menuVisibility)
            }

            override fun showMessage(message: String) {
                ToastUtil.showMessage(message)
            }
        }).apply {
            product = mViewModel.product.value ?: Product()
            this.closeButtonVisibility = View.GONE
        }.let { menuViewModel ->
            mHeaderMenuFragment = ProductDetailMenuFragment().apply { this.mViewModel = menuViewModel }
        }

        if (isAdded) {
            childFragmentManager.beginTransaction().let {
                if (::mMenuFragment.isInitialized && !mMenuFragment.isAdded) it.add(mBinding.framelayoutProductdetailMenu.id, mMenuFragment)
                if (::mHeaderMenuFragment.isInitialized && !mHeaderMenuFragment.isAdded) it.add(mBinding.includeProductdetailContentheader.framelayoutProductdetailHeadermenu.id, mHeaderMenuFragment)
                it.commitAllowingStateLoss()
            }
        }

        this@ProductDetailFragment.hideLoadingIndicator()
    }

    private fun setDetectScrollView() {
        // val scrollBounds = Rect()
        mBinding.scrollviewProductdetail.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            val height = mBinding.includeProductdetailHeader.toolbarProductdetailHeader.height + mBinding.includeProductdetailContentsummary.linearlayoutProductdetailSummayContainer.height
            if (animFlag) {
                if (oldScrollY > height) {
                    if (scrollY - oldScrollY > 50 && mViewModel.bottomBtnVisibility.get() == View.GONE) {
                        animFlag = false
                        mViewModel.bottomBtnVisibility = ObservableInt(View.VISIBLE)
                        mViewModel.notifyPropertyChanged(BR.bottomBtnVisibility)
                        animFlag = true
                    }
                } else {
                    if (oldScrollY - scrollY > 50 && mViewModel.bottomBtnVisibility.get() == View.VISIBLE) {
                        animFlag = false
                        mViewModel.bottomBtnVisibility = ObservableInt(View.GONE)
                        mViewModel.notifyPropertyChanged(BR.bottomBtnVisibility)
                        animFlag = true
                    }
                }
            }
        }
    }

    override fun showSideMenu() = this.mainListener.showSideMenu(true)

    // 메뉴 이동 탭 [상세정보|상품문의|셀러스토어]
    override fun scrollToElement(pos: Int) {
        var h = 0
        when (pos) {
            0 -> h = (mBinding.productdetailScrollflagContent.parent as View).top + mBinding.productdetailScrollflagContent.top
            1 -> h = (mBinding.productdetailScrollflagQna.parent as View).top + mBinding.productdetailScrollflagQna.top
            2 -> h = (mBinding.productdetailScrollflagRecommend.parent as View).top + mBinding.productdetailScrollflagRecommend.top
        }

        mBinding.scrollviewProductdetail.smoothScrollTo(0, h)
    }

    override fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun refreshClaims() {
        if (::mClaimFragment.isInitialized) mClaimFragment.refreshClaims()
    }

    fun refreshIsMyClaimsVisible() {
        if (::mClaimFragment.isInitialized) mClaimFragment.refreshIsMineVisible()
    }

    override fun redirectLoginActivity() {
        startActivityForResult(Intent(context, LoginActivity::class.java), Flag.RequestCode.LOGIN)
    }

    override fun showMenu() {
        val optionCount = mViewModel.product.value?.options?.size
        val selectedOptionCount = if (mViewModel.menuVisibility.get() == View.VISIBLE) {
            if (::mMenuFragment.isInitialized) mMenuFragment.getSelectedOptionCount() else 0
        } else {
            if (::mHeaderMenuFragment.isInitialized) mHeaderMenuFragment.getSelectedOptionCount() else 0
        }

        if (selectedOptionCount == optionCount) {
            mViewModel.addCartItem()
        } else {
            setMenuVisible()
        }
    }

    // 수정 예정 (BottomSheetFragment로 변경 예정)
    private fun setMenuVisible() {
        ToastUtil.showMessage(context?.getString(R.string.cart_message_notselectedoption)
                ?: BaseApplication.getInstance().getString(R.string.cart_message_notselectedoption))
        mViewModel.menuVisibility = ObservableInt(View.VISIBLE)
        mViewModel.notifyPropertyChanged(BR.menuVisibility)
    }

    override fun redirectPaymentActivity(isOptionPopupSelected: Boolean) {
        val optionCount = mViewModel.product.value?.options?.size ?: 0
        val selectedOptionCount = if (mViewModel.menuVisibility.get() == View.VISIBLE) {
            mMenuFragment.getSelectedOptionCount()
        } else {
            mHeaderMenuFragment.getSelectedOptionCount()
        }

        showOptionMenu(optionCount = optionCount, selectedOptionCount = selectedOptionCount, isOptionPopupSelected = isOptionPopupSelected)
    }


    /////
    private fun showOptionMenu(optionCount: Int, selectedOptionCount: Int, isOptionPopupSelected: Boolean) {
        if (selectedOptionCount == optionCount) {
            // 전달 데이터
            // 1.상품 대표 이미지, 2.상품 명, 3.옵션 선택 항목, 4.판매가, 5.수량
            val product = mViewModel.product.value
            val brandName = product?.brandName ?: ""
            val name = product?.name ?: ""
            val optionAttr: MutableMap<String, OptionAttr>
            val price: Int
            val count: Int

            if (isOptionPopupSelected) {
                count = mMenuFragment.getProductCount()
                price = mMenuFragment.getTotalPrice()
                optionAttr = mMenuFragment.getSelectedOptionAttrs()
            } else {
                count = mHeaderMenuFragment.getProductCount()
                price = mHeaderMenuFragment.getTotalPrice()
                optionAttr = mHeaderMenuFragment.getSelectedOptionAttrs()
            }

            BaseProduct().apply {
                this.dealId = mViewModel.dealId
                this.profileUrl = product?.imageUrls?.get(0) ?: "" // 대표이미지 임시
                this.name = name
                this.brandName = brandName
                this.optionMap = optionAttr
                this.totalCount = count
                this.totalPrice = price
                this.season = product?.season ?: ""
            }.let { baseProduct ->
                // 장바구니 API 파라미터
                baseProduct.dealOptionId = getSelectedOptionDealId()
                mViewModel.menuVisibility.set(View.GONE)
                mViewModel.notifyPropertyChanged(BR.menuVisibility)

                Intent(context, PaymentActivity::class.java).let { intent ->
                    intent.putExtra("quantity", getSelectedProductQuantity())
                    intent.putExtra("product", baseProduct)
                    startActivityForResult(intent, Flag.RequestCode.PAYMENT)
                }
            }
        } else {
            setMenuVisible()
            ToastUtil.showMessage(resources.getString(R.string.productdetail_message_selectoption))
        }
    }

    override fun getSelectedOptionDealId(): Long? {
        return when {
            mMenuFragment.getSelectedOptionCount() > 0 -> mMenuFragment.getSelectedOptionDealId()
            mHeaderMenuFragment.getSelectedOptionCount() > 0 -> mHeaderMenuFragment.getSelectedOptionDealId()
            else -> null
        }
    }

    override fun getSelectedProductQuantity(): Int {
        return if (mViewModel.menuVisibility.get() == View.VISIBLE) mMenuFragment.getProductCount()
        else mHeaderMenuFragment.getProductCount()
    }

    override fun hideLoadingIndicator() {
        if (::mLoadingIndicatorUtil.isInitialized && mLoadingIndicatorUtil.isShowing) mLoadingIndicatorUtil.dismiss()
    }

    override fun closeActivity() {
        mainListener.removeProductDetailFragment()
    }

    override fun setBrandProductList(brand: Brand) {
        CommonUtil.startBrandScreen(context as Activity, brand, false)
        //mainListener.setBrandProductList(brand)
    }

    /**
     * @author park jungho
     * 검색 화면으로 이동
     */
    override fun showSearchWordActivity() {
        CommonUtil.startSearchWordActivity(context as Activity, null, true)
    }

    override fun showReportActivity() {
        if(CommonUtil.checkToken()){
            CommonUtil.startReportActivity(context as Activity, 0, mViewModel.product.value, null)
        }else{
            CustomMessageDialog(message = "로그인 후 이용이 가능합니다.",
                    cancelButtonVisible = true,
                    confirmTask = {
                        CommonUtil.moveLoginPage(context as AppCompatActivity)
                    }).show(manager = (context as AppCompatActivity).supportFragmentManager, tag = "CommunityDetailActivity")
        }
    }

    companion object {
        @JvmStatic
        fun startActivity(context: Context, id: Int) {
            val intent = Intent(context, ProductFragmentDetailActivity::class.java)
            intent.putExtra(Info.INTENT_DEAL_ID, id)
            context.startActivity(intent)
        }

        @JvmStatic
        @BindingAdapter("productTags")
        fun RecyclerView.bindTags(list: MutableList<String>?) {
            if (list != null && list.isNotEmpty()) {
                if (this.adapter == null) {
                    this.adapter = ProductDetailTagAdapter()
                }
                (this.adapter as ProductDetailTagAdapter).setItems(list)
            }
        }

        @JvmStatic
        @BindingAdapter("productInfo")
        fun RecyclerView.bindInfo(list: MutableList<Product.Item>?) {
            if (list != null && list.isNotEmpty()) {
                if (this.adapter == null) {
                    this.adapter = ProductDetailInfoAdapter()
                }
                (this.adapter as ProductDetailInfoAdapter).setItems(list)
            }
        }

        @JvmStatic
        @BindingAdapter("productImage")
        fun ViewPager.bindImage(list: MutableList<String>?) {
            if (list != null && list.isNotEmpty()) {
                if (this.adapter == null) {
                    this.adapter = ImagePagerAdapter()
                }

                // 변경 예정
                val images: MutableList<Product.Image> = ArrayList()
                for (str in list) {
                    Product.Image().apply { url = str }.let { img -> images.add(img) }
                }
                (this.adapter as ImagePagerAdapter).setItems(images)
            }
        }

    }
}