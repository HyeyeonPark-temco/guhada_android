package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.util.Log
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
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.model.BaseProduct
import io.temco.guhada.data.model.Product
import io.temco.guhada.data.model.Token
import io.temco.guhada.data.viewmodel.ProductDetailMenuViewModel
import io.temco.guhada.data.viewmodel.ProductDetailViewModel
import io.temco.guhada.databinding.ActivityProductDetailBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.ImagePagerAdapter
import io.temco.guhada.view.adapter.ProductDetailInfoAdapter
import io.temco.guhada.view.adapter.ProductDetailOptionAdapter
import io.temco.guhada.view.adapter.ProductDetailTagAdapter
import io.temco.guhada.view.fragment.productdetail.ProductDetailClaimFragment
import io.temco.guhada.view.fragment.productdetail.ProductDetailMenuFragment
import io.temco.guhada.view.fragment.productdetail.ProductDetailReviewFragment

class ProductDetailActivity : BindActivity<ActivityProductDetailBinding>(), OnProductDetailListener {
    private lateinit var loadingIndicatorUtil: LoadingIndicatorUtil
    private lateinit var mViewModel: ProductDetailViewModel
    private lateinit var claimFragment: ProductDetailClaimFragment
    private lateinit var menuFragment: ProductDetailMenuFragment
    private lateinit var headerMenuFragment: ProductDetailMenuFragment
    private lateinit var reviewFragment: ProductDetailReviewFragment

    override fun getBaseTag(): String = ProductDetailActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_product_detail
    override fun getViewType(): Type.View = Type.View.PRODUCT_DETAIL

    override fun init() {
        loadingIndicatorUtil = LoadingIndicatorUtil(this)
        mViewModel = ProductDetailViewModel(this)

        // 임시 productId 12492
        mViewModel.dealId = intent.getLongExtra("productId", resources.getString(R.string.temp_productId).toLong())
        mViewModel.product.observe(this, Observer<Product> { product ->
            mBinding.includeProductdetailContentbody.viewModel = mViewModel
            mBinding.includeProductdetailContentsummary.viewModel = mViewModel
            mBinding.includeProductdetailContentheader.viewModel = mViewModel
            mBinding.includeProductdetailContentbody.viewModel = mViewModel
            mBinding.includeProductdetailContentinfo.viewModel = mViewModel
            mBinding.includeProductdetailContentshipping.viewModel = mViewModel
            mBinding.includeProductdetailContentnotifies.viewModel = mViewModel

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

            Log.e("TASK", "PRODUCT FINISH")
            mViewModel.getSellerInfo()
            initMenu()
        })
        loadingIndicatorUtil.execute { mViewModel.getDetail() }

        mBinding.viewModel = mViewModel
        initClaims(resources.getString(R.string.temp_productId).toInt())

        initReview(resources.getString(R.string.temp_productId).toLong())
        detectScrollView()

        mBinding.executePendingBindings()
    }

    private fun initClaims(productId: Int) {
        claimFragment = ProductDetailClaimFragment(productId)
        supportFragmentManager.beginTransaction().let {
            it.add(mBinding.framelayoutProductdetailClaim.id, claimFragment)
            it.commit()
        }
    }

    private fun initReview(productId: Long) {
        reviewFragment = ProductDetailReviewFragment()
        reviewFragment.setProductId(productId = productId)

        supportFragmentManager.beginTransaction().let {
            it.add(mBinding.framelayoutProductdetailReview.id, reviewFragment)
            it.commit()
        }
    }

    private fun initMenu() {
        ProductDetailMenuViewModel(object : OnMenuListener {
            override fun setColorName(optionAttr: ProductDetailOptionAdapter.OptionAttr, task: () -> Unit) = task()
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
            menuFragment = ProductDetailMenuFragment(menuViewModel)
        }

        ProductDetailMenuViewModel(object : OnMenuListener {
            override fun setColorName(optionAttr: ProductDetailOptionAdapter.OptionAttr, task: () -> Unit) = task()
            override fun closeMenu() {
                mViewModel.menuVisibility = ObservableInt(View.GONE)
                mViewModel.notifyPropertyChanged(BR.menuVisibility)
            }

            override fun showMessage(message: String) {
                Toast.makeText(this@ProductDetailActivity, message, Toast.LENGTH_SHORT).show()
            }
        }).apply {
            product = mViewModel.product.value ?: Product()
            this.closeButtonVisibility = View.GONE
        }.let { menuViewModel ->
            headerMenuFragment = ProductDetailMenuFragment(menuViewModel)
        }

        supportFragmentManager.beginTransaction().let {
            it.add(mBinding.framelayoutProductdetailMenu.id, menuFragment)
            it.add(mBinding.includeProductdetailContentheader.framelayoutProductdetailHeadermenu.id, headerMenuFragment)
            it.commit()
            loadingIndicatorUtil.dismiss()
            Log.e("TASK", "MENU FINISH")
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
            2 -> h = (mBinding.productdetailScrollflagStore.parent as View).top + mBinding.productdetailScrollflagStore.top
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
                    claimFragment.refreshClaims()
                }
                LOGIN -> {
                    claimFragment.refreshIsMineVisible()
                }
            }
        }
    }

    override fun showMenu() {
        val optionCount = mViewModel.product.value?.options?.size
        val selectedOptionCount = if (mViewModel.menuVisibility.get() == View.VISIBLE) {
            menuFragment.getSelectedOptionCount()
        } else {
            headerMenuFragment.getSelectedOptionCount()
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
            menuFragment.getSelectedOptionCount()
        } else {
            headerMenuFragment.getSelectedOptionCount()
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
                count = menuFragment.getProductCount()
                price = menuFragment.getTotalPrice()
                optionAttr = menuFragment.getSelectedOptionAttrs()
            } else {
                count = headerMenuFragment.getProductCount()
                price = headerMenuFragment.getTotalPrice()
                optionAttr = headerMenuFragment.getSelectedOptionAttrs()
            }

            BaseProduct().apply {
                this.productId = mViewModel.dealId // 임시
                this.profileUrl = product?.imageUrls?.get(0) ?: "" // 대표이미지 임시
                this.name = name
                this.brandName = brandName
                this.optionMap = optionAttr
                this.totalCount = count
                this.totalPrice = price
            }.let { baseProduct ->
                // 장바구니 API 파라미터
                var quantity: Int = 1
                when {
                    menuFragment.getSelectedOptionCount() > 0 -> {
                        baseProduct.dealOptionId = menuFragment.getSelectedOptionDealId()
                        quantity = menuFragment.getProductCount()
                    }
                    headerMenuFragment.getSelectedOptionCount() > 0 -> {
                        baseProduct.dealOptionId = headerMenuFragment.getSelectedOptionDealId()
                        quantity = headerMenuFragment.getProductCount()
                    }
                    else -> baseProduct.dealOptionId = null
                }

                Intent(this@ProductDetailActivity, PaymentActivity::class.java).let { intent ->
                    intent.putExtra("quantity", quantity)
                    intent.putExtra("product", baseProduct)
                    startActivityForResult(intent, Flag.RequestCode.PAYMENT)
                }
            }

        } else {
            Toast.makeText(this@ProductDetailActivity, "옵션을 선택해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    interface OnMenuListener {
        fun showMessage(message: String)
        fun closeMenu()
        fun setColorName(optionAttr: ProductDetailOptionAdapter.OptionAttr, task: () -> Unit)
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


