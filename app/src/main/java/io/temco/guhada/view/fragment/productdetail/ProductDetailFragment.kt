package io.temco.guhada.view.fragment.productdetail

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.widget.NestedScrollView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.analytics.HitBuilders
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.kochava.base.Tracker
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.BR
import io.temco.guhada.BuildConfig
import io.temco.guhada.R
import io.temco.guhada.common.*
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.enum.TrackingEvent
import io.temco.guhada.common.listener.OnMainListener
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.common.listener.OnProductDetailMenuListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.data.db.entity.RecentDealEntity
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.model.option.OptionAttr
import io.temco.guhada.data.model.option.OptionInfo
import io.temco.guhada.data.model.point.PointProcessParam
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailMenuViewModel
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailViewModel
import io.temco.guhada.databinding.ActivityProductDetailBinding
import io.temco.guhada.view.activity.*
import io.temco.guhada.view.activity.base.BaseActivity
import io.temco.guhada.view.adapter.ImagePagerAdapter
import io.temco.guhada.view.adapter.productdetail.ProductDetailInfoAdapter
import io.temco.guhada.view.adapter.productdetail.ProductDetailTagAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.fragment.base.BaseFragment
import io.temco.guhada.view.fragment.cart.AddCartResultFragment
import io.temco.guhada.view.fragment.productdetail.ProductDetailFragment.Companion.fadeInAndOut
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * 상품 상세 Fragment
 * @see io.temco.guhada.view.activity.ProductFragmentDetailActivity
 * @author Hyeyeon Park
 * @since 2019.06.26
 */
class ProductDetailFragment : BaseFragment<ActivityProductDetailBinding>(), OnProductDetailListener {
    var dealId: Long = 0 // * required initially
    lateinit var mainListener: OnMainListener
    private val INVALID_DEAL_ID = -1
    private var animFlag = true
    lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil

    private lateinit var mViewModel: ProductDetailViewModel
    private lateinit var mClaimFragment: ProductDetailClaimFragment
    private lateinit var mMenuFragment: ProductDetailMenuFragment
    private lateinit var mHeaderMenuFragment: ProductDetailMenuFragment
    private lateinit var mReviewFragment: ProductDetailReviewFragment
    private lateinit var mAddCartResultFragment: AddCartResultFragment
    private lateinit var mStoreFragment: ProductDetailStoreFragment

    override fun getBaseTag(): String = ProductDetailFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_product_detail

    override fun onResume() {
        super.onResume()
        setBadge()
    }

    // room database init
    private val db: GuhadaDB by lazy { GuhadaDB.getInstance(this.context as Activity)!! }
    // rx Init
    private var mDisposable: CompositeDisposable = CompositeDisposable()

