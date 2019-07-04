package io.temco.guhada.view.fragment.productdetail

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Info
import io.temco.guhada.common.ProductBridge
import io.temco.guhada.common.listener.OnMainListener
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.common.listener.OnProductDetailMenuListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.BaseProduct
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.Product
import io.temco.guhada.data.viewmodel.ProductDetailMenuViewModel
import io.temco.guhada.data.viewmodel.ProductDetailViewModel
import io.temco.guhada.databinding.ActivityProductDetailBinding
import io.temco.guhada.view.activity.PaymentActivity
import io.temco.guhada.view.activity.ProductDetailActivity
import io.temco.guhada.view.adapter.ImagePagerAdapter
import io.temco.guhada.view.adapter.ProductDetailInfoAdapter
import io.temco.guhada.view.adapter.ProductDetailOptionAdapter
import io.temco.guhada.view.adapter.ProductDetailTagAdapter
import io.temco.guhada.view.fragment.base.BaseFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ProductDetailFragment(val dealId: Long, private val mainListener: OnMainListener) : BaseFragment<ActivityProductDetailBinding>(), OnProductDetailListener {
    private val INVALID_DEAL_ID = -1
    private var animFlag = true
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    private lateinit var mViewModel: ProductDetailViewModel
    private lateinit var mClaimFragment: ProductDetailClaimFragment
    private lateinit var mMenuFragment: ProductDetailMenuFragment
    private lateinit var mHeaderMenuFragment: ProductDetailMenuFragment
    private lateinit var mReviewFragment: ProductDetailReviewFragment

    override fun getBaseTag(): String = ProductDetailFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_product_detail

    override fun init() {
        //[2019.06.26]임시 브릿지
        mLoadingIndicatorUtil = LoadingIndicatorUtil(context ?: ProductBridge.mainActivity)
        if (::mLoadingIndicatorUtil.isInitialized)
            mLoadingIndicatorUtil.show()

        mViewModel = ProductDetailViewModel(this)
        mViewModel.dealId = dealId
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
            // this@ProductDetailFragment.hideLoadingIndicator()

            // [상세정보|상품문의|셀러스토어] 탭 하단부 display
            GlobalScope.launch {
                mBinding.includeProductdetailContentbody.viewModel = mViewModel
                mBinding.includeProductdetailContentinfo.viewModel = mViewModel
                mBinding.includeProductdetailContentshipping.viewModel = mViewModel
                mBinding.includeProductdetailContentnotifies.viewModel = mViewModel

                mViewModel.getSellerInfo()
                initOptionMenu()
                initClaims()
                initReview()
            }
        })

        if (mViewModel.dealId > INVALID_DEAL_ID) {
            mLoadingIndicatorUtil.execute {
                mViewModel.getDetail()
            }
        }

        detectScrollView()

        mBinding.includeProductdetailHeader.viewModel = mViewModel
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    override fun redirectHome() {
        mainListener.removeProductFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mLoadingIndicatorUtil.isInitialized && mLoadingIndicatorUtil.isShowing) mLoadingIndicatorUtil.dismiss()
        if (::mClaimFragment.isInitialized) mClaimFragment.onDestroy()
        if (::mMenuFragment.isInitialized) mMenuFragment.onDestroy()
        if (::mHeaderMenuFragment.isInitialized) mHeaderMenuFragment.onDestroy()
        if (::mReviewFragment.isInitialized) mReviewFragment.onDestroy()

        (mBinding.includeProductdetailContentheader.viewpagerProductdetailImages.adapter as ImagePagerAdapter).clearItems()
    }

    /**
     * 상품 문의 View 구성
     * productId 전달
     */
    private fun initClaims() {
        mClaimFragment = ProductDetailClaimFragment(mViewModel.product.value?.productId ?: 0)
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

    private fun initOptionMenu() {
        ProductDetailMenuViewModel(object : OnProductDetailMenuListener {
            override fun setColorName(optionAttr: ProductDetailOptionAdapter.OptionAttr, task: () -> Unit) = task()
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
            mMenuFragment = ProductDetailMenuFragment(menuViewModel)
        }

        ProductDetailMenuViewModel(object : OnProductDetailMenuListener {
            override fun setColorName(optionAttr: ProductDetailOptionAdapter.OptionAttr, task: () -> Unit) = task()
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
            mHeaderMenuFragment = ProductDetailMenuFragment(menuViewModel)
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

    private fun detectScrollView() {
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

    override fun showSideMenu() = ProductBridge.showSideMenu()

    // 메뉴 이동 탭 [상세정보|상품문의|셀러스토어]
    override fun scrollToElement(pos: Int) {
        var h = 0
        when (pos) {
            0 -> h = (mBinding.productdetailScrollflagContent.parent as View).top + mBinding.productdetailScrollflagContent.top
            1 -> h = (mBinding.productdetailScrollflagQna.parent as View).top + mBinding.productdetailScrollflagQna.top
            2 -> h = (mBinding.productdetailScrollflagStore.parent as View).top + mBinding.productdetailScrollflagStore.top
        }

        mBinding.scrollviewProductdetail.smoothScrollTo(0, h)
    }

    override fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun refreshClaims() {
        mClaimFragment.refreshClaims()
    }

    fun refreshIsMyClaimsVisible() {
        mClaimFragment.refreshIsMineVisible()
    }

    override fun showMenu() {
        val optionCount = mViewModel.product.value?.options?.size
        val selectedOptionCount = if (mViewModel.menuVisibility.get() == View.VISIBLE) {
            mMenuFragment.getSelectedOptionCount()
        } else {
            mHeaderMenuFragment.getSelectedOptionCount()
        }

        if (selectedOptionCount == optionCount) {
            ToastUtil.showMessage("장바구니 이동")
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
            val optionAttr: MutableMap<String, ProductDetailOptionAdapter.OptionAttr>
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
                this.season = product?.season ?: ""
            }.let { baseProduct ->
                // 장바구니 API 파라미터
                var quantity: Int = 1
                when {
                    mMenuFragment.getSelectedOptionCount() > 0 -> {
                        baseProduct.dealOptionId = mMenuFragment.getSelectedOptionDealId()
                        quantity = mMenuFragment.getProductCount()
                    }
                    mHeaderMenuFragment.getSelectedOptionCount() > 0 -> {
                        baseProduct.dealOptionId = mHeaderMenuFragment.getSelectedOptionDealId()
                        quantity = mHeaderMenuFragment.getProductCount()
                    }
                    else -> baseProduct.dealOptionId = null
                }

                mViewModel.menuVisibility.set(View.GONE)
                mViewModel.notifyPropertyChanged(BR.menuVisibility)
                Intent(context, PaymentActivity::class.java).let { intent ->
                    intent.putExtra("quantity", quantity)
                    intent.putExtra("product", baseProduct)
                    startActivityForResult(intent, Flag.RequestCode.PAYMENT)
                }
            }

        } else {
            ToastUtil.showMessage("옵션을 선택해주세요.")
        }
    }

    override fun hideLoadingIndicator() {
        if (mLoadingIndicatorUtil.isShowing) mLoadingIndicatorUtil.dismiss()
    }


    override fun closeActivity() {
        ProductBridge.mainActivity.removeProductDetailFragment()
    }

    override fun setBrandProductList(brand: Brand) {
        ProductBridge.mainActivity.setBrandProductList(brand)
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