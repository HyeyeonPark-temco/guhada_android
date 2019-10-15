package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.View
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Flag.RequestCode.LOGIN
import io.temco.guhada.common.Flag.RequestCode.WRITE_CLAIM
import io.temco.guhada.common.Info
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.common.listener.OnProductDetailMenuListener
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.option.OptionAttr
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailMenuViewModel
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailViewModel
import io.temco.guhada.databinding.ActivityProductDetailBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.ImagePagerAdapter
import io.temco.guhada.view.adapter.productdetail.ProductDetailInfoAdapter
import io.temco.guhada.view.adapter.productdetail.ProductDetailTagAdapter
import io.temco.guhada.view.fragment.productdetail.ProductDetailClaimFragment
import io.temco.guhada.view.fragment.productdetail.ProductDetailMenuFragment
import io.temco.guhada.view.fragment.productdetail.ProductDetailReviewFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 현재 미사용/ ProductDetailFragment 사용중
 * @see ProductFragmentDetailActivity
 * @see io.temco.guhada.view.fragment.productdetail.ProductDetailFragment
 */
class ProductDetailActivity : BindActivity<ActivityProductDetailBinding>(), OnProductDetailListener {
    private val INVALID_DEAL_ID = -1
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    private lateinit var mViewModel: ProductDetailViewModel
    private lateinit var mClaimFragment: ProductDetailClaimFragment
    private lateinit var mMenuFragment: ProductDetailMenuFragment
    private lateinit var mHeaderMenuFragment: ProductDetailMenuFragment
    private lateinit var mReviewFragment: ProductDetailReviewFragment

    override fun getBaseTag(): String = ProductDetailActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_product_detail
    override fun getViewType(): Type.View = Type.View.PRODUCT_DETAIL

    override fun getSelectedProductQuantity(): Int {
        return 0
    }

    override fun getSelectedOptionDealId(): Long? {
        return 0
    }

    override fun redirectLoginActivity() {

    }

