package io.temco.guhada.view.activity

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.Info
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.data.model.Product
import io.temco.guhada.data.viewmodel.ProductDetailViewModel
import io.temco.guhada.databinding.ActivityProductDetailBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.ProductDetailInfoAdapter
import io.temco.guhada.view.adapter.ProductDetailTagAdapter

class ProductDetailActivity : BindActivity<ActivityProductDetailBinding>() {
    private lateinit var viewModel: ProductDetailViewModel

    override fun getBaseTag(): String = ProductDetailActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_product_detail
    override fun getViewType(): Type.View = Type.View.PRODUCT_DETAIL

    companion object {
        @JvmStatic
        fun startActivity(context: Context, id: Int) {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra(Info.INTENT_DEAL_ID, id)
            context.startActivity(intent)
        }

        @JvmStatic
        @BindingAdapter("productTags")
        fun RecyclerView.bindTags(list: MutableList<String>) {
            if (list.isNotEmpty()) {
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

    override fun init() {
        viewModel = ProductDetailViewModel(object : OnProductDetailListener {
            override fun scrollToElement(pos: Int) {
                var h: Int = 0
                when (pos) {
                    0 -> h = (mBinding.productdetailScrollflagContent.parent as View).top + mBinding.productdetailScrollflagContent.top
                    1 -> h = (mBinding.productdetailScrollflagQna.parent as View).top + mBinding.productdetailScrollflagQna.top
                    2 -> h = (mBinding.productdetailScrollflagStore.parent as View).top + mBinding.productdetailScrollflagStore.top
                }

                mBinding.scrollviewProductdetail.smoothScrollTo(0, h)
            }
        })
        viewModel.dealId = 12492

//        if (intent != null) {
//           // viewModel.dealId = intent.getIntExtra("id", 0)
//            viewModel.dealId = 12492
//        }

        viewModel.product.observe(this, Observer<Product> { it ->
            viewModel.totalPrice = ObservableInt(it.discountPrice)
            mBinding.includeProductdetailContentsummary.viewModel = viewModel
            mBinding.includeProductdetailContentheader.viewModel = viewModel
            mBinding.includeProductdetailContentbody.viewModel = viewModel
            mBinding.includeProductdetailContentinfo.viewModel = viewModel

            mBinding.includeProductdetailContentbody.webviewProductdetailContent.loadData(it.desc, "text/html", null)
        })
        viewModel.getDetail()
        mBinding.viewModel = viewModel
        mBinding.includeProductdetailContentbody.viewModel = viewModel
        mBinding.includeProductdetailContentbody.recyclerviewProductdetailTag.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        mBinding.includeProductdetailContentinfo.recyclerviewProductdetailInfo.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        detectButton()
        mBinding.executePendingBindings()
    }

    private fun detectButton() {
        val scrollBounds = Rect()
        mBinding.scrollviewProductdetail.viewTreeObserver.addOnScrollChangedListener {
            mBinding.scrollviewProductdetail.getHitRect(scrollBounds)
            if (mBinding.linearlayoutProductdetailBodycontainer.getLocalVisibleRect(scrollBounds)) {
                viewModel.bottomBtnVisibility = ObservableInt(View.VISIBLE)
            } else {
                viewModel.bottomBtnVisibility = ObservableInt(View.GONE)
            }
        }
    }

}


