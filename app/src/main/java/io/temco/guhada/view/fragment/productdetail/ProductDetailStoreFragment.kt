package io.temco.guhada.view.fragment.productdetail

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.enum.BookMarkTarget
import io.temco.guhada.data.model.BookMark
import io.temco.guhada.data.model.seller.Criteria
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailStoreViewModel
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailViewModel
import io.temco.guhada.databinding.FragmentProductdetailStoreBinding
import io.temco.guhada.view.activity.SellerInfoActivity
import io.temco.guhada.view.adapter.ProductDetailStoreAdapter
import io.temco.guhada.view.adapter.ProductDetailStoreGridAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

/**
 * 상품 상세- 셀러스토어 (셀러스토어, 연관상품, 추천상품)
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
            mBinding.imageviewProductdetailStoreSeller.setOnClickListener {
                if(mSellerId > INVALID_ID){
                    val intent = Intent(this@ProductDetailStoreFragment.context, SellerInfoActivity::class.java)
                    intent.putExtra("sellerId", mSellerId)
                    startActivity(intent)
                }
            }
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
        // 판매자의 연관 상품
        mViewModel.mRelatedProductList.observe(this, Observer {
            if (it.deals.isEmpty()) {
                mBinding.framelayoutProductdetailRelatedempty.visibility = View.VISIBLE
            } else {
                mBinding.framelayoutProductdetailRelatedempty.visibility = View.GONE
                if (mBinding.recyclerviewProductdetailRelated.adapter == null) {
                    mBinding.recyclerviewProductdetailRelated.adapter = ProductDetailStoreAdapter().apply {
                        this.mList = it.deals
                        this.mViewModel = this@ProductDetailStoreFragment.mViewModel
                    }
                    mBinding.executePendingBindings()
                } else {
                    (mBinding.recyclerviewProductdetailRelated.adapter as ProductDetailStoreAdapter).setItems(it.deals)
                }
            }
        })

        // 추천 상품
        mViewModel.mRecommendProductList.observe(this, Observer {
            if (it.deals.isEmpty()) {
                mBinding.framelayoutProductdetailRecommendempty.visibility = View.VISIBLE
            } else {
                mBinding.framelayoutProductdetailRecommendempty.visibility = View.GONE
                if (mBinding.recyclerviewProductdetailRecommend.adapter == null) {
                    mBinding.recyclerviewProductdetailRecommend.adapter = ProductDetailStoreAdapter().apply {
                        this.mList = it.deals
                        this.mViewModel = this@ProductDetailStoreFragment.mViewModel
                    }
                    mBinding.executePendingBindings()
                } else {
                    (mBinding.recyclerviewProductdetailRecommend.adapter as ProductDetailStoreAdapter).setItems(it.deals)
                }
            }
        })

        // 셀러 스토어 상품
        mViewModel.mSellerProductList.observe(this, Observer {
            if (it.isEmpty()) {
                mBinding.framelayoutProductdetailStoreempty.visibility = View.VISIBLE
            } else {
                mBinding.framelayoutProductdetailStoreempty.visibility = View.GONE
                if (mBinding.recyclerviewProductdetailSellerstore.adapter == null) {
                    mBinding.recyclerviewProductdetailSellerstore.adapter = ProductDetailStoreGridAdapter().apply {
                        this.mList = it
                        this.mIsGridLayout = true
                        this.mSpanCount = 3
                        this.mViewModel = this@ProductDetailStoreFragment.mViewModel
                    }
                    mBinding.executePendingBindings()
                } else {
                    (mBinding.recyclerviewProductdetailSellerstore.adapter as ProductDetailStoreGridAdapter).setItems(it)
                }
            }

        })
    }

    fun setSellerBookMark(bookMark: BookMark) {
        mViewModel.mSellerBookMark = bookMark
        mViewModel.notifyPropertyChanged(BR.mSellerBookMark)
    }

    fun getSellerBookMark(target: String) {
        if(::mViewModel.isInitialized) mViewModel.getSellerLike(target)
    }

    fun getStoreFlagHeight() : Int = (mBinding.productdetailScrollflagRecommend.parent as View).top + mBinding.productdetailScrollflagRecommend.top
}