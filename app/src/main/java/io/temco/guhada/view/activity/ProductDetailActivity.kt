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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Flag.RequestCode.LOGIN
import io.temco.guhada.common.Flag.RequestCode.WRITE_CLAIM
import io.temco.guhada.common.Info
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.data.model.Product
import io.temco.guhada.data.viewmodel.ProductDetailViewModel
import io.temco.guhada.databinding.ActivityProductDetailBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.*
import io.temco.guhada.view.fragment.productdetail.ProductDetailClaimFragment

class ProductDetailActivity : BindActivity<ActivityProductDetailBinding>(), OnProductDetailListener, ProductDetailOptionAttrAdapter.OnSelectAttrListener {
    private lateinit var mViewModel: ProductDetailViewModel
    private lateinit var claimFragment: ProductDetailClaimFragment

    override fun getBaseTag(): String = ProductDetailActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_product_detail
    override fun getViewType(): Type.View = Type.View.PRODUCT_DETAIL

    override fun init() {
        mViewModel = ProductDetailViewModel(this)

        // 임시 productId 12492
        mViewModel.dealId = intent.getIntExtra("productId", resources.getString(R.string.temp_productId).toInt())
        mBinding.includeProductdetailContentheader.recyclerviewProductdetailOption.adapter = ProductDetailOptionAdapter(mViewModel, this)
        mBinding.includeProductdetailMenu.recyclerviewProductdetailMenu.adapter = ProductDetailOptionAdapter(mViewModel, this)

        mViewModel.product.observe(this, Observer<Product> { it ->
            mBinding.includeProductdetailContentbody.recyclerviewProductdetailTag.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            mBinding.includeProductdetailContentinfo.recyclerviewProductdetailInfo.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            mBinding.includeProductdetailContentnotifies.recyclerviewProductdetailNotifies.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            mBinding.includeProductdetailContentheader.recyclerviewProductdetailOption.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            mBinding.includeProductdetailMenu.recyclerviewProductdetailMenu.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

            mBinding.includeProductdetailMenu.viewModel = mViewModel
            mBinding.includeProductdetailContentbody.viewModel = mViewModel
            mBinding.includeProductdetailContentsummary.viewModel = mViewModel
            mBinding.includeProductdetailContentheader.viewModel = mViewModel
            mBinding.includeProductdetailContentbody.viewModel = mViewModel
            mBinding.includeProductdetailContentinfo.viewModel = mViewModel
            mBinding.includeProductdetailContentshipping.viewModel = mViewModel
            mBinding.includeProductdetailContentnotifies.viewModel = mViewModel

            mBinding.includeProductdetailContentbody.webviewProductdetailContent.loadData(it.desc, "text/html", null)
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
        })

        mViewModel.getDetail()
        mBinding.viewModel = mViewModel


        initClaims(resources.getString(R.string.temp_productId).toInt())

        detectButton()
        mBinding.executePendingBindings()
    }

    private fun initClaims(productId: Int) {
        claimFragment = ProductDetailClaimFragment(productId)
        supportFragmentManager.beginTransaction().let {
            it.add(mBinding.framelayoutProductdetailClaim.id, claimFragment)
            it.commit()
        }
    }

    private fun detectButton() {
        val scrollBounds = Rect()
        mBinding.scrollviewProductdetail.viewTreeObserver.addOnScrollChangedListener {
            mBinding.scrollviewProductdetail.getHitRect(scrollBounds)
            if (mBinding.linearlayoutProductdetailBodycontainer.getLocalVisibleRect(scrollBounds)) {
                mViewModel.bottomBtnVisibility = ObservableInt(View.VISIBLE)
            } else {
                mViewModel.bottomBtnVisibility = ObservableInt(View.GONE)
            }
        }
    }

    // OnProductDetailListener
    override fun scrollToElement(pos: Int) {
        var h = 0
        when (pos) {
            0 -> h = (mBinding.productdetailScrollflagContent.parent as View).top + mBinding.productdetailScrollflagContent.top
            1 -> h = (mBinding.productdetailScrollflagQna.parent as View).top + mBinding.productdetailScrollflagQna.top
            2 -> h = (mBinding.productdetailScrollflagStore.parent as View).top + mBinding.productdetailScrollflagStore.top
        }

        mBinding.scrollviewProductdetail.smoothScrollTo(0, h)
    }

    override fun notifyOptionAttrAdapter() {
        (mBinding.includeProductdetailContentheader.recyclerviewProductdetailOption.adapter as ProductDetailOptionAdapter).notifyAttrAdapter()
        (mBinding.includeProductdetailMenu.recyclerviewProductdetailMenu.adapter as ProductDetailOptionAdapter).notifyAttrAdapter()
    }

    override fun onClickAttr(prevSelectedPos: Int, selectedPos: Int) {
        // (mBinding.includeProductdetailContentheader.recyclerviewProductdetailOption.adapter as ProductDetailOptionAdapter).setItemSelected(prevSelectedPos, selectedPos)
        // (mBinding.includeProductdetailMenu.recyclerviewProductdetailMenu.adapter as ProductDetailOptionAdapter).setItemSelected(prevSelectedPos, selectedPos)
    }

    override fun setColorName(optionAttr: ProductDetailOptionAdapter.OptionAttr) {
        (mBinding.includeProductdetailContentheader.recyclerviewProductdetailOption.adapter as ProductDetailOptionAdapter).setItemSelected(optionAttr)
    }

    override fun showMessage(message: String) {
        Toast.makeText(this@ProductDetailActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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

        @JvmStatic
        @BindingAdapter("productOption")
        fun RecyclerView.bindOption(list: List<Product.Option>?) {
            if (list != null && list.isNotEmpty() && this.adapter != null) {
                (this.adapter as ProductDetailOptionAdapter).setItems(list)
            }
        }

        @JvmStatic
        @BindingAdapter("productOptionAttr")
        fun RecyclerView.bindOptionAttr(option: Product.Option?) {
            if (option != null && this.adapter != null) {
                val attrList: MutableList<ProductDetailOptionAdapter.OptionAttr> = ArrayList()

                if (option.type == "COLOR") {
                    for (i in 0 until option.rgb.size) {
                        ProductDetailOptionAdapter.OptionAttr().apply {
                            rgb = option.rgb[i]
                            name = option.attributes[i]
                        }.let {
                            attrList.add(it)
                        }
                    }
                } else {
                    for (attr in option.attributes) {
                        ProductDetailOptionAdapter.OptionAttr().apply {
                            name = attr
                        }.let {
                            attrList.add(it)
                        }
                    }
                }

                (this.adapter as ProductDetailOptionAttrAdapter).setItems(attrList)
            }
        }


    }
}