    override fun init() {
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        mViewModel = ProductDetailViewModel(this)

        mViewModel.dealId = resources.getString(R.string.temp_productId).toLong()//intent.getIntExtra(Info.INTENT_DEAL_ID, INVALID_DEAL_ID).toLong()
        mViewModel.product.observe(this, Observer<Product> { product ->
            // [상세정보|상품문의|셀러스토어] 탭 상단부, 컨텐츠 웹뷰 먼저 display
            mBinding.includeProductdetailContentsummary.viewModel = mViewModel
            mBinding.includeProductdetailContentheader.viewModel = mViewModel
            mBinding.includeProductdetailContentbody.webviewProductdetailContent.loadData(product.desc, "text/html", null)
            mBinding.includeProductdetailContentheader.viewpagerProductdetailImages.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {
                    mViewModel.imagePos = position + 1
                    mViewModel.notifyPropertyChanged(BR.imagePos)
                }
            })
            if (::mLoadingIndicatorUtil.isInitialized && mLoadingIndicatorUtil.isShowing) mLoadingIndicatorUtil.hide()

            // [상세정보|상품문의|셀러스토어] 탭 하단부 display
            GlobalScope.launch {
                mViewModel.getSellerInfo()
                initOptionMenu()
                initClaims()
                initReview(product)
            }
        })

        if (mViewModel.dealId > INVALID_DEAL_ID) {
            mLoadingIndicatorUtil.execute {
                mViewModel.getDetail()
            }
        }

        mBinding.includeProductdetailContentbody.viewModel = mViewModel
        mBinding.includeProductdetailContentinfo.viewModel = mViewModel
        mBinding.includeProductdetailContentshipping.viewModel = mViewModel
        mBinding.includeProductdetailContentnotifies.viewModel = mViewModel
        mBinding.viewModel = mViewModel
        detectScrollView()

        mBinding.executePendingBindings()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mLoadingIndicatorUtil.isInitialized && mLoadingIndicatorUtil.isShowing) mLoadingIndicatorUtil.hide()
    }

    private fun initClaims() {
        mClaimFragment = ProductDetailClaimFragment().apply {
            this.productId = mViewModel.product.value?.productId ?: 0
        }
        supportFragmentManager.beginTransaction().let {
            it.add(mBinding.framelayoutProductdetailClaim.id, mClaimFragment)
            it.commitAllowingStateLoss()
        }
    }

    private fun initReview(product: Product) {
        mReviewFragment = ProductDetailReviewFragment().apply { this.productId = product.productId }
        mReviewFragment.notifySummary = { averageReviewsRating ->
            mBinding.includeProductdetailContentsummary.averageReviewsRating = averageReviewsRating
            mBinding.executePendingBindings()
        }

//        mReviewFragment.setProductId(productId = product.productId)

        supportFragmentManager.beginTransaction().let {
            it.add(mBinding.framelayoutProductdetailReview.id, mReviewFragment)
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
                Toast.makeText(this@ProductDetailActivity, message, Toast.LENGTH_SHORT).show()

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
//                Toast.makeText(this@ProductDetailActivity, message, Toast.LENGTH_SHORT).show()
            }
        }).apply {
            product = mViewModel.product.value ?: Product()
            this.closeButtonVisibility = View.GONE
        }.let { menuViewModel ->
            mHeaderMenuFragment = ProductDetailMenuFragment().apply { this.mViewModel = menuViewModel }
        }

        supportFragmentManager.beginTransaction().let {
            it.add(mBinding.framelayoutProductdetailMenu.id, mMenuFragment)
            it.add(mBinding.includeProductdetailContentheader.framelayoutProductdetailHeadermenu.id, mHeaderMenuFragment)
            it.commitAllowingStateLoss()
        }
    }

    private fun detectScrollView() {
        val scrollBounds = Rect()

        // Bottom Button
//        mBinding.scrollviewProductdetail.viewTreeObserver.addOnScrollChangedListener {
//            mBinding.scrollviewProductdetail.getHitRect(scrollBounds)
//
//            if (mBinding.linearlayoutProductdetailBodycontainer.getLocalVisibleRect(scrollBounds)) {
//                mViewModel.bottomBtnVisibility = ObservableInt(View.VISIBLE)
//            } else {
//                mViewModel.bottomBtnVisibility = ObservableInt(View.GONE)
//            }
//
//        }
    }

    // 메뉴 이동 탭 [상세정보|상품문의|셀러스토어]
    override fun scrollToElement(pos: Int) {
        var h = 0
        when (pos) {
            0 -> h = (mBinding.productdetailScrollflagContent.parent as View).top + mBinding.productdetailScrollflagContent.top
            1 -> h = (mBinding.productdetailScrollflagQna.parent as View).top + mBinding.productdetailScrollflagQna.top
//            2 -> h = (mBinding.productdetailScrollflagRecommend.parent as View).top + mBinding.productdetailScrollflagRecommend.top
//            2 -> h = (mStoreFragment.getmBinding().productdetailScrollflagRecommend.parent as View).top + mStoreFragment.getmBinding().productdetailScrollflagRecommend.top
        }

        mBinding.scrollviewProductdetail.smoothScrollTo(0, h)
    }

    override fun showMessage(message: String) {
        Toast.makeText(this@ProductDetailActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                WRITE_CLAIM -> {
                    // REFRESH
                    mClaimFragment.refreshClaims()
                }
                LOGIN -> {
                    mClaimFragment.refreshIsMineVisible()
                }
            }
        }
    }

    override fun showMenu() {
        val optionCount = mViewModel.product.value?.options?.size
        val selectedOptionCount = if (mViewModel.menuVisibility.get() == View.VISIBLE) {
            mMenuFragment.getSelectedOptionCount()
        } else {
            mHeaderMenuFragment.getSelectedOptionCount()
        }

        if (selectedOptionCount == optionCount) {
            Toast.makeText(this@ProductDetailActivity, "장바구니 이동", Toast.LENGTH_SHORT).show()
        } else {
            mViewModel.menuVisibility = ObservableInt(View.VISIBLE)
            mViewModel.notifyPropertyChanged(BR.menuVisibility)
        }
    }

    override fun redirectPaymentActivity(isOptionPopupSelected: Boolean) {
        val optionCount = mViewModel.product.value?.options?.size
        val selectedOptionCount = if (mViewModel.menuVisibility.get() == View.VISIBLE) {
            mMenuFragment.getSelectedOptionCount()
        } else {
            mHeaderMenuFragment.getSelectedOptionCount()
        }

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
                this.productId = mViewModel.dealId // 임시
                this.profileUrl = product?.imageUrls?.get(0) ?: "" // 대표이미지 임시
                this.name = name
                this.brandName = brandName
                this.optionMap = optionAttr
                this.totalCount = count
                this.totalPrice = price
                this.sellPrice = product?.sellPrice ?: 0
                this.discountPrice = product?.discountPrice ?: 0
            }.let { baseProduct ->
                // 장바구니 API 파라미터
                var quantity: Int = 1
                when {
                    mMenuFragment.getSelectedOptionCount() > 0 -> {
                        baseProduct.dealOptionId = mMenuFragment.getSelectedOptionDealId() ?: 0
                        quantity = mMenuFragment.getProductCount()
                    }
                    mHeaderMenuFragment.getSelectedOptionCount() > 0 -> {
                        baseProduct.dealOptionId = mHeaderMenuFragment.getSelectedOptionDealId()
                                ?: 0
                        quantity = mHeaderMenuFragment.getProductCount()
                    }
                    else -> baseProduct.dealOptionId = null
                }

                mViewModel.menuVisibility.set(View.GONE)
                mViewModel.notifyPropertyChanged(BR.menuVisibility)
                Intent(this@ProductDetailActivity, PaymentActivity::class.java).let { intent ->
                    intent.putExtra("quantity", quantity)
                    intent.putExtra("product", baseProduct)
                    startActivityForResult(intent, Flag.RequestCode.PAYMENT)
                }
            }

        } else {
            Toast.makeText(this@ProductDetailActivity, resources.getString(R.string.productdetail_message_selectoption), Toast.LENGTH_SHORT).show()
        }
    }

    override fun hideLoadingIndicator() {
        if (mLoadingIndicatorUtil.isShowing) mLoadingIndicatorUtil.hide()
    }

    override fun showSideMenu() {

    }

    override fun closeActivity() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }


    override fun setBrandProductList(brand: Brand) {

    }

    override fun redirectHome() {

    }

    override fun dismissOptionMenu() {
    }

    override fun dismissAddCartResult() {

    }

    override fun showAddCartResult() {
//        AddCartResultFragment.getInstance().show(supportFragmentManager, "addCartResult")
    }

    override fun showSearchWordActivity() {

    }

    override fun showReportActivity() {

    }

    override fun showShoppingBagActivity() {
    }

    companion object {
        @JvmStatic
        fun startActivity(context: Context, id: Int) {
            val intent = Intent(context, ProductDetailActivity::class.java)
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
    }
}