    override fun init() {
        initUtils()
        initViewModel()
        initTabListener()
        setDetectScrollView()
        setEvenBus()

        mBinding.includeProductdetailHeader.viewModel = mViewModel
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun setBadge() {
//        mBinding.includeProductdetailHeader.textviewBadge.text = BaseApplication.getInstance().getmCartCount().toString()
    }

    private fun initUtils() {
        if (context != null) mLoadingIndicatorUtil = LoadingIndicatorUtil(context!!)
        if (::mLoadingIndicatorUtil.isInitialized) mLoadingIndicatorUtil.show()
    }

    private fun initViewModel() {
        mViewModel = ProductDetailViewModel(this)
        mViewModel.dealId = dealId
        mViewModel.notifySellerStoreFollow = { bookMark -> mStoreFragment.setSellerBookMark(bookMark) }
        mViewModel.mSetBadgeTask = { setBadge() }
        mViewModel.mExpectedCouponList.observe(this, Observer { list ->
            if (list.isEmpty()) {
                mBinding.includeProductdetailContentheader.linearlayoutProductdetailCoupon.visibility = View.GONE
            } else {
                setCouponDownloadView(list)
            }
        })
        mViewModel.product.observe(this, Observer<Product> { product ->
            mBinding.product = product

            //[상세정보|상품문의|셀러스토어] 탭 상단부, 컨텐츠 웹뷰 먼저 display
            mViewModel.getDueSavePoint()
            mViewModel.getExpectedCoupon()
            initSummary()
            initContentHeader()
            initContent(product)
            hideLoadingIndicator()

            // [상세정보|상품문의|셀러스토어] 탭 하단부 display
            GlobalScope.launch {
                //                mBinding.includeProductdetailContentbody.viewModel = mViewModel
                mBinding.includeProductdetailContentinfo.viewModel = mViewModel
                mBinding.includeProductdetailContentshipping.viewModel = mViewModel
                mBinding.includeProductdetailContentnotifies.viewModel = mViewModel
                mViewModel.getSellerBookMark(Type.BookMarkTarget.SELLER.name)

                // 북마크 여부 확인
                mViewModel.getBookMark(Type.BookMarkTarget.PRODUCT.name, mViewModel.product.value!!.productId)
                mViewModel.getSellerInfo()
                mViewModel.getSellerStoreInfo()

                initOptionMenu()
                initClaims()
                initReview()
                initStore()

                setViewTracking(product)
                sendAnalyticEvent(product)
            }

            addRecentlyView(product)
            setShareClickListener(product.dealId)
            mBinding.executePendingBindings()
        })

        if (mViewModel.dealId > INVALID_DEAL_ID) {
            mViewModel.getDetail()
        }
    }

    /**
     * [코차바] 상품조회
     */
    private fun setViewTracking(product: Product) {
        Tracker.Event(TrackingEvent.Product.View_Product.eventName).let {
            it.addCustom("dealId", product.dealId.toString())
            it.addCustom("productId", product.productId.toString())
            it.addCustom("brandId", product.brandId.toString())
            it.addCustom("sellerId", product.sellerId.toString())
            it.addCustom("name", product.name)
            it.addCustom("sellPrice", product.sellPrice.toString())
            it.addCustom("discountPrice", product.discountPrice.toString())
            if (!TextUtils.isEmpty(product.season)) it.addCustom("season", product.season)
            TrackingUtil.sendKochavaEvent(it)
        }
    }

    /**
     * 19.07.25
     * 최근본상품의 상품 DB에 추가
     */
    private fun addRecentlyView(product: Product) {
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
    }

    // 공유하기
    private fun setShareClickListener(dealId: Long) {
        mBinding.includeProductdetailContentheader.imagebuttonProductdetailShare.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, Info.SHARE_PRODUCT_URL + dealId)
                type = "text/plain"
            }
            (context as Activity).startActivity(Intent.createChooser(sendIntent, "공유"))
        }
    }

    private fun setCouponDownloadView(list: MutableList<Coupon>) {
        var isAllAlreadySaved = true
        for (coupon in list) {
            if (!coupon.alreadySaved) {
                isAllAlreadySaved = false
                break
            }
        }

        var highestPrice = 0
        var highestRate = 0.0

        if (isAllAlreadySaved) {    // 회색 버튼
            for (coupon in list)
                if (coupon.discountPrice > highestPrice)
                    highestPrice = coupon.discountPrice

            // 정률 할인 쿠폰만 있는 경우
            if (highestPrice == 0)
                for (coupon in list)
                    if (coupon.discountRate > highestRate)
                        highestRate = coupon.discountRate
        } else {
            for (coupon in list)
                if (!coupon.alreadySaved && coupon.discountPrice > highestPrice)
                    highestPrice = coupon.discountPrice

            // 정률 할인 쿠폰만 있는 경우
            if (highestPrice == 0)
                for (coupon in list)
                    if (!coupon.alreadySaved && coupon.discountRate > highestRate)
                        highestRate = coupon.discountRate
        }

        if (highestPrice > 0 || highestRate > 0.0) {
            mBinding.includeProductdetailContentheader.linearlayoutProductdetailCoupon.visibility = View.VISIBLE
            mBinding.includeProductdetailContentheader.textviewProductdetailCoupon.text =
                    if (highestPrice > 0) String.format(context?.getString(R.string.productdetail_coupon_price)!!, highestPrice)
                    else String.format(context?.getString(R.string.productdetail_coupon_rate)!!, "${highestRate * 100}%")
            mBinding.includeProductdetailContentheader.linearlayoutProductdetailCoupon.setOnClickListener {
                ServerCallbackUtil.callWithToken(
                        task = { redirectCouponDownloadActivity(list) },
                        invalidTokenTask = { ToastUtil.showMessage(mBinding.root.context.getString(R.string.login_message_requiredlogin)) })
            }

            if (isAllAlreadySaved) setSaveCouponDisabled()
        } else {
            mBinding.includeProductdetailContentheader.linearlayoutProductdetailCoupon.visibility = View.GONE
        }
    }

    private fun redirectCouponDownloadActivity(list: MutableList<Coupon>) {
        val intent = Intent(context, CouponDownloadDialogActivity::class.java)
        intent.putParcelableArrayListExtra("couponList", ArrayList(list))
        intent.putExtra("dCategoryId", mViewModel.product.value?.dCategoryId)
        intent.putExtra("lCategoryId", mViewModel.product.value?.lCategoryId)
        intent.putExtra("mCategoryId", mViewModel.product.value?.mCategoryId)
        intent.putExtra("sCategoryId", mViewModel.product.value?.sCategoryId)
        intent.putExtra("dealId", mViewModel.product.value?.dealId)
        intent.putExtra("sellerId", mViewModel.product.value?.sellerId)
        (mBinding.root.context as AppCompatActivity).startActivityForResult(intent, RequestCode.COUPON_DOWNLOAD.flag)
    }

    private fun initTabListener() {
        val DETAIL_TAB_POS = 1
        val detailTab = mBinding.tablayoutProductdetail.getTabAt(DETAIL_TAB_POS)
        setTabTextStyle(tab = detailTab, textStyle = Typeface.BOLD, textColor = mBinding.root.context.resources.getColor(R.color.common_blue_purple))
        detailTab?.select()

        mBinding.tablayoutProductdetail.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (animFlag && tab?.position != 0 && tab?.position != 4) {
                    setTabTextStyle(tab = tab, textStyle = Typeface.BOLD, textColor = mBinding.root.context.resources.getColor(R.color.common_blue_purple))
                    this@ProductDetailFragment.scrollToElement(tab?.position ?: DETAIL_TAB_POS)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                setTabTextStyle(tab = tab, textStyle = Typeface.NORMAL, textColor = mBinding.root.context.resources.getColor(R.color.black_four))
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (animFlag && tab?.position != 0 && tab?.position != 4) {
                    setTabTextStyle(tab = tab, textStyle = Typeface.BOLD, textColor = mBinding.root.context.resources.getColor(R.color.common_blue_purple))
                    this@ProductDetailFragment.scrollToElement(tab?.position ?: DETAIL_TAB_POS)
                }
            }
        })

        // tablayout 양 끝 view
        var ll = (mBinding.tablayoutProductdetail.getChildAt(0) as LinearLayout).getChildAt(0) as LinearLayout
        val lp = ll.layoutParams as LinearLayout.LayoutParams

        lp.width = CommonViewUtil.convertDpToPixel(20, mBinding.root.context)
        lp.weight = 0f
        ll.layoutParams = lp
        ll.isEnabled = false

        ll = (mBinding.tablayoutProductdetail.getChildAt(0) as LinearLayout).getChildAt(4) as LinearLayout
        ll.layoutParams = lp
        ll.isEnabled = false
    }

    private fun setTabTextStyle(tab: TabLayout.Tab?, textStyle: Int, textColor: Int) {
        val STORE_TAB_POS = 3
        if (tab != null) {
            val tabLayout = (mBinding.tablayoutProductdetail.getChildAt(0) as ViewGroup).getChildAt(tab.position) as LinearLayout
            val textView =
                    if (tab.position == STORE_TAB_POS) tabLayout.findViewById(R.id.textview_tab)
                    else tabLayout.getChildAt(1) as TextView
            val typeface = if (textStyle == Typeface.BOLD) textView.typeface else null
            textView.setTypeface(typeface, textStyle)
            textView.setTextColor(textColor)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initSummary() {
        mViewModel.getProductReviewSummary()
        mViewModel.getSellerSatisfaction()
        mBinding.includeProductdetailContentsummary.viewModel = mViewModel
        mBinding.includeProductdetailContentsummary.imagebuttonProductdetailReview.setOnClickListener {
            val h = (mBinding.productdetailScrollflagReview.parent as View).top + mBinding.productdetailScrollflagReview.top
            mBinding.scrollviewProductdetail.smoothScrollTo(0, h)
        }
        mBinding.includeProductdetailContentsummary.imageviewProductdetailSellerprofile.setOnClickListener { redirectSellerInfoActivity() }
        mBinding.includeProductdetailContentsummary.framelayooutProductdetailSellerstore.setOnClickListener { redirectSellerInfoActivity() }

        // 혜택 정보-포인트 적립
        mViewModel.mExpectedPoint.observe(this, Observer {
            var advantageBuyPoint = 0
            var advantageReviewPoint = 0

            for (item in it.dueSavePointList) {
                when (item.dueSaveType) {
                    PointProcessParam.PointSave.BUY.type -> advantageBuyPoint = item.totalPoint
                    PointProcessParam.PointSave.REVIEW.type -> advantageReviewPoint = item.totalPoint
                }
            }

            if (advantageBuyPoint == 0 && advantageReviewPoint == 0) {
                mBinding.includeProductdetailContentsummary.linearlayoutProductdetailAdvantagepoint.visibility = View.GONE
//                mBinding.includeProductdetailContentsummary.viewProductdetailAdvantage.visibility = View.GONE
            } else {
                mBinding.includeProductdetailContentsummary.linearlayoutProductdetailAdvantagepoint.visibility = View.VISIBLE
                mBinding.includeProductdetailContentsummary.textviewProductdetailAdvantagetitle.text = "${getString(R.string.productdetail_advantage_point)}  ${getString(R.string.productdetail_advantage_cardinterest)}"
//                mBinding.includeProductdetailContentsummary.viewProductdetailAdvantage.visibility = View.VISIBLE

                if (advantageBuyPoint > 0 && advantageReviewPoint > 0)
                    (mBinding.includeProductdetailContentsummary.textviewProductdetailAdvangatepointReview.layoutParams as ViewGroup.MarginLayoutParams).apply {
                        this.topMargin = CommonViewUtil.convertDpToPixel(dp = 6, context = mBinding.root.context)
                    }

                if (advantageBuyPoint > 0) {
                    mBinding.includeProductdetailContentsummary.textviewProductdetailAdvangatepointBuy.visibility = View.VISIBLE
                    mBinding.includeProductdetailContentsummary.textviewProductdetailAdvangatepointBuy.text = String.format(mBinding.root.context.resources.getString(R.string.productdetail_format_buypoint), advantageBuyPoint)
                } else mBinding.includeProductdetailContentsummary.textviewProductdetailAdvangatepointBuy.visibility = View.GONE

                if (advantageReviewPoint > 0) {
                    mBinding.includeProductdetailContentsummary.textviewProductdetailAdvangatepointReview.visibility = View.VISIBLE
                    mBinding.includeProductdetailContentsummary.textviewProductdetailAdvangatepointReview.text = String.format(mBinding.root.context.resources.getString(R.string.productdetail_format_reviewpoint), advantageReviewPoint)
                } else mBinding.includeProductdetailContentsummary.textviewProductdetailAdvangatepointReview.visibility = View.GONE
            }
        })

        // 혜택정보-무이자 할부
        mBinding.includeProductdetailContentsummary.linearlayoutProductdetailCardinterest.setOnClickListener {
            mBinding.root.context.startActivity(Intent(context, CardInterestActivity::class.java))
        }
    }

    private fun redirectSellerInfoActivity() {
        val intent = Intent(this@ProductDetailFragment.context, SellerInfoActivity::class.java)
        intent.putExtra("sellerId", mViewModel.product.value?.sellerId)
        startActivity(intent)
    }

    private fun initContentHeader() {
        mBinding.includeProductdetailContentheader.viewModel = mViewModel
        mBinding.includeProductdetailContentheader.framelayoutProductdetailBookmark.bringToFront()

        val matrix = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(matrix)
        mBinding.includeProductdetailContentheader.viewpagerProductdetailImages.layoutParams = FrameLayout.LayoutParams(matrix.widthPixels, matrix.widthPixels)
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
            this.sellerId = mViewModel.product.value?.sellerId ?: 0
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
        mReviewFragment = ProductDetailReviewFragment().apply {
            if (this@ProductDetailFragment.mViewModel.product.value?.productId != null && this@ProductDetailFragment.mViewModel.product.value?.productId ?: 0 > 0)
                this.productId = this@ProductDetailFragment.mViewModel.product.value?.productId!!
        }
        mReviewFragment.notifySummary = { averageReviewsRating ->
            mBinding.includeProductdetailContentsummary.averageReviewsRating = averageReviewsRating
            mBinding.executePendingBindings()
        }
        mReviewFragment.notifyTotalCount = { totalReviewsCount ->
            mBinding.includeProductdetailContentsummary.totalReviewsCount = totalReviewsCount
            mBinding.executePendingBindings()
        }
        childFragmentManager.beginTransaction().let {
            if (!mReviewFragment.isAdded) {
                it.add(mBinding.framelayoutProductdetailReview.id, mReviewFragment)
                it.commitAllowingStateLoss()
            }
        }
    }

    /**
     * 셀러 스토어 View 구성
     * productId 전달
     */
    private fun initStore() {
        val storeFragment = ProductDetailStoreFragment().apply {
            this.mProductId = this@ProductDetailFragment.mViewModel.product.value?.productId ?: -1
            this.mSellerId = this@ProductDetailFragment.mViewModel.product.value?.sellerId
                    ?: mViewModel.mSeller.id
            this.mProductDetailViewModel = this@ProductDetailFragment.mViewModel
        }
        mStoreFragment = storeFragment
        childFragmentManager.beginTransaction().let {
            if (mStoreFragment.isAdded) {
                it.remove(mStoreFragment)
                mStoreFragment = storeFragment
            }
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
            mMenuFragment = ProductDetailMenuFragment().apply {
                this.mViewModel = menuViewModel
                this.mIsBottomPopup = true
            }
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
            mHeaderMenuFragment = ProductDetailMenuFragment().apply {
                this.mViewModel = menuViewModel
                this.mIsBottomPopup = false
            }
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
        mBinding.scrollviewProductdetail.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            val claimHeight = (mBinding.productdetailScrollflagQna.parent as View).top + mBinding.productdetailScrollflagQna.top
            val storeHeight = (mBinding.productdetailScrollflagRecommend.parent as View).top + mBinding.productdetailScrollflagRecommend.top + mStoreFragment.getStoreFlagHeight()

            when {
                scrollY in 0 until claimHeight -> {
                    animFlag = false
                    mBinding.tablayoutProductdetail.getTabAt(1)?.select()
                    animFlag = true
                }
                scrollY in claimHeight until storeHeight -> {
                    animFlag = false
                    mBinding.tablayoutProductdetail.getTabAt(2)?.select()
                    animFlag = true
                }
                scrollY >= storeHeight -> {
                    animFlag = false
                    mBinding.tablayoutProductdetail.getTabAt(3)?.select()
                    animFlag = true
                }
            }
        }

        mBinding.appbarlayoutProductdetail.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset != 0 && verticalOffset + appBarLayout.totalScrollRange < 1500) {    // collapsed
                if (!mViewModel.bottomBtnVisible.get()) {
                    mViewModel.bottomBtnVisible = ObservableBoolean(true)
                    mViewModel.notifyPropertyChanged(BR.bottomBtnVisible)
                }
            } else {    // expanded
                if (mViewModel.bottomBtnVisible.get()) {
                    mViewModel.bottomBtnVisible = ObservableBoolean(false)
                    mViewModel.notifyPropertyChanged(BR.bottomBtnVisible)
                }
            }
        })
    }

    override fun showSideMenu() = this.mainListener.showSideMenu(true)

    /**
     * [상세정보|상품문의|셀러스토어] 탭 이동 scroll
     */
    override fun scrollToElement(pos: Int) {
        var h = 0
        when (pos) {
            1 -> h = 0 // (mBinding.productdetailScrollflagContent.parent as View).top + mBinding.productdetailScrollflagContent.top
            2 -> h = (mBinding.productdetailScrollflagQna.parent as View).top + mBinding.productdetailScrollflagQna.top
            3 -> h = (mBinding.productdetailScrollflagRecommend.parent as View).top + mBinding.productdetailScrollflagRecommend.top + mStoreFragment.getStoreFlagHeight()
        }

        mBinding.appbarlayoutProductdetail.setExpanded(false)
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

    fun refreshCouponDownloadView() {
        mViewModel.getExpectedCoupon()
    }

    override fun redirectLoginActivity() {
        startActivityForResult(Intent(context, LoginActivity::class.java), Flag.RequestCode.LOGIN)
    }

//      GRID LIST (DEPRECATED)
//    override fun showMenu() {
//        val optionCount = mViewModel.product.value?.options?.size
//        val selectedOptionCount = if (mViewModel.menuVisibility.get() == View.VISIBLE) {
//            if (::mMenuFragment.isInitialized) mMenuFragment.getSelectedOptionCount() else 0
//        } else {
//            if (::mHeaderMenuFragment.isInitialized) mHeaderMenuFragment.getSelectedOptionCount() else 0
//        }
//
//        if (selectedOptionCount == optionCount) {
//            mViewModel.addCartItem()
//        } else {
//            setMenuVisible()
//        }
//    }

    override fun showMenu() {
        val menuVisible = mViewModel.menuVisibility.get() == View.VISIBLE
        val selectedOption: OptionInfo? = if (menuVisible) mMenuFragment.mViewModel.mSelectedOptionInfo else mHeaderMenuFragment.mViewModel.mSelectedOptionInfo
        val isOptionNone = mViewModel.product.value?.options?.isEmpty() ?: false

        if (selectedOption != null || isOptionNone) {
            mViewModel.addCartItem()
        } else {
            setMenuVisible()
        }
    }

    private fun setMenuVisible() {
//        ToastUtil.showMessage(context?.getString(R.string.cart_message_notselectedoption)
//                ?: BaseApplication.getInstance().getString(R.string.cart_message_notselectedoption))
        mViewModel.menuVisibility = ObservableInt(View.VISIBLE)
        mViewModel.notifyPropertyChanged(BR.menuVisibility)
    }

    fun getMenuVisible(): Boolean = mViewModel.menuVisibility.get() == View.VISIBLE
    fun closeMenuPopup() {
        if (mViewModel.menuVisibility.get() == View.VISIBLE) {
            mViewModel.menuVisibility = ObservableInt(View.GONE)
            mViewModel.notifyPropertyChanged(BR.menuVisibility)
        }
    }

//      GRID LIST (DEPRECATED)
//      override fun redirectPaymentActivity(menuVisibile: Boolean) {
//        val optionCount = mViewModel.product.value?.options?.size ?: 0
//        val selectedOptionCount = if (mViewModel.menuVisibility.get() == View.VISIBLE) {
//            mMenuFragment.getSelectedOptionCount()
//        } else {
//            mHeaderMenuFragment.getSelectedOptionCount()
//        }
//
//        showOptionMenu(optionCount = optionCount, selectedOptionCount = selectedOptionCount, isOptionPopupSelected = isOptionPopupSelected)
//    }

    /**
     * 옵션 스피너 변경에 맞춰 수정
     * @since 2019.09.06
     * @author Hyeyeo Park
     */
    override fun redirectPaymentActivity(menuVisibile: Boolean) {
        val selectedOption: OptionInfo? = if (menuVisibile) mMenuFragment.mViewModel.mSelectedOptionInfo else mHeaderMenuFragment.mViewModel.mSelectedOptionInfo
        val isOptionNone = mViewModel.product.value?.options?.isEmpty() ?: false
        if (selectedOption != null || isOptionNone) {

            // 전달 데이터
            // 1.상품 대표 이미지, 2.상품 명, 3.옵션 선택 항목, 4.판매가, 5.수량
            val product = mViewModel.product.value
            val brandName = product?.brandName ?: ""
            val name = product?.name ?: ""
            val price: Int
            val count: Int

            if (menuVisibile) {
                count = mMenuFragment.getProductCount()
                price = mMenuFragment.getTotalPrice()
            } else {
                count = mHeaderMenuFragment.getProductCount()
                price = mHeaderMenuFragment.getTotalPrice()
            }

            BaseProduct().apply {
                this.dealId = mViewModel.dealId
                this.productId = mViewModel.product.value?.productId ?: -1
                this.profileUrl = product?.imageUrls?.get(0) ?: "" // 대표이미지 임시
                this.name = name
                this.brandName = brandName
                this.totalCount = count
                this.totalPrice = price
                this.season = product?.season ?: ""
                this.optionStr = getOptionText(option = selectedOption, count = count)
                this.dealOptionId = if (!isOptionNone) selectedOption!!.dealOptionSelectId.toLong() else null
                this.sellPrice = product?.sellPrice ?: 0
                this.discountPrice = product?.sellPrice ?: 0
            }.let { baseProduct ->
                // 장바구니 API 파라미터
                mViewModel.menuVisibility.set(View.GONE)
                mViewModel.notifyPropertyChanged(BR.menuVisibility)

                Intent(context, PaymentActivity::class.java).let { intent ->
                    intent.putExtra("quantity", count)
                    intent.putExtra("product", baseProduct)
                    intent.putExtra("brandId", mViewModel.product.value?.brandId)
                    intent.putExtra("sellerId", mViewModel.product.value?.sellerId)
                    startActivityForResult(intent, Flag.RequestCode.PAYMENT)
                }
            }
        } else {
            setMenuVisible()
            //   ToastUtil.showMessage(resources.getString(R.string.productdetail_message_selectoption))
        }
    }

    private fun getOptionText(option: OptionInfo?, count: Int): String {
        var optionText = ""
        if (option != null) {
            if (!option.attribute1.isNullOrEmpty())
                optionText = "${option.attribute1}"

            if (!option.attribute2.isNullOrEmpty())
                optionText = "$optionText, ${option.attribute2}"

            if (!option.attribute3.isNullOrEmpty())
                optionText = "$optionText, ${option.attribute3}"
        }

        optionText = if (optionText.isNotEmpty()) "$optionText, ${count}개"
        else "${count}개"

        return optionText
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
                this.productId = mViewModel.product.value?.productId ?: -1
                this.profileUrl = product?.imageUrls?.get(0) ?: "" // 대표이미지 임시
                this.name = name
                this.brandName = brandName
                this.optionMap = optionAttr
                this.totalCount = count
                this.totalPrice = price
                this.season = product?.season ?: ""
                this.sellPrice = product?.sellPrice ?: 0
                this.discountPrice = product?.discountPrice ?: 0
            }.let { baseProduct ->
                // 장바구니 API 파라미터
                baseProduct.dealOptionId = getSelectedOptionDealId() ?: 0
                mViewModel.menuVisibility.set(View.GONE)
                mViewModel.notifyPropertyChanged(BR.menuVisibility)

                Intent(context, PaymentActivity::class.java).let { intent ->
                    intent.putExtra("quantity", getSelectedProductQuantity())
                    intent.putExtra("product", baseProduct)
                    intent.putExtra("brandId", mViewModel.product.value?.brandId)
                    intent.putExtra("sellerId", mViewModel.product.value?.sellerId)
                    startActivityForResult(intent, Flag.RequestCode.PAYMENT)
                }
            }
        } else {
            setMenuVisible()
            ToastUtil.showMessage(resources.getString(R.string.productdetail_message_selectoption))
        }
    }

    override fun getSelectedOptionDealId(): Long? {
        return if (mViewModel.menuVisibility.get() == View.GONE) mHeaderMenuFragment.mViewModel.mSelectedOptionInfo?.dealOptionSelectId?.toLong()
        else mMenuFragment.mViewModel.mSelectedOptionInfo?.dealOptionSelectId?.toLong()
//        return when {
//            mMenuFragment.getSelectedOptionCount() > 0 -> mMenuFragment.getSelectedOptionDealId()
//            mHeaderMenuFragment.getSelectedOptionCount() > 0 -> mHeaderMenuFragment.getSelectedOptionDealId()
//            else -> null
//        }
    }

    override fun getSelectedProductQuantity(): Int {
        return if (mViewModel.menuVisibility.get() == View.VISIBLE) mMenuFragment.getProductCount()
        else mHeaderMenuFragment.getProductCount()
    }

    override fun hideLoadingIndicator() {
        if (::mLoadingIndicatorUtil.isInitialized) mLoadingIndicatorUtil.dismiss()
    }

    override fun closeActivity() {
        //mainListener.removeProductDetailFragment()
        (context as AppCompatActivity).finish()
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
        if (CommonUtil.checkToken()) {
            CommonUtil.startReportActivity(context as Activity, 0, mViewModel.product.value, null)
        } else {
            CustomMessageDialog(message = "로그인 후 이용이 가능합니다.",
                    cancelButtonVisible = true,
                    confirmTask = {
                        CommonUtil.startLoginPage(context as AppCompatActivity)
                    }).show(manager = (context as AppCompatActivity).supportFragmentManager, tag = "CommunityDetailActivity")
        }
    }

    override fun showShoppingBagActivity() {
        CommonUtil.startCartActivity(context as Activity)
    }

    /**
     * @author park jungho
     * get porduct id
     */
    fun getProductId(): Long = mViewModel.product.value!!.productId ?: 0L

    /**
     * 쿠폰 발급 후, 버튼 비활성화
     */
    fun setSaveCouponDisabled() {
        mBinding.includeProductdetailContentheader.linearlayoutProductdetailCoupon.setOnClickListener { }
        mBinding.includeProductdetailContentheader.textviewProductdetailCoupon.setBackgroundResource(R.drawable.coupon_text_disabled)
        mBinding.includeProductdetailContentheader.imageviewProductdetailCoupon.setImageResource(R.drawable.coupon_comlete_disabled)
        mBinding.executePendingBindings()

        // 팔로우 버튼 리셋
        if (::mStoreFragment.isInitialized) mStoreFragment.getSellerBookMark(Type.BookMarkTarget.SELLER.name)
        mViewModel.getSellerBookMark(Type.BookMarkTarget.SELLER.name)
    }

    // 제품 설명 css
    private fun initContent(product: Product) {
        val data = StringBuilder()
        data.append("<style>img{display: inline;height: auto;max-width: 100%;}" +
                "body{word-break: break-all; word-break: break-word}" +
                "h1{font-size:large; word-break: break-all; word-break: break-word}" +
                "h2{font-size:medium; word-break: break-all; word-break: break-word}</style>")
        data.append(product.desc.replace("\"//www", "\"https://www"))
        mBinding.webviewProductdetailContent.settings.apply {
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
        mBinding.webviewProductdetailContent.loadData(data.toString(), "text/html", null)

        /*mBinding.includeProductdetailContentbody.webviewProductdetailContent.settings.apply {
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
        mBinding.includeProductdetailContentbody.webviewProductdetailContent.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                //return super.shouldOverrideUrlLoading(view, request)
                view?.webChromeClient = WebChromeClient()
                if (CustomLog.flag) CustomLog.L("CommunityDetailContentsFragment", request?.url!!.toString())
                return true
            }
        }

        val data = StringBuilder()
        data.append("<HTML><HEAD><LINK href=\"community.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body><style>img{display: inline;height: auto;max-width: 100%;}</style>")
        data.append(product.desc.replace("\"//www", "\"https://www"))
        data.append("</body></HTML>")
        mBinding.includeProductdetailContentbody.webviewProductdetailContent.loadDataWithBaseURL("file:///android_asset/", data.toString(), "text/html; video/mpeg", "utf-8", null)
*/
    }


    private fun sendAnalyticEvent(product: Product) {
        if ((context as BaseActivity).getmTracker() != null) {
            var pInfo = com.google.android.gms.analytics.ecommerce.Product().apply {
                setId(product.dealId.toString())
                setName(product.name)
                setBrand(product.brandName)
                setCustomDimension(0, BuildConfig.BuildType.name)
            }
            var builder = HitBuilders.ScreenViewBuilder().apply {
                addImpression(pInfo, "Product Detail")
            }
            (context as BaseActivity).getmTracker().setScreenName("searchResults")
            (context as BaseActivity).getmTracker().send(builder.build())
        }

        if (getmFirebaseAnalytics() != null) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, product.dealId.toString())
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, product.name)
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, BuildConfig.BuildType.name)
            getmFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
        }
    }


    @SuppressLint("CheckResult")
    private fun setEvenBus() {
        EventBusHelper.mSubject.subscribe { requestCode ->
            when (requestCode.requestCode) {
                RequestCode.CART_BADGE.flag -> {
                    val count = requestCode.data as Int
                    if (count > 0) {
                        mBinding.includeProductdetailHeader.textviewBadge.visibility = View.VISIBLE
                        mBinding.includeProductdetailHeader.textviewBadge.text = count.toString()
                    } else {
                        mBinding.includeProductdetailHeader.textviewBadge.visibility = View.GONE
                    }
                }
            }
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
                if (this.adapter == null) this.adapter = ImagePagerAdapter()
                (this.adapter as ImagePagerAdapter).setItems(list)
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["fade", "visible"])
        fun FrameLayout.fadeInAndOut(fade: Boolean, visible: Boolean = false) {
            if (Preferences.getToken() != null && visible) {
                val animDuration = 600.toLong()
                val fadeIn = ObjectAnimator.ofFloat(this@fadeInAndOut, "alpha", 0f, 1f)
                fadeIn.duration = animDuration
                fadeIn.addListener(
                        onEnd = {
                            val fadeOut = ObjectAnimator.ofFloat(this@fadeInAndOut, "alpha", 1f, 0f)
                            fadeOut.duration = animDuration
                            fadeOut.start()
                        },
                        onStart = { this.visibility = View.VISIBLE })
                fadeIn.start()
            }
        }
    }
}