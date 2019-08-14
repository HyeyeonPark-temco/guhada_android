package io.temco.guhada.view.fragment.productdetail

import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.data.model.seller.Criteria
import io.temco.guhada.data.viewmodel.ProductDetailStoreViewModel
import io.temco.guhada.databinding.FragmentProductdetailStoreBinding
import io.temco.guhada.view.adapter.ProductDetailStoreAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

class ProductDetailStoreFragment : BaseFragment<FragmentProductdetailStoreBinding>() {
    private lateinit var mViewModel: ProductDetailStoreViewModel
    private val INVALID_ID: Long = -1
    var mProductId: Long = INVALID_ID
    var mSellerId: Long = INVALID_ID

    override fun getBaseTag(): String = ProductDetailStoreFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.fragment_productdetail_store
    override fun init() {
        if (mProductId > INVALID_ID && mSellerId > INVALID_ID) {
            mViewModel = ProductDetailStoreViewModel().apply {
                this.mCriteria = Criteria().apply {
                    this.productId = this@ProductDetailStoreFragment.mProductId
                    this.sellerId = this@ProductDetailStoreFragment.mSellerId
                }
            }
            mViewModel.mRelatedProductList.observe(this, Observer {
                if (mBinding.recyclerviewProductdetailRelated.adapter == null) {
                    mBinding.recyclerviewProductdetailRelated.adapter = ProductDetailStoreAdapter().apply { this.mList = it.deals }
                    mBinding.executePendingBindings()
                } else {
                    (mBinding.recyclerviewProductdetailRelated.adapter as ProductDetailStoreAdapter).setItems(it.deals)
                }
            })
            mViewModel.mRecommendProductList.observe(this, Observer {
                if (mBinding.recyclerviewProductdetailRecommend.adapter == null) {
                    mBinding.recyclerviewProductdetailRecommend.adapter = ProductDetailStoreAdapter().apply { this.mList = it.deals }
                    mBinding.executePendingBindings()
                } else {
                    (mBinding.recyclerviewProductdetailRecommend.adapter as ProductDetailStoreAdapter).setItems(it.deals)
                }
            })
            mViewModel.mSellerProductList.observe(this, Observer {
                if (mBinding.recyclerviewProductdetailSellerstore.adapter == null) {
                    mBinding.recyclerviewProductdetailSellerstore.adapter = ProductDetailStoreAdapter().apply {
                        this.mList = it
                        this.mIsGridLayout = true
                        this.mSpanCount = 3
                    }
                    mBinding.executePendingBindings()
                } else {
                    (mBinding.recyclerviewProductdetailSellerstore.adapter as ProductDetailStoreAdapter).setItems(it)
                }
            })

            mViewModel.getRelatedProductList()
            mViewModel.getRecommendProductList()
            mViewModel.getSellerProductList()
            mViewModel.getSellerInfo()

            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }
}