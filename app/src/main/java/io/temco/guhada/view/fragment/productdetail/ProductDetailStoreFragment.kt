package io.temco.guhada.view.fragment.productdetail

import androidx.lifecycle.Observer
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.enum.BookMarkTarget
import io.temco.guhada.data.model.BookMark
import io.temco.guhada.data.model.seller.Criteria
import io.temco.guhada.data.viewmodel.ProductDetailStoreViewModel
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailViewModel
import io.temco.guhada.databinding.FragmentProductdetailStoreBinding
import io.temco.guhada.view.adapter.ProductDetailStoreAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

/**
 * 상품 상세- 셀러스토어
 * @author Hyeyeon Park
 * @since 2019.08.13
 */
class ProductDetailStoreFragment : BaseFragment<FragmentProductdetailStoreBinding>() {
    lateinit var mViewModel: ProductDetailStoreViewModel
    lateinit var mProductDetailViewModel: ProductDetailViewModel
    private val INVALID_ID: Long = -1
    var mProductId: Long = INVALID_ID
    var mSellerId: Long = INVALID_ID

    override fun getBaseTag(): String = ProductDetailStoreFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.fragment_productdetail_store
    override fun init() {
        if (mProductId > INVALID_ID && mSellerId > INVALID_ID) {
            initViewModel()
            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }

    private fun initViewModel() {
        mViewModel = ProductDetailStoreViewModel().apply {
            this.mCriteria = Criteria().apply {
                this.productId = this@ProductDetailStoreFragment.mProductId
                this.sellerId = this@ProductDetailStoreFragment.mSellerId
            }
        }
        mViewModel.notifyProductDetailViewModel = { bookMark ->
            mProductDetailViewModel.mSellerBookMark = bookMark
            mProductDetailViewModel.notifyPropertyChanged(BR.mSellerBookMark)
        }
        serObservers()
        mViewModel.getRelatedProductList()
        mViewModel.getRecommendProductList()
        mViewModel.getSellerProductList()
        mViewModel.getSellerInfo()
        mViewModel.getSellerLike(BookMarkTarget.SELLER.target)
    }

    private fun serObservers() {
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
    }

    fun setSellerBookMark(bookMark: BookMark) {
        mViewModel.mSellerBookMark = bookMark
        mViewModel.notifyPropertyChanged(BR.mSellerBookMark)
    }
}