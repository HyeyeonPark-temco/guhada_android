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
    private val INVALID_PRODUCT_ID: Long = -1
    var mProductId: Long = INVALID_PRODUCT_ID

    override fun getBaseTag(): String = ProductDetailStoreFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.fragment_productdetail_store
    override fun init() {
        if (mProductId > INVALID_PRODUCT_ID) {
            mViewModel = ProductDetailStoreViewModel().apply { this.mCriteria = Criteria().apply { this.productId = this@ProductDetailStoreFragment.mProductId } }
            mViewModel.mRelatedProductList.observe(this, Observer {
                if (mBinding.recyclerviewProductdetailRelated.adapter == null) {
                    mBinding.recyclerviewProductdetailRelated.adapter = ProductDetailStoreAdapter().apply { mList = it.deals }
                    mBinding.executePendingBindings()
                } else {
                    (mBinding.recyclerviewProductdetailRelated.adapter as ProductDetailStoreAdapter).setItems(it.deals)
                }
            })
            mViewModel.getRelatedProductList()
            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }
}